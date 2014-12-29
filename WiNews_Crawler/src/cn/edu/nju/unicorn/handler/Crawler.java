package cn.edu.nju.unicorn.handler;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.edu.nju.unicorn.entity.EmptySetException;
import cn.edu.nju.unicorn.entity.ParserPool;
import cn.edu.nju.unicorn.entity.URLSet;
import cn.edu.nju.unicorn.parser.MainParser;
import cn.edu.nju.unicorn.parser.URLFilter;

public class Crawler {
	public static final int DEFAULT_DEPTH = 3;
	public static final int DEFAULT_TIMEOUT = 5000;
	public static final int DEFAULT_EMPTYWAIT = 1000;
	private URLSet urlSet;
	private ParserPool pPool;
	private URLFilter urlFilter = new URLFilter();
	private int depth = DEFAULT_DEPTH;
	private int timeout = DEFAULT_TIMEOUT;
	private int emptyWait = DEFAULT_EMPTYWAIT;
	private boolean shieldExternal = false;

	public Crawler() {
		pPool = ParserPool.getInstance();
		urlSet = URLSet.getInstance();
	}

	public Crawler(URL base) {
		pPool = ParserPool.getInstance();
		urlSet = URLSet.getInstance();
		urlSet.addUrl(base);
	}

	public void addBaseURL(URL base) {
		urlSet.addUrl(base);
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setEmptyWait(int emptyWait) {
		this.emptyWait = emptyWait;
	}

	public void setUrlFilter(URLFilter filter) {
		this.urlFilter = filter;
	}
	
	public void shieldExternalUrl(boolean flag) {
		this.shieldExternal = flag;
	}

	/**
	 * 开始爬取
	 */
	public void start() {
		while (true) {
			if (urlSet.isEmpty()) { // 如果没有找到URL，则等待1秒
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					continue;
				}
			} else {
				// 从Set中取得URL
				URL u = null;
				try {
					u = urlSet.getUrl();
				} catch (EmptySetException e) {
					e.printStackTrace();
					return;
				}
				if (u == null) {
					return;
				}
				// 获取Document
				System.out.println("Fetching: " + u);
				try {
					final Document doc = Jsoup.parse(u, timeout);
					// 新建线程处理Document
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							MainParser p = new MainParser(doc, urlFilter);
							p.shieldExternalUrl(shieldExternal);
							p.process();
						}
					});
					pPool.registerThread(t);
					t.start();
				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
					continue;
				}
			}
		}
	}
}

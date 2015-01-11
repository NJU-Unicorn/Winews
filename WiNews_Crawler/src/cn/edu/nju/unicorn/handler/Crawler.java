package cn.edu.nju.unicorn.handler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.nju.unicorn.data.EmptySetException;
import cn.edu.nju.unicorn.data.ParserPool;
import cn.edu.nju.unicorn.data.URLSet;
import cn.edu.nju.unicorn.parser.MainParser;

public class Crawler {
	public static final int DEFAULT_TIMEOUT = 5000;
	private URLSet urlSet;
	private ParserPool pPool;
	private int timeout = DEFAULT_TIMEOUT;
	private boolean domainLimit = false;

	private ArrayList<String> newsPageUrlRegex = new ArrayList<String>();
	private ArrayList<String> skipUrlRegex = new ArrayList<String>();

	public Crawler() {
		pPool = ParserPool.getInstance();
		urlSet = URLSet.getInstance();
	}

	public Crawler(Properties prop) {
		pPool = ParserPool.getInstance();
		urlSet = URLSet.getInstance();
		urlSet.addUrl(prop.getProperty("root"));
		domainLimit = Boolean.valueOf(prop.getProperty("domain_limit",true+""));
		timeout = Integer.parseInt(prop.getProperty("read_timeout",DEFAULT_TIMEOUT+""));
		String[] newsPageRegex = prop.getProperty("news_page_regex").split(
				"&&&");
		for (int i = 0; i < newsPageRegex.length; i++) {
			newsPageUrlRegex.add(newsPageRegex[i]);
		}
		String[] skipRegex = prop.getProperty("skip_url_regex").split("&&&");
		for (int i = 0; i < skipRegex.length; i++) {
			skipUrlRegex.add(skipRegex[i]);
		}
	}

	private boolean isSkippedUrl(String url) {
		for (String s : skipUrlRegex) {
			if (Pattern.matches(s, url)) {
				return true;
			}
		}
		return false;
	}

	private boolean isNewsPageUrl(String url) {
		for (String s : newsPageUrlRegex) {
			if (Pattern.matches(s, url)) {
				return true;
			}
		}
		return false;
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
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				if (u == null) {
					return;
				}
				// 获取Document
				// System.out.println("Fetching: " + u);
				try {
					final Document doc = Jsoup.parse(u, timeout);

					// 获取其中的链接
					Elements urlElements = doc.select("a");
					URL baseUrl;
					String startUrl = "";
					String sameParentUrl = "";
					try {
						baseUrl = new URL(doc.baseUri());
						startUrl = baseUrl.getProtocol() + "://"
								+ baseUrl.getHost();
						String[] absPathSp = u.toString().replace(startUrl, "")
								.split("/");
						sameParentUrl += startUrl;
						for (int i = 0; i < absPathSp.length - 1; i++) {
							sameParentUrl += absPathSp[i] + "/";
						}
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
					for (Element e : urlElements) {
						String newUrl = e.attr("href");
						if (newUrl.startsWith("/")) {
							newUrl = startUrl + newUrl;
						} else if (!newUrl.startsWith("http://")) {
							newUrl = sameParentUrl + newUrl;
						}
						if (!isSkippedUrl(newUrl)) { // 如果该url不需要跳过
							if (domainLimit) { // 屏蔽外部站点
								if (newUrl.startsWith(startUrl)) {
									URLSet.getInstance().addUrl(newUrl);
								}
							} else {
								URLSet.getInstance().addUrl(newUrl);
							}
						}
					}

					// 如果是包含新闻的链接，新建线程处理Document
					if (isNewsPageUrl(u.toString())) {
						System.out.println("正在处理：" + u);
						Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								MainParser p = new MainParser(doc);
								p.process();
							}
						});
						pPool.registerThread(t);
						t.start();
					}
				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
					continue;
				}
			}
		}
	}
}

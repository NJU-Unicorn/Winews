package cn.edu.nju.unicorn.parser;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.nju.unicorn.entity.ParserPool;
import cn.edu.nju.unicorn.entity.URLSet;

public class MainParser {
	private Document doc = null;
	private URLFilter urlFilter = new URLFilter();
	private boolean shieldExternal = false;

	public MainParser(Document doc) {
		this.doc = doc;
	}

	public MainParser(Document doc, URLFilter urlFilter) {
		this.doc = doc;
		this.urlFilter = urlFilter;
	}
	
	public void shieldExternalUrl(boolean flag) {
		this.shieldExternal = flag;
	}

	public void process() {
		Elements urlElements = doc.select("a");
		URL baseUrl;
		String startUrl = "";
		try {
			baseUrl = new URL(doc.baseUri());
			startUrl = baseUrl.getProtocol() + "://" + baseUrl.getHost();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		for (Element e : urlElements) {
			String newUrl = e.attr("href");
			if (newUrl.startsWith("/")) {
				newUrl = startUrl + newUrl;
			}
			if(urlFilter.filter(newUrl)) {
				try {
					if(shieldExternal) {	// 屏蔽外部站点
						if(newUrl.startsWith(startUrl)) {
							System.out.println("Add: " + newUrl);
							URLSet.getInstance().addUrl(new URL(newUrl));
						}
					} else {
						System.out.println("Add: " + newUrl);
						URLSet.getInstance().addUrl(new URL(newUrl));
					}
				} catch (MalformedURLException e1) {
					System.out.println("Error URL: " + newUrl);
					continue;
				}
			}

		}
		ParserPool.getInstance().closeThread(Thread.currentThread());
	}
}

package cn.edu.nju.unicorn.parser;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.nju.unicorn.entity.ParserPool;

public class MainParser {
	private Document doc = null;
	private URLFilter urlFilter = new URLFilter();;

	public MainParser(Document doc) {
		this.doc = doc;
	}

	public MainParser(Document doc, URLFilter urlFilter) {
		this.doc = doc;
		this.urlFilter = urlFilter;
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
			
			System.out.println(newUrl);

		}
		ParserPool.getInstance().closeThread(Thread.currentThread());
	}
}

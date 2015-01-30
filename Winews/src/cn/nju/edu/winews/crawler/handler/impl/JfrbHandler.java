package cn.nju.edu.winews.crawler.handler.impl;

import java.net.URL;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.handler.WiHandler;
import cn.nju.edu.winews.crawler.handler.filter.WiUrlFilter;
import cn.nju.edu.winews.crawler.handler.parser.ParserFactory;
import cn.nju.edu.winews.crawler.handler.parser.WiParser;
import cn.nju.edu.winews.crawler.handler.parser.ParserFactory.ParserType;

public class JfrbHandler extends WiHandler {

	public JfrbHandler() {
		super("jfrb");
	}

	private JfrbHandler(String sourceID) {
		super("jfrb");
	}

	public void getLinks(URL url) throws Exception {
		System.out.println(url);
		Document doc = Jsoup.parse(url, timeoutMillis);
		Elements links = doc.getElementsByTag("a");
		Elements links2 = doc.getElementsByTag("area");
		links.addAll(links2);
		WiUrlFilter urlFilter = new WiUrlFilter();
		HashSet<URL> urlSet = urlFilter.filter(doc.baseUri(), links);
		for (URL link : urlSet) {
			// Check URL
			String dateStr;
			try {
				dateStr = getDateFromLink(link.toString()).toString();
			} catch (Exception e1) {
				continue;
			}
			if (!DATE_SET.contains(dateStr)) {
				if (!URL_SET.contains(link)) {
					URL_SET.add(link);
					if (Pattern.matches(nodeUrlPattern, link.toString())) {
						try {
							getLinks(link);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					} else if (Pattern.matches(contentUrlPattern,
							link.toString())) {
						System.out.println("Content Link: " + link);
						WiNews news;
						try {
							WiParser parser = ParserFactory.createParser(
									ParserType.CONTENT_PAGE_PARSER, link);
							news = parser.parse(link);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						if (!news.getTitle().equals("")) {
							save(news);
						}
					}
				}
			}
		}
	}
}

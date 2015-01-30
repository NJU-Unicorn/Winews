package cn.nju.edu.winews.crawler.handler;

import java.net.URL;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.handler.filter.WiUrlFilter;
import cn.nju.edu.winews.crawler.handler.parser.ParserFactory;
import cn.nju.edu.winews.crawler.handler.parser.WiParser;

public class SimpleWiHandler extends WiHandler {
	public SimpleWiHandler(String sourceID) {
		super(sourceID);
	}

	public void getLinks(URL url, int depth) throws Exception {
		if (depth > MAX_DEPTH) {
			return;
		}
		Document doc = Jsoup.parse(url, timeoutMillis);
		Elements links = doc.getElementsByTag("a");
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
							System.out.println("Node Link: " + link);
							getLinks(link, depth++);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					} else if (Pattern.matches(contentUrlPattern,
							link.toString())) {
						System.out.println("Content Link: " + link);
						WiNews news;
						try {
							WiParser parser = ParserFactory
									.createSimpleParser(sourceID);
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

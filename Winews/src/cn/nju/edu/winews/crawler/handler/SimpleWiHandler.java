package cn.nju.edu.winews.crawler.handler;

import java.net.URL;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.handler.filter.WiUrlFilter;
import cn.nju.edu.winews.crawler.handler.parser.ParserFactory;
import cn.nju.edu.winews.crawler.handler.parser.WiParser;

public class SimpleWiHandler extends WiHandler {
	public SimpleWiHandler(String sourceID) {
		super(sourceID);
	}

	public void getLinks(URL url, int depth) throws Exception {
		WiDate curDate = getDateFromLink(url.toString());
//		if (depth > MAX_DEPTH) {
//			return;
//		}
		Document doc;
		try {
			doc = Jsoup
					.connect(url.toString())
					.ignoreContentType(true)
					.ignoreHttpErrors(true)
					.timeout(timeoutMillis)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
					.get();
		} catch (Exception e1) {
			Thread.sleep(2000);
			doc = Jsoup
					.connect(url.toString())
					.ignoreContentType(true)
					.ignoreHttpErrors(true)
					.timeout(timeoutMillis)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
					.get();
		}
		Elements links = doc.getElementsByTag("a");
		links.addAll(doc.getElementsByTag("area"));
		WiUrlFilter urlFilter = new WiUrlFilter();
		HashSet<URL> urlSet = urlFilter.filter(doc.baseUri(), links);
		for (URL link : urlSet) {
			// Check URL date
			WiDate linkDate = getDateFromLink(url.toString());
			// 如果页面中发现的链接的日期不晚于页面自身的链接日期
			if (!linkDate.after(curDate)) {
				// 如果该链接没有被爬取过
				if (!mongo.existsUrl(sourceID, link.toString())) {
					mongo.addUrl(sourceID, link.toString()); // 链接加入链接列表
					// 如果是节点链接
					if (Pattern.matches(nodeUrlPattern, link.toString())) {
						try {
							System.out.println("Node Link: " + link);
//							System.out.print(".");
							getLinks(link, depth++);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						// 如果是正文链接
					} else if (Pattern.matches(contentUrlPattern,
							link.toString())) {
						System.out.println("Content Link: " + link);
//						System.out.print(".");
						WiNews news;
						try {
							WiParser parser = ParserFactory
									.createSimpleParser(sourceID);
							news = parser.parse(link);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						// 如果有标题就保存
						if (!news.getTitle().equals("")) {
							mongo.addNews(news);
						}
					}
				}
			}
		}
	}
}

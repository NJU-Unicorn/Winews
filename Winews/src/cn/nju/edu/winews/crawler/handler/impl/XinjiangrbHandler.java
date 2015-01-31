package cn.nju.edu.winews.crawler.handler.impl;

import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.handler.WiHandler;
import cn.nju.edu.winews.crawler.handler.filter.WiUrlFilter;
import cn.nju.edu.winews.crawler.handler.parser.ParserFactory;
import cn.nju.edu.winews.crawler.handler.parser.ParserFactory.ParserType;
import cn.nju.edu.winews.crawler.handler.parser.WiParser;
import cn.nju.edu.winews.crawler.handler.parser.impl.CommonParser;

public class XinjiangrbHandler extends WiHandler {

	private XinjiangrbHandler(String sourceID) {
		super("xinjiangrb");
	}

	public XinjiangrbHandler() {
		super("xinjiangrb");
	}

	@Override
	public void getLinks(URL url, int depth) throws Exception {
		WiDate curDate = getDateFromLink(url.toString());
		Document doc = Jsoup.parse(url, timeoutMillis);
		Elements links = doc.getElementsByTag("a");
		links.addAll(doc.getElementsByTag("area"));
		WiUrlFilter urlFilter = new WiUrlFilter();
		HashSet<URL> urlSet = urlFilter.xinjiangFilter(doc.baseUri(), links);
		for (URL link : urlSet) {
			// Check URL date
			WiDate linkDate = getDateFromLink(url.toString());
			// 如果页面中发现的链接的日期等于页面自身的链接日期
			if (linkDate.equals(curDate)) {
				// 如果该链接没有被爬取过
				if (!mongo.existsUrl(link.toString())) {
					mongo.addUrl(link.toString()); // 链接加入链接列表
					// 如果是节点链接
					if (Pattern.matches(nodeUrlPattern, link.toString())) {
						try {
							System.out.println("Node Link: " + link);
							getLinks(link, depth++);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						// 如果是正文链接
					} else if (Pattern.matches(contentUrlPattern,
							link.toString())) {
						System.out.println("Content Link: " + link);
						WiNews news;
						try {
							WiParser parser = ParserFactory.createParser(
									ParserType.CONTENT_PAGE_PARSER, link);
							news = parser.parse(link);
							news.setLayout(doc.select("#banzhibar>div").first()
									.text().trim());
							news.setDate(CommonParser.formatDate("yyyy-MM-dd", curDate.toString()));
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
	
	public static void main(String[] args) {
		new XinjiangrbHandler().start(new WiDate(new Date()));
	}
}

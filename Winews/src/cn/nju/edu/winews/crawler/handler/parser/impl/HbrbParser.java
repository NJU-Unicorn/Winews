package cn.nju.edu.winews.crawler.handler.parser.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.entity.WiNewsPicture;
import cn.nju.edu.winews.crawler.handler.exception.ParserException;
import cn.nju.edu.winews.crawler.handler.parser.WiParser;

public class HbrbParser implements WiParser {
	private static final String sourceID = "hbrb";
	private static final int timeoutMillis = 5000;

	public WiNews parse(URL url) {
		Document doc;
		try {
			doc = Jsoup.parse(url, timeoutMillis);
		} catch (IOException e1) {
			throw new ParserException("Jsoup error: " + e1.getMessage());
		}
		WiNews news = new WiNews();
		news.setId(CommonParser.getId(sourceID, url.toString()));
		news.setUrl(url);
		news.setSourceID(sourceID);
		news.setSource("河北日报");
		news.setTitle(doc.select("td[width=572]>strong").text().trim());
		String subTitle = "";
		for(Element e: doc.select("td[width=572]>span")) {
			subTitle += e.text().trim() + " ";
		}
		news.setSubTitle(subTitle.trim());
		news.setLayout(doc.select("td[width=208]").text().trim());
		news.setDate(CommonParser.formatDate("", doc.select(".time").text()));
		for (Element e : doc.select("#ozoom p")) {
			String line = e.text().trim().replaceAll("^ *", "")
					.replaceAll(" *$", "")
					+ "\n";
			if (line.length() > 1) {
				news.appendContent(line);
			}
		}
		for (Element e : doc.select("table[width=100%] table[align=center] table[border=0]")) {
			String[] urlSp = url.toString().split("/");
			String rootUrl = url.toString()
					.replace(urlSp[urlSp.length - 1], "");
			String picRelUrl = e.getElementsByTag("img").attr("src");
			while (picRelUrl.startsWith("../")) {
				urlSp = rootUrl.split("/");
				rootUrl = rootUrl.replace(urlSp[urlSp.length - 1] + "/", "");
				picRelUrl = picRelUrl.substring(3);
			}
			String picAbsUrl = rootUrl + picRelUrl;
			WiNewsPicture pic = new WiNewsPicture();
			System.out.println("Picture Link: " + picAbsUrl);
			pic.setNewsId(news.getId());
			try {
				pic.setUrl(new URL(picAbsUrl));
			} catch (MalformedURLException e1) {
				throw new ParserException("URL create error: "
						+ e1.getMessage());
			}
			pic.setComment(e.text().trim().replaceAll("^ *", "")
					.replaceAll(" *$", ""));
			news.addPicture(pic);
		}
		return news;
	}

	
	public static void main(String[] args) throws MalformedURLException {
		WiNews news = new CqrbParser().parse(new URL("http://hbrb.hebnews.cn/html/2014-01/01/content_9022.htm"));
		System.out.println(news);
	}
}

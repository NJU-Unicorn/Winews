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

public class TjrbParser implements WiParser {
	private static final String sourceID = "tjrb";
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
		news.setSource("天津日报");
		news.setTitle(doc.select(".font01").text().trim().replace("(图)", "")
				.replace("（图）", ""));
		news.setSubTitle(doc.select(".font02").first().text().trim()
				+ doc.select(".font02").get(1).text().trim());

		news.setLayout(doc.select("td[width=145]").text().trim());
		news.setDate(doc.select("td[width=47%]").text().trim());
		for (Element e : doc.select("#ozoom p")) {
			String line = e.text().trim().replaceAll("^ *", "")
					.replaceAll("^　*", "").replaceAll(" *$", "")
					+ "\n";
			if (line.length() > 1) {
				news.appendContent(line);
			}
		}
		for (Element e : doc.select("table[bgcolor=#efefef]>tbody")) {
			String[] urlSp = url.toString().split("/");
			String rootUrl = url.toString()
					.replace(urlSp[urlSp.length - 1], "");
			String picRelUrl = e.getElementsByTag("img").attr("src");
			while (picRelUrl.startsWith("../")) {
				urlSp = rootUrl.split("/");
				rootUrl = rootUrl.replaceAll(urlSp[urlSp.length - 1] + "/$", "");
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
			pic.setComment(doc.select(".font02").get(2).text().trim());
			news.addPicture(pic);
		}
		return news;
	}
}

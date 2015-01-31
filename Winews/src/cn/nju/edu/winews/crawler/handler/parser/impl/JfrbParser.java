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

public class JfrbParser implements WiParser {
	private static final String sourceID = "jfrb";
	private static final int timeoutMillis = 5000;
	
	public WiNews parse(URL url) {
		Document doc;
		try {
			doc = Jsoup.parse(url, timeoutMillis);
		} catch (IOException e1) {
			throw new ParserException("Jsoup error: " + e1.getMessage());
		}
		WiNews news = new WiNews();
		news.setUrl(url);
		news.setSourceID(sourceID);
		news.setSource("解放日报");
		news.setTitle(doc.select(".title h1").text().trim());
		String subTitle = "";
		for(Element e: doc.select(".title h3")) {
			subTitle+=e.text() + " ";
		}
		news.setSubTitle(subTitle.trim());
		String dateAndTitle = doc.select(".title h5").text();
		String[] datSp = dateAndTitle.split(" ");
		news.setDate(CommonParser.formatDate("yyyy年MM月dd日",datSp[0].trim()));
		news.setLayout(datSp[1].trim().replace(" ", ""));
		
		news.appendContent(doc.select(".content").html().replace("&nbsp;", "").replace("<br>", "").replaceAll("　", "").trim());
		for (Element e : doc.select("#coin-slider a")) {
			
			String[] urlSp = url.toString().split("/");
			String rootUrl = url.toString()
					.replace(urlSp[urlSp.length - 1], "");
			String picRelUrl = e.getElementsByTag("img").attr("src").replace("\\", "/");
			while (picRelUrl.startsWith("../")) {
				urlSp = rootUrl.split("/");
				rootUrl = rootUrl.replace(urlSp[urlSp.length - 1] + "/", "");
				picRelUrl = picRelUrl.substring(3);
			}
			String picAbsUrl = rootUrl + picRelUrl;
			WiNewsPicture pic = new WiNewsPicture();
			System.out.println("Picture Link: " + picAbsUrl);
			pic.setNewsUrl(news.getUrl());
			try {
				pic.setUrl(new URL(picAbsUrl));
			} catch (MalformedURLException e1) {
				throw new ParserException("URL create error: "
						+ e1.getMessage());
			}
			pic.setComment(e.text().trim().replaceAll("^　*", "")
					.replaceAll(" *$", ""));
			news.addPicture(pic);
		}
		return news;
	}
	
	public static void main(String[] args) throws MalformedURLException {
		WiNews news = new JfrbParser().parse(new URL("http://newspaper.jfdaily.com/jfrb/html/2014-07/31/content_1180610.htm"));
		System.out.println(news);
	}
}

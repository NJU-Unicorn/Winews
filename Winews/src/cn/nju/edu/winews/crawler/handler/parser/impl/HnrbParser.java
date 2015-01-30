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

public class HnrbParser implements WiParser {
	private static final String sourceID = "hnrb";
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
		news.setSource("河南日报");
		news.setTitle(doc.select(".font01").text().trim());
		String subTitle = "";
		for(Element e: doc.select(".font02")) {
			subTitle += e.text().trim() + " ";
		}
		news.setSubTitle(subTitle.trim());
		news.setLayout(doc.select("td[width=160]").text().trim());
		news.setDate(CommonParser.formatDate("yyyy年MM月dd日", doc.select(".dSearch>strong").text()));
		for (Element e : doc.select("#ozoom p")) {
			String line = e.text().trim().replaceAll("^ *", "")
					.replaceAll(" *$", "")
					+ "\n";
			if (line.length() > 1) {
				news.appendContent(line);
			}
		}
		for (Element e : doc.select(".dContents img")) {
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
			pic.setNewsId(news.getId());
			try {
				pic.setUrl(new URL(picAbsUrl));
			} catch (MalformedURLException e1) {
				throw new ParserException("URL create error: "
						+ e1.getMessage());
			}
			if(doc.select(".dContents img").indexOf(e) == 0) {
				pic.setComment(doc.select(".dContents>p").text().trim().replaceAll("^ *", "")
						.replaceAll(" *$", ""));
			} else {
				pic.setComment(doc.select(".picdes").get(doc.select(".dContents img").indexOf(e)).text().trim().replaceAll("^ *", "")
						.replaceAll(" *$", ""));
			}
			System.out.println("Picture Link: " + picAbsUrl + "("+pic.getComment()+")");
			news.addPicture(pic);
		}
		return news;
	}
	
	public static void main(String[] args) throws MalformedURLException {
		WiNews news = new HnrbParser().parse(new URL("http://newpaper.dahe.cn/hnrb/html/2015-01/30/content_1219014.htm?div=1"));
		System.out.println(news);
	}

}

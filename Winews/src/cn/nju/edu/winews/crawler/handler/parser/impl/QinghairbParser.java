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

public class QinghairbParser implements WiParser{
	private static final String sourceID = "qinghairb";
	private static final int timeoutMillis = 5000;
	public static void main(String[] args) throws MalformedURLException {
		// TODO Auto-generated method stub
		WiNews news = new QinghairbParser().parse(new URL("http://epaper.tibet3.com/qhrb/html/2014-12/21/content_200221.htm"));
		System.out.println(news);

	}
	@Override
	public WiNews parse(URL url) {
		// TODO Auto-generated method stub
		Document doc;
		try {
			doc = Jsoup.parse(url, timeoutMillis);
		} catch (IOException e1) {
			throw new ParserException("Jsoup error: " + e1.getMessage());
		}
		WiNews news = new WiNews();
		news.setUrl(url);
		news.setSourceID(sourceID);
		news.setSource("青海日报");
		news.setTitle(doc.select("td.font01").text().trim());
		String subTitle = "";
		for(Element e: doc.select("td.font02")) {
			subTitle += e.text().trim() + " ";
		}
		news.setSubTitle(subTitle.trim());
		news.setLayout(doc.select("td[width=145]").text().trim());
		news.setDate(doc.select("span.default").text().replace(" ", "").replace("　", " ").trim());
		for (Element e : doc.select("div#ozoom p")) {
			String line = e.text().trim().replaceAll("^ *", "")
					.replaceAll(" *$", "")
					+ "\n";
			if (line.length() > 1) {
				news.appendContent(line);
			}
		}
		for (Element e : doc.select("table[bgcolor=#efefef] table")) {
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
			pic.setNewsUrl(news.getUrl());
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
	

}

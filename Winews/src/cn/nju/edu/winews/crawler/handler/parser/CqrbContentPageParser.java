package cn.nju.edu.winews.crawler.handler.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.entity.WiNewsPicture;
import cn.nju.edu.winews.crawler.handler.exception.ParserException;

public class CqrbContentPageParser implements WiParser {
	private static final String sourceID = "cqrb";
	private static final int timeoutMillis = 5000;

	public WiNews parse(URL url) {
		Document doc;
		try {
			doc = Jsoup.parse(url, timeoutMillis);
		} catch (IOException e1) {
			throw new ParserException("Jsoup error: " + e1.getMessage());
		}
		WiNews news = new WiNews();
		news.setId(getId(url.toString()));
		news.setUrl(url);
		news.setSourceID(sourceID);
		news.setSource("重庆日报");
		news.setTitle(doc.select("td[width=572]>strong").text().trim());
		String subTitle = "";
		for(Element e: doc.select("td[width=572]>span")) {
			subTitle += e.text().trim() + " ";
		}
		news.setSubTitle(subTitle.trim());
		news.setLayout(doc.select("td[width=208]").text().trim());
		news.setDate(doc.select("td[width=268]").text().replace(" ", "").replace("　", " ").trim());
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

	private String getId(String urlStr) {
		String[] urlSp = urlStr.split("/");
		String fileName = urlSp[urlSp.length - 1].split("\\.")[0];
		Matcher m = Pattern.compile("[0-9]+").matcher(fileName);
		if (m.find()) {
			String id = sourceID + "_" + m.group();
			return id;
		} else {
			throw new ParserException("Can't find the id of news url: "
					+ urlStr);
		}
	}
	
	public static void main(String[] args) throws MalformedURLException {
		WiNews news = new CqrbContentPageParser().parse(new URL("http://cqrbepaper.cqnews.net/cqrb/html/2015-01/29/content_1813932.htm"));
		System.out.println(news);
	}
}

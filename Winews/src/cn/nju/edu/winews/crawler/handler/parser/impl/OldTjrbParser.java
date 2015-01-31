package cn.nju.edu.winews.crawler.handler.parser.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.entity.WiNewsPicture;
import cn.nju.edu.winews.crawler.handler.exception.ParserException;
import cn.nju.edu.winews.crawler.handler.parser.WiParser;

public class OldTjrbParser implements WiParser {
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
		news.setUrl(url);
		news.setSourceID(sourceID);
		news.setSource("天津日报");
		news.setTitle(doc.select(".title_table tr").get(1).text().trim()
				.replace("(图)", "").replace("（图）", ""));
		news.setSubTitle(doc.select(".title_table tr").first().text().trim()
				+ doc.select(".title_table tr").get(2).text().trim());
		news.setLayout(doc.select("td[width=51%]").text().trim());
		String date = doc.select("td[height=35]").text().trim();
		String formatDate = null;
		Matcher m = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}").matcher(date);
		if (m.find()) {
			date = m.group();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d = null;
			try {
				d = sdf.parse(date);
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日 E");
				formatDate = sdf2.format(d);
			} catch (ParseException e1) {
				formatDate = null;
			}
		}
		if (formatDate != null) {
			news.setDate(formatDate);
		}
		for (Element e : doc.select("#fontzoom p")) {
			String line = e.text().trim().replaceAll("^ *", "")
					.replaceAll("^　*", "").replaceAll(" *$", "")
					+ "\n";
			if (line.length() > 1) {
				news.appendContent(line);
			}
		}
		for (Element e : doc.select("table[bgcolor=#efefef]>tbody tbody")) {
			String[] urlSp = url.toString().split("/");
			String rootUrl = url.toString()
					.replace(urlSp[urlSp.length - 1], "");
			String picRelUrl = e.getElementsByTag("img").attr("src");
			while (picRelUrl.startsWith("../")) {
				urlSp = rootUrl.split("/");
				rootUrl = rootUrl
						.replaceAll(urlSp[urlSp.length - 1] + "/$", "");
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
			pic.setComment(doc.select(".title_table tr").get(3).text().trim());
			news.addPicture(pic);
		}
		return news;
	}

}
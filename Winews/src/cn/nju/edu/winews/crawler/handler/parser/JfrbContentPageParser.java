package cn.nju.edu.winews.crawler.handler.parser;

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

public class JfrbContentPageParser implements WiParser {
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
		news.setId(getId(url.toString()));
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
		news.setDate(formatDate(datSp[0].trim()));
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
			pic.setNewsId(news.getId());
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
	
	private String formatDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		try {
			Date d = sdf.parse(date);
			sdf = new SimpleDateFormat("yyyy年MM月dd日 E");
			return sdf.format(d);
		} catch (ParseException e) {
			return date;
		}
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
}

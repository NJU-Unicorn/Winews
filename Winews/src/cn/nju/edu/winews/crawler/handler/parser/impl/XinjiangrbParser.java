package cn.nju.edu.winews.crawler.handler.parser.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.entity.WiNewsPicture;
import cn.nju.edu.winews.crawler.handler.exception.ParserException;
import cn.nju.edu.winews.crawler.handler.parser.WiParser;

public class XinjiangrbParser implements WiParser{

	public WiNews parse(URL url) {
		Document doc;
		try {
			doc = Jsoup.parse(url, 5000);
		} catch (IOException e1) {
			throw new ParserException("Jsoup error("+url+"): " + e1.getMessage());
		}
		WiNews news = new WiNews();
		news.setUrl(url);
		news.setSourceID("xinjiangrb");
		news.setSource("新疆日报");
		news.setTitle(doc.select("#title").text().trim());
		String subTitle = "";
		for (Element e : doc.select("#leadTitle,#title1")) {
			subTitle += e.text().trim() + " ";
		}
		news.setSubTitle(subTitle.trim());
		for (Element e : doc.select("#content p")) {
			if(!e.getElementsByTag("br").isEmpty()) {
				String raw = e.html();
				// 把br替换为换行符
				HashSet<String> brTypes = new HashSet<String>();
				for(Element bre: e.getElementsByTag("br")) {
					brTypes.add(bre.toString());
				}
				for(String brStr:brTypes) {
					raw = raw.replace(brStr, "[BREnter]");
				}
				Document newDoc = Jsoup.parse(raw);
				String[] lines = newDoc.text().split("\\[BREnter\\]");
				for(int i = 0; i < lines.length;i++) {
					String line = lines[i].trim().replaceAll("^( |　)*", "")
							.replaceAll("( |　)*$", "").replace("　　", "\n")
							+ "\n";
					if (line.length() > 1) {
						news.appendContent(line);
					}
				}
			} else {
				String line = e.text().trim().replaceAll("^( |　)*", "")
						.replaceAll("( |　)*$", "").replace("　　", "\n")
						+ "\n";
				if (line.length() > 1) {
					news.appendContent(line);
				}
			}
		}
		Matcher m = Pattern.compile("var pics = '.*';").matcher(doc.toString());
		if(m.find()) {
			String jsCode = m.group();
			String[] location = jsCode.split("'")[1].split("\\|");
			for(int i = 0; i < location.length; i++) {
				if(location[i].equals("")) {
					continue;
				}
				String picUrl = "http://epaper.xjdaily.com/" + location[i];
				WiNewsPicture pic = new WiNewsPicture();
				try {
					pic.setUrl(new URL(picUrl));
				} catch (MalformedURLException e1) {
					throw new ParserException("URL create error: "
							+ e1.getMessage());
				}
				pic.setNewsUrl(news.getUrl());
				pic.setComment("");
				System.out.println("Picture Link: " + picUrl + "("
						+ pic.getComment() + ")");
				news.addPicture(pic);
			}
		}
		return news;
	}
}

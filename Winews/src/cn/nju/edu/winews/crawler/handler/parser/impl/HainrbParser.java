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

public class HainrbParser implements WiParser{
	private static final String sourceID = "hainanrb";
	private static final int timeoutMillis = 5000;
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
		news.setSource("海南日报");
		news.setTitle(doc.select("founder-title>p").text().trim());
		String subTitle = "";
		for(Element e: doc.select("td.font02")) {
			subTitle += e.text().trim() + " ";
		}
		news.setSubTitle(subTitle.trim());
		//?????
		news.setLayout(doc.select("td[width=147]").text().trim());
		news.setDate(doc.select("span.default").text());
		
		//??????
		for (Element e : doc.select("founder-content")) {
			String line = e.text().trim().replaceAll("^ *", "")
					.replaceAll(" *$", "")
					+ "\n";
			line=line.replaceAll("^ 　　*", "/n");
			if (line.length() > 1) {
				news.appendContent(line);
			}
		}
		for (Element e : doc.select("table#newspic table[align=center]")) {
			if(e.getElementsByTag("img").isEmpty()) {
				continue;
			}
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
			pic.setNewsUrl(news.getUrl());
			try {
				pic.setUrl(new URL(picAbsUrl));
			} catch (MalformedURLException e1) {
				throw new ParserException("URL create error: "
						+ e1.getMessage());
			}
			pic.setComment(e.getElementsByTag("a").attr("title").replaceAll("^( | )*", "")
					.replaceAll("( | )*$", ""));
			System.out.println("Picture Link: " + picAbsUrl + "("+pic.getComment()+")");
			news.addPicture(pic);
		}
		return news;
	}
	public static void main(String[] args) throws MalformedURLException {
		WiNews news = new HainrbParser().parse(new URL("http://hnrb.hinews.cn/html/2014-12/21/content_1_3.htm"));
		System.out.println(news);
	}
	

}

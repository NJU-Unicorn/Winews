package cn.nju.edu.winews.crawler.handler.parser.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.entity.WiNewsPicture;
import cn.nju.edu.winews.crawler.handler.exception.ParserException;
import cn.nju.edu.winews.crawler.handler.parser.WiParser;

public class NingxiarbParser implements WiParser{
	private static final String sourceID = "ningxiarb";
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
		news.setId(CommonParser.getId(sourceID, url.toString()));
		news.setUrl(url);
		news.setSourceID(sourceID);
		news.setSource("宁夏日报");
		news.setTitle(doc.select("span.listblackfont20h30heiti").text().trim());
		String subTitle = "";
		for(Element e: doc.select("span.listblackfont14h30")) {
			subTitle += e.text().trim() + " ";
		}
		news.setSubTitle(subTitle.trim());
		//?????
		news.setLayout(doc.select("td.listbluefont12h18[width=37%]").text().trim());
		news.setDate(doc.select("td.Navwhite14[width=215]").text());
		
		//??????
		Elements es=doc.select("div#ozoom p");
		for (Element e : es) {
			String line = e.text().trim().replaceAll("^ *", "")
					.replaceAll(" *$", "")
					+ "\n";
			line=line.replaceAll("^ 　　*", "/n");
			if (line.length() > 1) {
				news.appendContent(line);
			}
			
		}
		for (Element e : doc.select("table[bgcolor=#DFDFDF] table[align=center]")) {
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
		WiNews news = new NingxiarbParser().parse(new URL("http://sz.nxnews.net/nxrb/html/2013-07/01/content_386331.htm"));
		System.out.println(news);
	}
	

}

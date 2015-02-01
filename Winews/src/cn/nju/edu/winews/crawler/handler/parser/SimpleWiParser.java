package cn.nju.edu.winews.crawler.handler.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.nju.edu.winews.crawler.data.PropertiesHelper;
import cn.nju.edu.winews.crawler.data.exception.ConfigIOException;
import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.entity.WiNewsPicture;
import cn.nju.edu.winews.crawler.handler.WiHandler;
import cn.nju.edu.winews.crawler.handler.exception.ConfigException;
import cn.nju.edu.winews.crawler.handler.exception.ParserException;
import cn.nju.edu.winews.crawler.handler.parser.impl.CommonParser;

public class SimpleWiParser implements WiParser {
	public static final String SOURCE_NAME_KEY = "source_name";
	public static final String TITLE_SELECTOR_KEY = "title_selector";
	public static final String SUBTITLE_SELECTOR_KEY = "subtitle_selector";
	public static final String LAYOUT_SELECTOR_KEY = "layout_selector";
	public static final String DATE_PATTERN_KEY = "date_pattern";
	public static final String DATE_FORMAT_KEY = "date_format";
	public static final String CONTENT_SELECTOR_KEY = "content_selector";
	public static final String PICTURE_SELECTOR_KEY = "picture_selector";
	public static final String UNKNOWN_VALUE = "NULL";

	protected String sourceID;
	protected String source;

	protected int timeoutMillis;
	protected String titleSelector;
	protected String subTitleSelector;
	protected String layoutSelector;
	protected String datePattern;
	protected String dateFormat;
	protected String contentSelector;
	protected String pictureSelector;

	public SimpleWiParser(String sourceID) {
		this.sourceID = sourceID;
		loadConf();
	}

	public WiNews parse(URL url) {
		Document doc;
		try {
			doc = Jsoup
					.connect(url.toString())
					.ignoreContentType(true)
					.ignoreHttpErrors(true)
					.timeout(timeoutMillis)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
					.get();
		} catch (IOException e1) {
			throw new ParserException("Jsoup error(" + url + "): "
					+ e1.getMessage());
		}
		WiNews news = new WiNews();
		news.setUrl(url);
		news.setSourceID(sourceID);
		news.setSource(source);
		news.setTitle(doc.select(titleSelector).first().text().trim());
		String subTitle = "";
		for (Element e : doc.select(subTitleSelector)) {
			subTitle += e.text().trim() + " ";
		}
		news.setSubTitle(subTitle.trim().replace(news.getTitle(), " "));
		news.setLayout(doc.select(layoutSelector).text().trim());
		String dateStr = CommonParser.getDateFromLink(datePattern, dateFormat,
				url.toString()).toString();
		news.setDate(CommonParser.formatDate("yyyy-MM-dd", dateStr));
		for (Element e : doc.select(contentSelector)) {
			if (!e.getElementsByTag("br").isEmpty()) {
				String raw = e.html();
				// 把br替换为换行符
				HashSet<String> brTypes = new HashSet<String>();
				for (Element bre : e.getElementsByTag("br")) {
					brTypes.add(bre.toString());
				}
				for (String brStr : brTypes) {
					raw = raw.replace(brStr, "[BREnter]");
				}
				Document newDoc = Jsoup.parse(raw);
				String[] lines = newDoc.text().split("\\[BREnter\\]");
				for (int i = 0; i < lines.length; i++) {
					String line = lines[i].trim().replaceAll("^( |　)*", "")
							.replaceAll("( |　)*$", "").replace("　　", "\n").trim()
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
		for (Element e : doc.select(pictureSelector)) {
			if (e.getElementsByTag("img").isEmpty()) {
				continue;
			}
			String picRelUrl = e.getElementsByTag("img").attr("src");
			WiNewsPicture pic = new WiNewsPicture();
			try {
				URL picUrl = new URL(picRelUrl);
				pic.setUrl(picUrl);
			} catch (MalformedURLException e2) {
				String picAbsUrl;
				if (picRelUrl.startsWith("/")) {
					picAbsUrl = url.getProtocol()+"://" + url.getHost() +picRelUrl;
				} else {
					String[] urlSp = url.toString().split("/");
					String rootUrl = url.toString().replace(
							urlSp[urlSp.length - 1], "");
					while (picRelUrl.startsWith("../")) {
						urlSp = rootUrl.split("/");
						rootUrl = rootUrl.replace(
								urlSp[urlSp.length - 1] + "/", "");
						picRelUrl = picRelUrl.substring(3);
					}
					picAbsUrl = rootUrl + picRelUrl;
				}
				pic.setNewsUrl(news.getUrl());
				try {
					pic.setUrl(new URL(picAbsUrl));
				} catch (MalformedURLException e1) {
					throw new ParserException("URL create error: "
							+ e1.getMessage());
				}
			}
			pic.setComment(e.text().trim().replaceAll("^ *", "")
					.replaceAll(" *$", ""));
			if(pic.getComment().equals("")) {
				pic.setComment(e.getElementsByAttribute("data-title").attr("data-title"));
			}
			if(pic.getComment().equals("")) {
				pic.setComment(e.getElementsByAttribute("title").attr("title"));
			}
			System.out.println("Picture Link: " + pic.getUrl() + "("
					+ pic.getComment() + ")");
			// System.out.print(".");
			news.addPicture(pic);
		}
		return news;
	}

	protected void loadConf() {
		PropertiesHelper propHelper = new PropertiesHelper();
		Properties conf;
		try {
			conf = propHelper.getConf(sourceID);
		} catch (ConfigIOException e) {
			throw new ConfigException(e.getMessage());
		}
		timeoutMillis = Integer.parseInt(conf.getProperty(
				WiHandler.TIMEOUT_MILLIS_KEY, ""
						+ WiHandler.DEFAULT_TIMEOUT_MILLIS));
		source = conf.getProperty(SimpleWiParser.SOURCE_NAME_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		titleSelector = conf.getProperty(SimpleWiParser.TITLE_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		subTitleSelector = conf.getProperty(
				SimpleWiParser.SUBTITLE_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		layoutSelector = conf.getProperty(SimpleWiParser.LAYOUT_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		datePattern = conf.getProperty(SimpleWiParser.DATE_PATTERN_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		dateFormat = conf.getProperty(SimpleWiParser.DATE_FORMAT_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		contentSelector = conf.getProperty(SimpleWiParser.CONTENT_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		pictureSelector = conf.getProperty(SimpleWiParser.PICTURE_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);

		if (titleSelector.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| source.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| subTitleSelector.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| layoutSelector.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| datePattern.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| dateFormat.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| contentSelector.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| pictureSelector.equals(SimpleWiParser.UNKNOWN_VALUE)) {
			throw new ConfigException("Congif file is incomplete!");
		}
	}

	public static void main(String[] args) throws MalformedURLException {
		WiParser p = new SimpleWiParser("guizhourb");
		WiNews news = p.parse(new URL(
				"http://58.42.249.98/epaper/gzrb/Content/20141221/Articel01001WD.htm"));
		System.out.println(news);
	}
}

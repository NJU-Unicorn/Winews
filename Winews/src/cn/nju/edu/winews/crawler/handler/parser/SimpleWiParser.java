package cn.nju.edu.winews.crawler.handler.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
	public static final String DATE_SELECTOR_KEY = "date_selector";
	public static final String DATE_FORMAT_KEY = "content_date_format";
	public static final String CONTENT_SELECTOR_KEY = "content_selector";
	public static final String PICTURE_SELECTOR_KEY = "picture_selector";
	public static final String UNKNOWN_VALUE = "NULL";

	protected String sourceID;
	protected String source;

	protected int timeoutMillis;
	protected String titleSelector;
	protected String subTitleSelector;
	protected String layoutSelector;
	protected String dateSelector;
	protected String dateFormat;
	protected String contentSelector;
	protected String pictureSelector;

	public SimpleWiParser(String sourceID, String sourceName) {
		this.sourceID = sourceID;
		this.source = sourceName;
		loadConf();
	}

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
		news.setSource(source);
		news.setTitle(doc.select(titleSelector).text().trim());
		String subTitle = "";
		for(Element e: doc.select(subTitleSelector)) {
			subTitle += e.text().trim() + " ";
		}
		news.setSubTitle(subTitle.trim());
		news.setLayout(doc.select(layoutSelector).text().trim());
		news.setDate(CommonParser.formatDate(dateFormat, doc.select(dateSelector).text()));
		for (Element e : doc.select(contentSelector)) {
			String line = e.text().trim().replaceAll("^ *", "")
					.replaceAll(" *$", "")
					+ "\n";
			if (line.length() > 1) {
				news.appendContent(line);
			}
		}
		for (Element e : doc.select(pictureSelector)) {
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
			pic.setComment(e.text().trim().replaceAll("^ *", "")
					.replaceAll(" *$", ""));
			System.out.println("Picture Link: " + picAbsUrl +"("+pic.getComment()+")");
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
		titleSelector = conf.getProperty(SimpleWiParser.TITLE_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		subTitleSelector = conf.getProperty(
				SimpleWiParser.SUBTITLE_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		layoutSelector = conf.getProperty(SimpleWiParser.LAYOUT_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		dateSelector = conf.getProperty(SimpleWiParser.DATE_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		dateFormat = conf.getProperty(SimpleWiParser.DATE_FORMAT_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		contentSelector = conf.getProperty(SimpleWiParser.CONTENT_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);
		pictureSelector = conf.getProperty(SimpleWiParser.PICTURE_SELECTOR_KEY,
				SimpleWiParser.UNKNOWN_VALUE);

		if (titleSelector.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| subTitleSelector.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| layoutSelector.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| dateSelector.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| dateFormat.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| contentSelector.equals(SimpleWiParser.UNKNOWN_VALUE)
				|| pictureSelector.equals(SimpleWiParser.UNKNOWN_VALUE)) {
			throw new ConfigException("Congif file is incomplete!");
		}
	}
}

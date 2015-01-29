package cn.nju.edu.winews.crawler.handler;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.nju.edu.winews.crawler.data.MongoHelper;
import cn.nju.edu.winews.crawler.data.PropertiesHelper;
import cn.nju.edu.winews.crawler.data.exception.ConfigIOException;
import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.handler.exception.ConfigException;
import cn.nju.edu.winews.crawler.handler.filter.WiUrlFilter;
import cn.nju.edu.winews.crawler.handler.parser.ParserFactory;
import cn.nju.edu.winews.crawler.handler.parser.ParserFactory.ParserType;
import cn.nju.edu.winews.crawler.handler.parser.WiParser;

public abstract class WiHandler {
	public static final String TIMEOUT_MILLIS_KEY = "timeout";
	public static final String NODE_URL_PATTERN_KEY = "node_url_pattern";
	public static final String CONTENT_URL_PATTERN_KEY = "content_url_pattern";
	public static final String DATE_PATTERN_KEY = "date_pattern";
	public static final String ROOT_URL_FORMAT_KEY = "root_url_format";
	public static final String DATE_FORMAT_KEY = "date_format";
	public static final String END_DATE_KEY = "end_date";
	public static final int DEFAULT_TIMEOUT_MILLIS = 5000;
	public static final String UNKNOWN_VALUE = "NULL";

	protected String sourceID = "";
	protected HashSet<URL> URL_SET = new HashSet<URL>();
	protected HashSet<String> DATE_SET = new HashSet<String>();

	protected int timeoutMillis;
	protected String nodeUrlPattern;
	protected String contentUrlPattern;
	protected String datePattern;
	protected String rootUrlFormat;
	protected String dateFormat;
	protected WiDate endDate;

	public WiHandler(String sourceID) {
		this.sourceID = sourceID;
	}

	public synchronized void start(WiDate date) {
		loadConf();
		while (!date.before(endDate)) {
			String rootUrlStr = date.fillDate(dateFormat, rootUrlFormat);
			URL url;
			try {
				url = new URL(rootUrlStr);
			} catch (Exception e) {
				System.err.println(rootUrlStr + "不能读取");
				break;
			}
			try {
				getLinks(url);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			URL_SET.clear();
			DATE_SET.add(date.toString());
			date.toLastDay();
		}
	}

	public void getLinks(URL url) throws Exception {
		Document doc = Jsoup.parse(url, timeoutMillis);
		Elements links = doc.getElementsByTag("a");
		WiUrlFilter urlFilter = new WiUrlFilter();
		HashSet<URL> urlSet = urlFilter.filter(doc.baseUri(), links);
		for (URL link : urlSet) {
			// Check URL
			String dateStr;
			try {
				dateStr = getDateFromLink(link.toString()).toString();
			} catch (Exception e1) {
				continue;
			}
			if (!DATE_SET.contains(dateStr)) {
				if (!URL_SET.contains(link)) {
					URL_SET.add(link);
					if (Pattern.matches(nodeUrlPattern, link.toString())) {
						try {
							getLinks(link);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					} else if (Pattern.matches(contentUrlPattern,
							link.toString())) {
						System.out.println("Content Link: " + link);
						WiNews news;
						try {
							WiParser parser = ParserFactory.createParser(
									ParserType.CONTENT_PAGE_PARSER, link);
							news = parser.parse(link);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						if (!news.getTitle().equals("")) {
							save(news);
						}
					}
				}
			}
		}
	}

	protected void save(WiNews news) throws Exception {
		MongoHelper mongo = new MongoHelper();
		mongo.addNews(news);
	}

	protected WiDate getDateFromLink(String s) throws ParseException {
		Pattern p = Pattern.compile(datePattern);
		Matcher m = p.matcher(s);
		if (m.find()) {
			String dateStr = m.group();
			DateFormat df = new SimpleDateFormat(dateFormat);
			Date date = df.parse(dateStr);
			return new WiDate(date);
		} else {
			throw new ParseException("在日期中找不到相应的Pattern: " + s, 0);
		}
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
		nodeUrlPattern = conf.getProperty(WiHandler.NODE_URL_PATTERN_KEY,
				WiHandler.UNKNOWN_VALUE);
		contentUrlPattern = conf.getProperty(WiHandler.CONTENT_URL_PATTERN_KEY,
				WiHandler.UNKNOWN_VALUE);
		datePattern = conf.getProperty(WiHandler.DATE_PATTERN_KEY,
				WiHandler.UNKNOWN_VALUE);
		rootUrlFormat = conf.getProperty(WiHandler.ROOT_URL_FORMAT_KEY,
				WiHandler.UNKNOWN_VALUE);
		dateFormat = conf.getProperty(WiHandler.DATE_FORMAT_KEY,
				WiHandler.UNKNOWN_VALUE);
		String endDateStr = conf.getProperty(WiHandler.END_DATE_KEY,
				WiHandler.UNKNOWN_VALUE);

		if (nodeUrlPattern.equals(WiHandler.UNKNOWN_VALUE)
				|| contentUrlPattern.equals(WiHandler.UNKNOWN_VALUE)
				|| datePattern.equals(WiHandler.UNKNOWN_VALUE)
				|| rootUrlFormat.equals(WiHandler.UNKNOWN_VALUE)
				|| dateFormat.equals(WiHandler.UNKNOWN_VALUE)
				|| endDateStr.equals(WiHandler.UNKNOWN_VALUE)) {
			throw new ConfigException("Congif file is incomplete!");
		}
		try {
			endDate = new WiDate(endDateStr, "yyyy-MM-dd");
		} catch (ParseException e) {
			throw new ConfigException("Congif date parse error!");
		}
	}
}

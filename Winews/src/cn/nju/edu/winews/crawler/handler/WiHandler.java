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
	public static final int MAX_DEPTH = 3;

	protected String sourceID = "";
	protected MongoHelper mongo;

	protected int timeoutMillis;
	protected String nodeUrlPattern;
	protected String contentUrlPattern;
	protected String datePattern;
	protected String rootUrlFormat;
	protected String dateFormat;
	protected WiDate endDate;
	protected WiDate curDate;

	public WiHandler(String sourceID) {
		this.sourceID = sourceID;
		try {
			mongo = new MongoHelper();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void start(WiDate date) {
		loadConf();
		while (!date.before(endDate)) {
			curDate = date;
			String rootUrlStr = date.fillDate(dateFormat, rootUrlFormat);
			URL url;
			try {
				url = new URL(rootUrlStr);
			} catch (Exception e) {
				System.err.println(rootUrlStr + "不能读取");
				break;
			}
			if (!mongo.existsDate(sourceID,date)) {
				try {
					System.out.println("\n" +date);
					getLinks(url, 0);
				} catch (Exception e) {
					e.printStackTrace();
					mongo.clearUrl(sourceID);
					date.toLastDay();
					continue;
				}
				mongo.clearUrl(sourceID);
				mongo.addDate(sourceID,date);
			} else {
				System.out.println(date + "has been fetched!");
			}
			
			date.toLastDay();
			// sleep a random time
			try {
				Thread.sleep((int) Math.random() * 2000);
			} catch (InterruptedException e) {
			}
		}
	}

	public void getLinks(URL url, int depth) throws Exception {
		WiDate curDate = getDateFromLink(url.toString());
		Document doc = Jsoup
				.connect(url.toString())
				.ignoreContentType(true)
				.ignoreHttpErrors(true)
				.timeout(timeoutMillis)
				.userAgent(
						"Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
				.get();
		Elements links = doc.getElementsByTag("a");
		links.addAll(doc.getElementsByTag("area"));
		WiUrlFilter urlFilter = new WiUrlFilter();
		HashSet<URL> urlSet = urlFilter.filter(doc.baseUri(), links);
		for (URL link : urlSet) {
			// Check URL date
			WiDate linkDate = getDateFromLink(url.toString());
			// 如果页面中发现的链接的日期等于页面自身的链接日期
			if (linkDate.equals(curDate)) {
				// 如果该链接没有被爬取过
				if (!mongo.existsUrl(sourceID,link.toString())) {
					mongo.addUrl(sourceID,link.toString()); // 链接加入链接列表
					// 如果是节点链接
					if (Pattern.matches(nodeUrlPattern, link.toString())) {
						try {
//							System.out.println("Node Link: " + link);
							System.out.print(".");
							getLinks(link, depth++);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						// 如果是正文链接
					} else if (Pattern.matches(contentUrlPattern,
							link.toString())) {
//						System.out.println("Content Link: " + link);
						System.out.print(".");
						WiNews news;
						try {
							WiParser parser = ParserFactory.createParser(
									ParserType.CONTENT_PAGE_PARSER, link);
							news = parser.parse(link);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						// 如果有标题就保存
						if (!news.getTitle().equals("")) {
							mongo.addNews(news);
						}
					}
				}
			}
		}
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

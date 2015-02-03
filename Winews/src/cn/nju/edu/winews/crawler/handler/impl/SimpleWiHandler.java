package cn.nju.edu.winews.crawler.handler.impl;

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

import cn.nju.edu.winews.crawler.data.exception.ConfigIOException;
import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.handler.WiHandler;
import cn.nju.edu.winews.crawler.handler.exception.ConfigException;
import cn.nju.edu.winews.data.MongoHelper;
import cn.nju.edu.winews.data.PropertiesHelper;

public class SimpleWiHandler implements WiHandler {
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

	private String sourceName = "";
	private MongoHelper mongo;

	private int timeoutMillis;
	private String nodeUrlPattern;
	private String contentUrlPattern;
	private String datePattern;
	private String rootUrlFormat;
	private String dateFormat;
	private WiDate endDate;

	private WiDate curDate;

	public SimpleWiHandler(String sourceName) {
		this.sourceName = sourceName;
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
			if (!mongo.existsDate(sourceName, date)) {
				try {
					System.out.println("\n" + date);
					getLinks(url, 0);
				} catch (Exception e) {
					e.printStackTrace();
					mongo.clearUrl(sourceName);
					date.toLastDay();
					continue;
				}
				mongo.clearUrl(sourceName);
				mongo.addDate(sourceName, date);
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
		} catch (Exception e1) {
			Thread.sleep(2000);
			doc = Jsoup
					.connect(url.toString())
					.ignoreContentType(true)
					.ignoreHttpErrors(true)
					.timeout(timeoutMillis)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; rv:22.0) Gecko/20100101 Firefox/22.0")
					.get();
		}
		Elements links = doc.getElementsByTag("a");
		links.addAll(doc.getElementsByTag("area"));
		WiUrlFilter urlFilter = new WiUrlFilter();
		HashSet<URL> urlSet = urlFilter.filter(doc.baseUri(), links);
		for (URL link : urlSet) {
			// Check URL date
			WiDate linkDate = getDateFromLink(url.toString());
			// 如果页面中发现的链接的日期不晚于页面自身的链接日期
			if (linkDate.equals(curDate)) {
				// 如果该链接没有被爬取过
				if (!mongo.existsUrl(sourceName, link.toString())) {
					mongo.addUrl(sourceName, link.toString()); // 链接加入链接列表
					// 如果是节点链接
					if (Pattern.matches(nodeUrlPattern, link.toString())) {
						try {
							System.out.println("Node Link: " + link);
							// System.out.print(".");
							getLinks(link, depth++);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						// 如果是正文链接
					} else if (Pattern.matches(contentUrlPattern,
							link.toString())) {
						System.out.println("Content Link: " + link);
						// System.out.print(".");
						WiNews news;
						try {
							SimpleWiParser parser = ParserFactory
									.createSimpleParser(sourceName);
							news = parser.parse(link);
							news.setDate(curDate);
							if (news.getLayout().equals("")) {
								news.setLayout(doc.select("#banzhibar>div")
										.first().text().trim());
							}
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

	private WiDate getDateFromLink(String s) throws ParseException {
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

	private void loadConf() {
		PropertiesHelper propHelper = new PropertiesHelper();
		Properties conf;
		try {
			conf = propHelper.getConf(sourceName);
		} catch (ConfigIOException e) {
			throw new ConfigException(e.getMessage());
		}
		timeoutMillis = Integer.parseInt(conf.getProperty(TIMEOUT_MILLIS_KEY,
				"" + DEFAULT_TIMEOUT_MILLIS));
		nodeUrlPattern = conf.getProperty(NODE_URL_PATTERN_KEY, UNKNOWN_VALUE);
		contentUrlPattern = conf.getProperty(CONTENT_URL_PATTERN_KEY,
				UNKNOWN_VALUE);
		datePattern = conf.getProperty(DATE_PATTERN_KEY, UNKNOWN_VALUE);
		rootUrlFormat = conf.getProperty(ROOT_URL_FORMAT_KEY, UNKNOWN_VALUE);
		dateFormat = conf.getProperty(DATE_FORMAT_KEY, UNKNOWN_VALUE);
		String endDateStr = conf.getProperty(END_DATE_KEY, UNKNOWN_VALUE);

		if (nodeUrlPattern.equals(UNKNOWN_VALUE)
				|| contentUrlPattern.equals(UNKNOWN_VALUE)
				|| datePattern.equals(UNKNOWN_VALUE)
				|| rootUrlFormat.equals(UNKNOWN_VALUE)
				|| dateFormat.equals(UNKNOWN_VALUE)
				|| endDateStr.equals(UNKNOWN_VALUE)) {
			throw new ConfigException("Congif file is incomplete!");
		}
		try {
			endDate = new WiDate(endDateStr, "yyyy-MM-dd");
		} catch (ParseException e) {
			throw new ConfigException("Congif date parse error!");
		}
	}
}

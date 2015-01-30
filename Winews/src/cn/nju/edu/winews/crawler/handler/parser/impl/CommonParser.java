package cn.nju.edu.winews.crawler.handler.parser.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nju.edu.winews.crawler.handler.exception.ParserException;

public class CommonParser {
	public static String getId(String sourceID,String urlStr) {
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
	
	public static String formatDate(String oldPattern, String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(oldPattern);
		try {
			Date d = sdf.parse(date);
			sdf = new SimpleDateFormat("yyyy年MM月dd日 E");
			return sdf.format(d);
		} catch (ParseException e) {
			return date;
		}
	}
}

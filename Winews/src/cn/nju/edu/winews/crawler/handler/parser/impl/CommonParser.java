package cn.nju.edu.winews.crawler.handler.parser.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.handler.exception.ParserException;

public class CommonParser {
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
	
	public static WiDate getDateFromLink(String datePattern, String dateFormat,String s) {
		Pattern p = Pattern.compile(datePattern);
		Matcher m = p.matcher(s);
		if (m.find()) {
			String dateStr = m.group();
			DateFormat df = new SimpleDateFormat(dateFormat);
			Date date;
			try {
				date = df.parse(dateStr);
			} catch (ParseException e) {
				throw new ParserException("在日期中找不到相应的Pattern: " + s);
			}
			return new WiDate(date);
		} else {
			throw new ParserException("在日期中找不到相应的Pattern: " + s);
		}
	}
}

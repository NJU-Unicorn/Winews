package cn.nju.edu.winews.crawler.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WiDate extends GregorianCalendar {
	private static final long serialVersionUID = 1L;

	public WiDate(int year, int month, int day) {
		super(year, month - 1, day);
	}

	public WiDate(Date date) {
		this.setTime(date);
	}
	
	public WiDate(String dateStr,String dateFormat) throws ParseException {
		DateFormat df = new SimpleDateFormat(dateFormat);
		this.setTime(df.parse(dateStr));
	}

	public int year() {
		return this.get(GregorianCalendar.YEAR);
	}

	public int month() {
		return this.get(GregorianCalendar.MONTH) + 1;
	}

	public int day() {
		return this.get(GregorianCalendar.DAY_OF_MONTH);
	}
	
	public WiDate toLastDay() {
		this.add(GregorianCalendar.DAY_OF_MONTH, -1);
		return this;
	}
	
	public WiDate toNextDay() {
		this.add(GregorianCalendar.DAY_OF_MONTH, 1);
		return this;
	}

	public String toString() {
		return year() + "-" + month() + "-" + day();
	}
	

	public String fillDate(String dateFormat, String s) {
		DateFormat df = new SimpleDateFormat(dateFormat);
		String dateStr = df.format(this.getTime());
		return s.replace("{{DATE}}", dateStr);
	}
	
	public String getFormatDate(String format) {
		DateFormat df = new SimpleDateFormat(format);
		String dateStr = df.format(this.getTime());
		return dateStr;
	}
	
	public String convertWeekName(String cn) {
		switch (cn) {
		case "星期一":
			return "Mon";
		case "星期二":
			return "Tue";
		case "星期三":
			return "Wed";
		case "星期四":
			return "Thu";
		case "星期五":
			return "Fri";
		case "星期六":
			return "Sat";
		case "星期日":
			return "Sun";
		default:
			System.out.println("无法转换星期: " + cn );
			return cn;
		}
	}

	public boolean equals(Object o) {
		if (o instanceof WiDate) {
			WiDate d = (WiDate) o;
			return d.year() == this.year() && d.month() == this.month()
					&& d.month() == this.month();
		}
		return false;
	}

}
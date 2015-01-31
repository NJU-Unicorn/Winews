package cn.nju.edu.winews.crawler.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

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
	
	public void toLastDay() {
		this.add(GregorianCalendar.DAY_OF_MONTH, -1);
	}
	
	public void toNextDay() {
		this.add(GregorianCalendar.DAY_OF_MONTH, 1);
	}

	public String toString() {
		return year() + "-" + month() + "-" + day();
	}
	

	public String fillDate(String dateFormat, String s) {
		DateFormat df = new SimpleDateFormat(dateFormat);
		String dateStr = df.format(this.getTime());
		return s.replace("{{DATE}}", dateStr);
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
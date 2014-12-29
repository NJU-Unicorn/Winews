package cn.edu.nju.unicorn;

import java.net.MalformedURLException;
import java.net.URL;

import cn.edu.nju.unicorn.handler.Crawler;
import cn.edu.nju.unicorn.parser.URLFilter;

public class TestApp {
	public static void main(String[] args) {
		try {
			Crawler c = new Crawler(new URL("http://wxppt.me/blog/"));
			URLFilter filter = new URLFilter();
			filter.addRemoveRegex(".*#.*");
			c.setUrlFilter(filter);
			c.setTimeout(10000);
			c.shieldExternalUrl(true);
			c.start();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

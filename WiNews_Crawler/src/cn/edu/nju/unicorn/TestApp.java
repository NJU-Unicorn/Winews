package cn.edu.nju.unicorn;

import java.net.MalformedURLException;
import java.net.URL;

import cn.edu.nju.unicorn.handler.Crawler;

public class TestApp {
	public static void main(String[] args) {
		try {
			Crawler c = new Crawler(new URL("http://www.lovelivewiki.com/index.php/EventList"));
			c.start();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

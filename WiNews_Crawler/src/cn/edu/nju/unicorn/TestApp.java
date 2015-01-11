package cn.edu.nju.unicorn;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import cn.edu.nju.unicorn.handler.Crawler;

public class TestApp {
	public static void main(String[] args) {
		Crawler c = new Crawler(readProperties("conf/bjrb.properties"));
		c.start();
	}

	public static Properties readProperties(String filePath) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					filePath));
			props.load(in);
			return props;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

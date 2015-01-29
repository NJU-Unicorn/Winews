package cn.nju.edu.winews.crawler;

import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.handler.BjrbHandler;

public class WiNewsMain {
	public static void main(String[] args) {
		BjrbHandler handler = new BjrbHandler();
		handler.start(new WiDate(2014, 9, 17));
	}
}

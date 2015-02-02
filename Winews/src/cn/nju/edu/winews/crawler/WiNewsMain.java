package cn.nju.edu.winews.crawler;

import java.util.Date;

import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.handler.WiHandler;
import cn.nju.edu.winews.crawler.handler.impl.SimpleWiHandler;

public class WiNewsMain {
	public static void main(String[] args) {
		WiHandler handler = new SimpleWiHandler(args[0]);
		if (args.length != 1) {
			handler.start(new WiDate(Integer.parseInt(args[1]), Integer
					.parseInt(args[2]), Integer.parseInt(args[3])));
		} else {
			handler.start(new WiDate(new Date()));
		}
	}
}

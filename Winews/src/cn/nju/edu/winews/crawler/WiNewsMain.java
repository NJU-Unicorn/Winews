package cn.nju.edu.winews.crawler;

import java.util.Date;

import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.handler.SimpleWiHandler;
import cn.nju.edu.winews.crawler.handler.WiHandler;
import cn.nju.edu.winews.crawler.handler.impl.CqrbHandler;
import cn.nju.edu.winews.crawler.handler.impl.HbrbHandler;
import cn.nju.edu.winews.crawler.handler.impl.HnrbHandler;
import cn.nju.edu.winews.crawler.handler.impl.JfrbHandler;

public class WiNewsMain {
	public static void main(String[] args) {
		WiHandler handler;
		switch (args[0]) {
		case "jfrb":
			handler = new JfrbHandler();
			break;
		case "cqrb":
			handler = new CqrbHandler();
			break;
		case "hbrb":
			handler = new HbrbHandler();
			break;
		case "hnrb":
			handler = new HnrbHandler();
			break;
		case "ynrb":
			handler = new SimpleWiHandler("ynrb");
			break;
		default:
			return;
		}
		if(args.length!=1) {
			handler.start(new WiDate(Integer.parseInt(args[1]), Integer
					.parseInt(args[2]), Integer.parseInt(args[3])));
		} else {
			handler.start(new WiDate(new Date()));
		}
	}
}

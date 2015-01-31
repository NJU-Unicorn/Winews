package cn.nju.edu.winews.crawler;

import java.util.Date;

import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.handler.SimpleWiHandler;
import cn.nju.edu.winews.crawler.handler.WiHandler;
import cn.nju.edu.winews.crawler.handler.impl.CqrbHandler;
import cn.nju.edu.winews.crawler.handler.impl.HbrbHandler;
import cn.nju.edu.winews.crawler.handler.impl.HenanrbHandler;
import cn.nju.edu.winews.crawler.handler.impl.JfrbHandler;
import cn.nju.edu.winews.crawler.handler.impl.XinjiangrbHandler;

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
		case "henanrb":
			handler = new HenanrbHandler();
			break;
		case "xinjiangrb":
			handler = new XinjiangrbHandler();
			break;
		default:
			handler = new SimpleWiHandler(args[0]);
		}
		handler.start(new WiDate(new Date()));
	}
}

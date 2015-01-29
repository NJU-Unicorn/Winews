package cn.nju.edu.winews.crawler;

import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.handler.BjrbHandler;
import cn.nju.edu.winews.crawler.handler.TjrbHandler;

public class WiNewsMain {
	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				TjrbHandler handler = new TjrbHandler();
				handler.start(new WiDate(2014, 12, 8));
			}
		}).start();
//		new Thread(new Runnable() {
//			public void run() {
//				BjrbHandler handler = new BjrbHandler();
//				handler.start(new WiDate(2014, 1, 10));
//			}
//		}).start();

	}
}

package cn.nju.edu.winews.crawler;

import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.handler.impl.CqrbHandler;
import cn.nju.edu.winews.crawler.handler.impl.JfrbHandler;
import cn.nju.edu.winews.crawler.handler.impl.TjrbHandler;

public class WiNewsMain {
	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				TjrbHandler handler = new TjrbHandler();
				handler.start(new WiDate(2014, 3, 11));
			}
		}).start();
//		new Thread(new Runnable() {
//			public void run() {
//				JfrbHandler handler = new JfrbHandler();
//				handler.start(new WiDate(2014,8,1));
//			}
//		}).start();
//		new Thread(new Runnable() {
//			public void run() {
//				CqrbHandler handler = new CqrbHandler();
//				handler.start(new WiDate(2014,9,23));
//			}
//		}).start();
	}
}

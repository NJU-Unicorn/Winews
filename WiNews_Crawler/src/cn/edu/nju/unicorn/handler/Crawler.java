package cn.edu.nju.unicorn.handler;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.edu.nju.unicorn.entity.EmptySetException;
import cn.edu.nju.unicorn.entity.URLSet;

public class Crawler {
	public static final int DEFAULT_DEPTH = 3;
	public static final int DEFAULT_TIMEOUT = 5000;
	private URLSet urlSet;
	private int depth = DEFAULT_DEPTH;
	private int timeout = DEFAULT_TIMEOUT;
	
	public Crawler() {
		urlSet = new URLSet();
	}

	public Crawler(URL base) {
		urlSet = new URLSet();
		urlSet.addUrl(base);
	}
	
	public void addBaseURL(URL base) {
		urlSet.addUrl(base);
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public void start() {
		while(!urlSet.isEmpty()) {
			URL u = null;
			try {
				u = urlSet.getUrl();
			} catch (EmptySetException e) {
				e.printStackTrace();
				return;
			}
			if(u == null) {
				return;
			}
			Document doc = null;
			try {
				doc = Jsoup.parse(u, timeout);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			// create a thread to deal with document
			// add new url to set
		}
	}
}

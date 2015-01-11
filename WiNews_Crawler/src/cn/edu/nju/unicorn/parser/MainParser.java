package cn.edu.nju.unicorn.parser;

import org.jsoup.nodes.Document;

import cn.edu.nju.unicorn.data.ParserPool;

public class MainParser {
	private Document doc = null;

	public MainParser(Document doc) {
		this.doc = doc;
	}

	public void process() {
		
		ParserPool.getInstance().closeThread(Thread.currentThread());
	}
}

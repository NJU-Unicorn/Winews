package cn.nju.edu.winews.crawler.handler.parser;

import java.net.URL;

import cn.nju.edu.winews.crawler.handler.exception.ParserException;

public class ParserFactory {
	public static enum ParserType {
		CONTENT_PAGE_PARSER
	}
	
	public static WiParser createParser(ParserType type, URL url) {
		if(type.equals(ParserType.CONTENT_PAGE_PARSER)) {
			String hostName = url.getHost();
			switch(hostName) {
			case "bjrb.bjd.com.cn":
				return new BjrbContentPageParser();
			default:
				System.out.println("Niconiconi!");
				throw new ParserException("Can't recognize the host: " + hostName);
			}
		} else {
			System.out.println("Niconiconi!");
			throw new ParserException("Can't recognize the type: " + type.toString());
		}
	}
}

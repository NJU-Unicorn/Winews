package cn.nju.edu.winews.crawler.handler.parser;

import java.net.URL;

import cn.nju.edu.winews.crawler.handler.exception.ParserException;

public class ParserFactory {
	public static enum ParserType {
		CONTENT_PAGE_PARSER, OLD_CONTENT_PAGE_PARSER
	}

	public static WiParser createParser(ParserType type, URL url) {
		String hostName = url.getHost();
		if (type.equals(ParserType.CONTENT_PAGE_PARSER)) {
			switch (hostName) {
			case "bjrb.bjd.com.cn":
				return new BjrbContentPageParser();
			case "epaper.tianjinwe.com":
				return new TjrbContentPageParser();
			case "newspaper.jfdaily.com":
				return new JfrbContentPageParser();
			case "cqrbepaper.cqnews.net":
				return new CqrbContentPageParser();
			default:
				System.out.println("Niconiconi!");
				throw new ParserException("Can't recognize the host: "
						+ hostName);
			}
		} else if (type.equals(ParserType.OLD_CONTENT_PAGE_PARSER)) {
			switch (hostName) {
			case "epaper.tianjinwe.com":
				return new OldTjrbContentPageParser();
			default:
				System.out.println("Niconiconi!");
				throw new ParserException("Can't recognize the host: "
						+ hostName);
			}
		} else {
			System.out.println("Niconiconi!");
			throw new ParserException("Can't recognize the type: "
					+ type.toString());
		}
	}
}

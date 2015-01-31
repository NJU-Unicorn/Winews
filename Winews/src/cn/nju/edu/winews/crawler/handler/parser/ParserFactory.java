package cn.nju.edu.winews.crawler.handler.parser;

import java.net.URL;
import java.util.HashMap;

import cn.nju.edu.winews.crawler.handler.exception.ParserException;
import cn.nju.edu.winews.crawler.handler.parser.impl.BjrbParser;
import cn.nju.edu.winews.crawler.handler.parser.impl.CqrbParser;
import cn.nju.edu.winews.crawler.handler.parser.impl.HbrbParser;
import cn.nju.edu.winews.crawler.handler.parser.impl.HenanrbParser;
import cn.nju.edu.winews.crawler.handler.parser.impl.JfrbParser;
import cn.nju.edu.winews.crawler.handler.parser.impl.OldTjrbParser;
import cn.nju.edu.winews.crawler.handler.parser.impl.TjrbParser;

public class ParserFactory {
	private static HashMap<String, SimpleWiParser> map = new HashMap<String, SimpleWiParser>();

	public static enum ParserType {
		CONTENT_PAGE_PARSER, OLD_CONTENT_PAGE_PARSER
	}

	private static String sourceID2sourceName(String sourceID) {
		switch (sourceID) {
		case "ynrb":
			return "云南日报";
		default:
			throw new ParserException("Can't recognize the sourceID: "
					+ sourceID);
		}
	}

	public static SimpleWiParser createSimpleParser(String sourceID) {
		if (map.get(sourceID) == null) {
			SimpleWiParser parser = new SimpleWiParser(sourceID,sourceID2sourceName(sourceID));
			map.put(sourceID, parser);
		}
		return map.get(sourceID);
	}

	public static WiParser createParser(ParserType type, URL url) {
		String hostName = url.getHost();
		if (type.equals(ParserType.CONTENT_PAGE_PARSER)) {
			switch (hostName) {
			case "bjrb.bjd.com.cn":
				return new BjrbParser();
			case "epaper.tianjinwe.com":
				return new TjrbParser();
			case "newspaper.jfdaily.com":
				return new JfrbParser();
			case "cqrbepaper.cqnews.net":
				return new CqrbParser();
			case "hbrb.hebnews.cn":
				return new HbrbParser();
			case "newpaper.dahe.cn":
				return new HenanrbParser();
			default:
				System.out.println("Niconiconi!");
				throw new ParserException("Can't recognize the host: "
						+ hostName);
			}
		} else if (type.equals(ParserType.OLD_CONTENT_PAGE_PARSER)) {
			switch (hostName) {
			case "epaper.tianjinwe.com":
				return new OldTjrbParser();
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

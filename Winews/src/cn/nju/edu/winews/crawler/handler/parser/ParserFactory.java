package cn.nju.edu.winews.crawler.handler.parser;

import java.util.HashMap;

public class ParserFactory {
	private static HashMap<String, SimpleWiParser> map = new HashMap<String, SimpleWiParser>();

	public static SimpleWiParser createSimpleParser(String sourceID) {
		if (map.get(sourceID) == null) {
			SimpleWiParser parser = new SimpleWiParser(sourceID);
			map.put(sourceID, parser);
		}
		return map.get(sourceID);
	}
}

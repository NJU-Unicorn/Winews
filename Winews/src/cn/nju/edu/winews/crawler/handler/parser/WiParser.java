package cn.nju.edu.winews.crawler.handler.parser;

import java.net.URL;

import cn.nju.edu.winews.crawler.entity.WiNews;

public interface WiParser {
	public WiNews parse(URL url);
}

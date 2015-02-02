package cn.nju.edu.winews.crawler.handler;

import java.net.URL;

import cn.nju.edu.winews.crawler.entity.WiDate;

public interface WiHandler {

	public void start(WiDate date);

	public void getLinks(URL url, int depth) throws Exception;

}

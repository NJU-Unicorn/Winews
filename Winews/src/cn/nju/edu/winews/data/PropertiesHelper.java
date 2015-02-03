package cn.nju.edu.winews.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import cn.nju.edu.winews.crawler.data.exception.ConfigIOException;

public class PropertiesHelper {
	public static final String CONF_PATH = "conf";

	public Properties getConf(String sourceID) throws ConfigIOException {
		String filePath = CONF_PATH + "/" + sourceID + ".properties";
		File confFile = new File(filePath);
		if (!confFile.exists()) {
			throw new ConfigIOException("Can't find config file in path: "
					+ filePath);
		}
		Properties conf = new Properties();
		try {
			conf.load(new FileInputStream(confFile));
		} catch (IOException e) {
			throw new ConfigIOException(e.getMessage());
		}
		return conf;
	}
}

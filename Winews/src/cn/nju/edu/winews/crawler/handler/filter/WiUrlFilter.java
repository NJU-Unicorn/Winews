package cn.nju.edu.winews.crawler.handler.filter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WiUrlFilter {
	
	public HashSet<URL> xinjiangFilter(String baseUri, Elements elements) {
		HashSet<URL> set = new HashSet<URL>();
		URL baseUrl = null;
		try {
			baseUrl = new URL(baseUri);
		} catch (MalformedURLException e2) {
			return set;
		}
		for (Element element : elements) {
			String u = element.attr("href");
			if (isEligible(u)) {
				String[] baseUriSp = baseUri.split("/");
				String replacement = baseUriSp[baseUriSp.length - 1];
				u = baseUri.replace(replacement, u);
				URL url = null;
				try {
					url = new URL(u);
				} catch (MalformedURLException e) {
				}
				if (baseUrl != null && url != null) {
					if (baseUrl.getHost().equals(url.getHost())) {
						set.add(url);
					}
				}
			}
		}
		return set;
	}
	
	public HashSet<URL> filter(String baseUri, Elements elements) {
		HashSet<URL> set = new HashSet<URL>();
		URL baseUrl = null;
		try {
			baseUrl = new URL(baseUri);
		} catch (MalformedURLException e2) {
			return set;
		}
		for (Element element : elements) {
			String u = element.attr("href").split("\\?")[0];
			if (isEligible(u)) {
				URL url = null;
				try {
					url = new URL(u);
				} catch (MalformedURLException e) {
					if (e.getMessage().contains("no protocol")) {
						String[] baseUriSp = baseUri.split("/");
						String replacement = baseUriSp[baseUriSp.length - 1];
						u = u.replaceAll("^\\./", "");
						u = baseUri.replaceAll(replacement + "$", u);
						try {
							url = new URL(u);
						} catch (MalformedURLException e1) {
							System.err.println("Error Url: " + u);
						}
					}
				}
				if (baseUrl != null && url != null) {
					if (baseUrl.getHost().equals(url.getHost())) {
						set.add(url);
					}
				}
			}
		}
		return set;
	}

	public boolean isEligible(String s) {
		if (s.trim().equals("")) {
			return false;
		}
		if (s.contains("#")) {
			return false;
		}
		if (s.contains("javascript")) {
			return false;
		}
		if (s.endsWith(".pdf")) {
			return false;
		}
		return true;
	}
}
package cn.nju.edu.winews.crawler.entity;

import java.net.URL;

public class WiNewsPicture {
	private URL newsUrl;
	private URL url;
	private String comment;


	/**
	 * @return the newsUrl
	 */
	public URL getNewsUrl() {
		return newsUrl;
	}

	/**
	 * @param newsUrl the newsUrl to set
	 */
	public void setNewsUrl(URL newsUrl) {
		this.newsUrl = newsUrl;
	}

	/**
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

}

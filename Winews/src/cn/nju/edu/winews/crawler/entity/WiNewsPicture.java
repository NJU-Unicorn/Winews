package cn.nju.edu.winews.crawler.entity;

import java.net.URL;

public class WiNewsPicture {
	private String newsId;
	private URL url;
	private String comment;

	/**
	 * @return the newsId
	 */
	public String getNewsId() {
		return newsId;
	}

	/**
	 * @param newsId
	 *            the newsId to set
	 */
	public void setNewsId(String newsId) {
		this.newsId = newsId;
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

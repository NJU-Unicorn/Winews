package cn.nju.edu.winews.crawler.entity;

import java.net.URL;
import java.util.ArrayList;

public class WiNews {
	private URL url;
	private String title;
	private String subTitle;
	private String source;
	private WiDate date;
	private String layout;
	private String content = "";
	private ArrayList<WiNewsPicture> pictures = new ArrayList<WiNewsPicture>();

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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the subTitle
	 */
	public String getSubTitle() {
		return subTitle;
	}

	/**
	 * @param subTitle
	 *            the subTitle to set
	 */
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the date
	 */
	public WiDate getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(WiDate date) {
		this.date = date;
	}

	/**
	 * @return the layout
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * @param layout
	 *            the layout to set
	 */
	public void setLayout(String layout) {
		this.layout = layout;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @param content
	 *            the content to append
	 */
	public void appendContent(String content) {
		this.content += content;
	}

	/**
	 * @return the pictures
	 */
	public ArrayList<WiNewsPicture> getPictures() {
		return pictures;
	}

	/**
	 * @param pictures
	 *            the pictures to set
	 */
	public void setPictures(ArrayList<WiNewsPicture> pictures) {
		this.pictures = pictures;
	}

	/**
	 * @param picture
	 *            the picture to add
	 */
	public void addPicture(WiNewsPicture picture) {
		pictures.add(picture);
	}

	public String toString() {
		return "《" + title + " —— " + subTitle + "》\r\n"
				+ "来源：" + source + "\t\t版面：" + layout + "\t\t时间：" + date
				+ "\r\n" + "地址：" + url + "\r\n" + content;
	}

}

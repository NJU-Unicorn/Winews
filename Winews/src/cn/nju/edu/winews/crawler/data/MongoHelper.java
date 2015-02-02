package cn.nju.edu.winews.crawler.data;

import cn.nju.edu.winews.crawler.data.exception.MongoIOException;
import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.entity.WiNewsPicture;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public class MongoHelper {
	private MongoClient client;
	private DB db;

	public MongoHelper() throws Exception {
		try {
			// client = new MongoClient("121.40.127.177", 18017);
			client = new MongoClient("localhost", 27017);
			// client = new MongoClient("localhost", 18017);
		} catch (Exception e) {
			throw e;
		}
		db = client.getDB("Winews");
	}

	public boolean existsDate(String source, WiDate date) {
		DBCollection coll = db.getCollection("_date");
		BasicDBObject dbObj = new BasicDBObject("source", source).append(
				"date", date.toString());
		return coll.findOne(dbObj) != null;
	}

	public void addDate(String source, WiDate date) {
		DBCollection coll = db.getCollection("_date");
		BasicDBObject dbObj = new BasicDBObject("source", source).append(
				"date", date.toString());
		coll.save(dbObj);
	}

	public boolean existsUrl(String source, String url) {
		DBCollection coll = db.getCollection("_url");
		BasicDBObject dbObj = new BasicDBObject("source", source).append(
				"url", url);
		return coll.findOne(dbObj) != null;
	}

	public void addUrl(String source, String url) {
		DBCollection coll = db.getCollection("_url");
		BasicDBObject dbObj = new BasicDBObject("source", source).append(
				"url", url);
		coll.save(dbObj);
	}

	public void clearUrl(String source) {
		DBCollection coll = db.getCollection("_url");
		coll.remove(new BasicDBObject("source", source));
	}

	public boolean existsNews(WiNews news) {
		DBCollection coll = db.getCollection(news.getSource());
		BasicDBObject dbObj = new BasicDBObject("url", news.getUrl().toString());
		return coll.findOne(dbObj) != null;
	}

	public synchronized void addNews(WiNews news) throws MongoIOException {
		DBCollection coll = db.getCollection(news.getSource());
		// if there is no reference Collection
		if (!db.getCollectionNames().contains(news.getSource())) {
			db.createCollection(news.getSource(), new BasicDBObject());
			coll = db.getCollection(news.getSource());
			coll.createIndex(new BasicDBObject("url", 1), new BasicDBObject(
					"unique", true).append("name", "url"));
		}
		if (coll.findOne(new BasicDBObject("url", news.getUrl().toString())) != null) {
			System.out.println("News already in the database.");
			return;
		}
		DBObject obj = news2DBObject(news);
		WriteResult result = coll.save(obj);
		if (result.getN() != 0) {
			throw new MongoIOException("Can't save Mongo Object!");
		}
	}

	// private WiNews DBObject2News(DBObject o) throws MalformedURLException {
	// WiNews news = new WiNews();
	// news.setContent(o.get("content").toString());
	// news.setDate(o.get("date").toString());
	// news.setLayout(o.get("layout").toString());
	// news.setSource(o.get("source").toString());
	// news.setSubTitle(o.get("sub_title").toString());
	// news.setUrl(new URL(o.get("url").toString()));
	// for (Object listObj : ((BasicDBList) o.get("pictures"))) {
	// DBObject dbListObj = (DBObject) listObj;
	// WiNewsPicture pic = new WiNewsPicture();
	// pic.setNewsUrl(news.getUrl());
	// pic.setUrl(new URL(dbListObj.get("url").toString()));
	// pic.setComment(dbListObj.get("comment").toString());
	// news.addPicture(pic);
	// }
	// return news;
	// }

	private DBObject news2DBObject(WiNews news) {
		DBObject o = new BasicDBObject();
		o.put("url", news.getUrl().toString());
		o.put("date", news.getDate());
		o.put("layout", news.getLayout());
		o.put("source", news.getSource());
		o.put("title", news.getTitle());
		o.put("sub_title", news.getSubTitle());
		o.put("content", news.getContent());
		BasicDBList pics = new BasicDBList();
		for (WiNewsPicture pic : news.getPictures()) {
			DBObject picObj = new BasicDBObject();
			picObj.put("url", pic.getUrl().toString());
			picObj.put("comment", pic.getComment());
			pics.add(picObj);
		}
		o.put("pictures", pics);
		return o;
	}
}

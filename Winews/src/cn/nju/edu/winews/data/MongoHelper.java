package cn.nju.edu.winews.data;

import cn.nju.edu.winews.crawler.data.exception.MongoIOException;
import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.entity.WiNews;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public class MongoHelper {
	private MongoClient client;
	private DB db;

	public MongoHelper() throws Exception {
		try {
			// client = new MongoClient("121.40.127.177", 18017);
			client = new MongoClient("115.29.242.187", 27017);
			// client = new MongoClient("localhost", 27017);
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
		BasicDBObject dbObj = new BasicDBObject("source", source).append("url",
				url);
		return coll.findOne(dbObj) != null;
	}

	public void addUrl(String source, String url) {
		DBCollection coll = db.getCollection("_url");
		BasicDBObject dbObj = new BasicDBObject("source", source).append("url",
				url);
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
		DBObject obj = WiConvertor.news2DBObject(news);
		WriteResult result = coll.save(obj);
		if (result.getN() != 0) {
			throw new MongoIOException("Can't save Mongo Object!");
		}
	}

	public synchronized DBCursor getNews(String source) throws MongoIOException {
		DBCollection coll = db.getCollection(source);
		if (coll == null) {
			throw new MongoIOException("Can't find this collection: " + source);
		}
		DBCursor cur = coll.find(new BasicDBObject());
		return cur;
	}

	public synchronized DBCursor getNews(String source, WiDate date)
			throws MongoIOException {
		DBCollection coll = db.getCollection(source);
		if (coll == null) {
			throw new MongoIOException("Can't find this collection: " + source);
		}
		DBCursor cur = coll.find(new BasicDBObject("date", date
				.getFormatDate("yyyy-MM-dd")));
		return cur;
	}

	public static void main(String[] args) throws MongoIOException, Exception {
		DBCursor cur = new MongoHelper()
				.getNews("北京日报", new WiDate(2015, 5, 5));
		if (cur.hasNext()) {
			System.out.println(cur.next());
		}
	}
}

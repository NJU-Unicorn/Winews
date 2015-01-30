package cn.nju.edu.winews.crawler.data;

import java.net.MalformedURLException;
import java.net.URL;

import cn.nju.edu.winews.crawler.data.exception.MongoIOException;
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
			client = new MongoClient("121.40.127.177", 18017);
		} catch (Exception e) {
			throw e;
		}
		db = client.getDB("Winews");
	}

	public boolean existsNews(WiNews news) {
		DBCollection coll = db.getCollection(news.getSourceID());
		BasicDBObject dbObj = new BasicDBObject("news_id", news.getId());
		return coll.findOne(dbObj) != null;
	}

	public synchronized void addNews(WiNews news) throws MongoIOException {
		DBCollection coll = db.getCollection(news.getSourceID());
		// if there is no reference Collection
		if (!db.getCollectionNames().contains(news.getSourceID())) {
			db.createCollection(news.getSourceID(), new BasicDBObject());
			coll = db.getCollection(news.getSourceID());
			coll.createIndex(new BasicDBObject("news_id", 1),
					new BasicDBObject("unique", true).append("name", "news_id"));
		}
		if(coll.findOne(new BasicDBObject("news_id", news.getId())) != null) {
			System.out.println("News already in the database.");
			return;
		}
		DBObject obj = news2DBObject(news);
		WriteResult result = coll.save(obj);
		if (result.getN() != 0) {
			throw new MongoIOException("Can't save Mongo Object!");
		}
	}

	public WiNews getNews(String sourceID, String id) {
		DBCollection coll = db.getCollection(sourceID);
		DBObject obj = coll.findOne(new BasicDBObject("news_id", id));
		if(obj != null) {
			WiNews result = null;
			try {
				result = DBObject2News(obj);
			} catch (MalformedURLException e) {
				// impossible
				System.err.println("URL Read Error");
			}
			return result;
		}
		return null;
	}

	public void removeNews(String sourceID, String id) {
		DBCollection coll = db.getCollection(sourceID);
		coll.remove(new BasicDBObject("news_id",id));
	}

	public void updateNews(WiNews news) {
		DBCollection coll = db.getCollection(news.getSourceID());
		coll.remove(new BasicDBObject("news_id",news.getId()));
		coll.findOne(new BasicDBObject("news_id", news.getId()));
		coll.save(news2DBObject(news));
	}
	
	private WiNews DBObject2News(DBObject o) throws MalformedURLException {
		WiNews news = new WiNews();
		news.setId(o.get("news_id").toString());
		news.setContent(o.get("content").toString());
		news.setDate(o.get("date").toString());
		news.setLayout(o.get("layout").toString());
		news.setSource(o.get("source").toString());
		news.setSourceID(o.get("source_id").toString());
		news.setSubTitle(o.get("sub_title").toString());
		news.setUrl(new URL(o.get("url").toString()));
		for(Object listObj: ((BasicDBList)o.get("pictures"))) {
			DBObject dbListObj = (DBObject) listObj;
			WiNewsPicture pic = new WiNewsPicture();
			pic.setNewsId(news.getId());
			pic.setUrl(new URL(dbListObj.get("url").toString()));
			pic.setComment(dbListObj.get("comment").toString());
			news.addPicture(pic);
		}
		return news;
	}

	private DBObject news2DBObject(WiNews news) {
		DBObject o = new BasicDBObject();
		o.put("news_id", news.getId());
		o.put("url", news.getUrl().toString());
		o.put("date", news.getDate());
		o.put("layout", news.getLayout());
		o.put("source_id", news.getSourceID());
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

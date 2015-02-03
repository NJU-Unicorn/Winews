package cn.nju.edu.winews.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import cn.nju.edu.winews.crawler.entity.WiDate;
import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.crawler.entity.WiNewsPicture;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class WiConvertor {
	public static WiNews DBObject2News(DBObject o) throws MalformedURLException, ParseException {
		WiNews news = new WiNews();
		news.setContent(o.get("content").toString());
		news.setDate(new WiDate(o.get("date").toString(),"yyyy-MM-dd"));
		news.setLayout(o.get("layout").toString());
		news.setSource(o.get("source").toString());
		news.setSubTitle(o.get("sub_title").toString());
		news.setUrl(new URL(o.get("url").toString()));
		for (Object listObj : ((BasicDBList) o.get("pictures"))) {
			DBObject dbListObj = (DBObject) listObj;
			WiNewsPicture pic = new WiNewsPicture();
			pic.setNewsUrl(news.getUrl());
			pic.setUrl(new URL(dbListObj.get("url").toString()));
			pic.setComment(dbListObj.get("comment").toString());
			news.addPicture(pic);
		}
		return news;
	}

	public static DBObject news2DBObject(WiNews news) {
		DBObject o = new BasicDBObject();
		o.put("url", news.getUrl().toString());
		o.put("date", news.getDate().getFormatDate("yyyy-MM-dd"));
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

package cn.nju.edu.winews.nlpir;

import cn.nju.edu.winews.crawler.entity.WiNews;
import cn.nju.edu.winews.data.MongoHelper;
import cn.nju.edu.winews.data.WiConvertor;
import cn.nju.edu.winews.nlpir.lib.NLPIR;
import cn.nju.edu.winews.nlpir.lib.NLPIRConfig;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class NlpirMain {
	public static void main(String[] args) throws Exception {
		// 初始化NLPIR分词工具
		NLPIR nlpir = NLPIRConfig.initNLPIR();
		// 实例化MongoDB数据库工具
		MongoHelper data = new MongoHelper();
		// 获得指定报刊的新闻指针
		DBCursor cur = data.getNews("新华日报");
		while(cur.hasNext()) {	// 如果有下一条
			DBObject obj = cur.next();		// 取得新闻
			WiNews news = WiConvertor.DBObject2News(obj);	// BSON格式数据转WiNews
			if(news.getContent().equals("")) {	// 如果正文为空则跳过
				continue;
			}
			System.out.println(news);	// 打印新闻
			String result = nlpir.NLPIR_ParagraphProcess(news.getContent(), 1);	// 分词
			System.out.println();
			System.out.println(result);	// 打印分词结果
			break;
		}
	}
}

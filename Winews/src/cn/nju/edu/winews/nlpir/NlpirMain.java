package cn.nju.edu.winews.nlpir;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		// 读取词库
		readDictFile(nlpir);
		// 实例化MongoDB数据库工具
		MongoHelper data = new MongoHelper();
		// 获得指定报刊的新闻指针
		DBCursor cur = data.getNews("新华日报");
		while (cur.hasNext()) { // 如果有下一条
			DBObject obj = cur.next(); // 取得新闻
			WiNews news = WiConvertor.DBObject2News(obj); // BSON格式数据转WiNews
			if (news.getContent().length()<30) { // 如果正文为空则跳过
				continue;
			}
//			System.out.println(news.getUrl());
			String titleNlpir = nlpir
					.NLPIR_ParagraphProcess(news.getTitle(), 1);
			String subTitleNlpir = nlpir.NLPIR_ParagraphProcess(
					news.getSubTitle(), 1);
			String contentNlpir = nlpir.NLPIR_ParagraphProcess(
					news.getContent(), 1);
			int totalWordsCnt = 0;
			int govWordsCnt = 0;
			Pattern p = Pattern.compile("/");
			Matcher m = p.matcher(contentNlpir);
			while (m.find()) {
				totalWordsCnt++;
			}
			Pattern p2 = Pattern.compile("/gov");
			Matcher m2 = p2.matcher(contentNlpir);
			while (m2.find()) {
				govWordsCnt++;
			}
//			System.out.println();
//			System.out.println("title: " + titleNlpir + "\nsub: "
//					+ subTitleNlpir + "\ncontent: " + contentNlpir); // 打印分词结果
			if(((double)govWordsCnt)/((double)totalWordsCnt)>0.03) {
				System.out.println(news.getTitle());
			} else {
				System.out.print(".");
			}
//			System.out
//					.println("----------------------------------------------------------------");
//			System.in.read();
//			System.in.read();
//			readDictFile(nlpir);
		}
	}

	public static void readDictFile(NLPIR nlpir) throws Exception {
		BufferedReader bfr = new BufferedReader(new InputStreamReader(
				new FileInputStream("wi_dict.txt")));
		String line = null;
		while ((line = bfr.readLine()) != null) {
			nlpir.NLPIR_AddUserWord(line);
		}
		bfr.close();
	}
}

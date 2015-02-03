package cn.nju.edu.winews.nlpir;

import cn.nju.edu.winews.nlpir.lib.NLPIR;
import cn.nju.edu.winews.nlpir.lib.NLPIRConfig;

public class NlpirMain {
	public static void main(String[] args) {
		NLPIR nlpir = NLPIRConfig.initNLPIR();
		String content = "我爱我的祖国";
		String result = nlpir.NLPIR_ParagraphProcess(content, 1);
		System.out.println(result);
	}
}

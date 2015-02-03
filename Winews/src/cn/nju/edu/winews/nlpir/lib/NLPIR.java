package cn.nju.edu.winews.nlpir.lib;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface NLPIR extends Library {
	// 定义并初始化接口的静态变量
	public static NLPIR INSTANCE = (NLPIR) Native.loadLibrary(
			NLPIRConfig.getLibraryPath(), NLPIR.class);

	public int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);

	public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

	public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
			boolean bWeightOut);

	public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,
			boolean bWeightOut);

	public int NLPIR_AddUserWord(String sWord);

	public int NLPIR_DelUsrWord(String sWord);

	public String NLPIR_GetLastErrorMsg();

	public void NLPIR_Exit();

}

package cn.nju.edu.winews.nlpir.lib;

import java.util.Properties;

public class NLPIRConfig {

	static String getLibraryPath() {
		// 读取系统属性，不同的系统调用不同版本的NLPIR库
		Properties props = System.getProperties();
		String osName = props.getProperty("os.name").toLowerCase();
		String osArch = props.getProperty("os.arch").toLowerCase();
		if (osName.contains("windows")) {
			if (osArch.contains("64")) {
				return "NLPIR/Library/win64/NLPIR";
			} else {
				return "NLPIR/Library/win32/NLPIR";
			}
		} else if (osName.contains("ubuntu") || osName.contains("linux")) {
			if (osArch.contains("64")) {
				return "NLPIR/Library/linux64/NLPIR";
			} else {
				return "NLPIR/Library/linux32/NLPIR";
			}
		} else {
			System.err.println("Unsupported System: " + osName);
			return null;
		}
	}
	

	public static NLPIR initNLPIR() {
		int init_flag = NLPIR.INSTANCE.NLPIR_Init("NLPIR/", 1, "0");
		String nativeBytes = null;

		if (0 == init_flag) {
			nativeBytes = NLPIR.INSTANCE.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is " + nativeBytes);
			System.exit(-1);
		}
		return NLPIR.INSTANCE;
	}
}

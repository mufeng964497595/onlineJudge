package com.hwf.util;

/**
 * 记录状态值
 */
public class SelectOptionValueUtil {

	private static String[] results = { "All", "Accepted", "Wrong Answer", "Time Limit Exceeded", "Memory Limit Exceeded",
			"Runtime Error", "Output Limit Exceeded", "Compile Error", "System Error", "Queueing", "Compiling", "Running" };
	private static String[] languages = { "All", "C", "C++", "JAVA" };

	public static String[] getResults() {
		return results;
	}

	public static String[] getLanguages() {
		return languages;
	}
}

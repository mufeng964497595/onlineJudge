package com.hwf.util;

/**
 * 记录状态值
 */
public class SelectOptionValueUtil {

	private static String[] results = { "All", "Accepted", "Wrong Answer", "Time Limit Exceed", "Memory Limit Exceed", "Runtime Error", "Compile Error",
			"Queuing &amp;&amp; Judging", "System Error" };
	private static String[] languages = { "All", "C", "C++", "JAVA" };

	public static String[] getResults() {
		return results;
	}

	public static String[] getLanguages() {
		return languages;
	}
}

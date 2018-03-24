package com.hwf.deal;

import java.util.ArrayList;

/**
 * 处理Java语言提交的代码
 */
public class DealSubmitCodeInJava extends DealSubmitCode {
	private final String sourceFileName = "Main.java";
	private final String outFileName = "Main";

	DealSubmitCodeInJava(String cid, String pno, String code, long runId, int timeLimit, int memLimit) {
		super(cid, pno, code, runId, timeLimit, memLimit);
	}

	@Override
	protected String getCodeFileName() {
		return sourceFileName;
	}

	@Override
	protected String getCompileFileName() {
		return outFileName + ".class";
	}

	@Override
	protected ArrayList<String> getCompileCommands() {
		ArrayList<String> compileCommands = new ArrayList<>();
		/*用docker编译程序*/
		compileCommands.add(compileShell);
		compileCommands.add(String.format("%s:%s", codeDir, codeDir));
		compileCommands.add("java:8-jdk");
		compileCommands.add(String.format("cd %s&&javac %s", codeDir, sourceFileName));

		return compileCommands;
	}

	@Override
	protected ArrayList<String> getExecuteCommands() {
		ArrayList<String> executeCommands = new ArrayList<>();
		/*用docker执行程序*/
		/*java的内存和时间限制均为C、C++的两倍*/
		executeCommands.add(executeShell);
		executeCommands.add(String.format("%s:%s", codeDir, codeDir));
		executeCommands.add("java:8-jdk");
		executeCommands.add(String.format("cd %s&&java %s", codeDir, outFileName));
		executeCommands.add(timeLimit * 2 + "");
		executeCommands.add(memLimit * 2 + "");

		return executeCommands;
	}

}

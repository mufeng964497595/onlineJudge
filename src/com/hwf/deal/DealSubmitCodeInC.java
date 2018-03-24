package com.hwf.deal;

import java.util.ArrayList;

/**
 * 处理C语言提交的代码
 */
public class DealSubmitCodeInC extends DealSubmitCode {
	private final String sourceFileName = "Main.c";
	private final String outFileName = "Main";

	DealSubmitCodeInC(String cid, String pno, String code, long runId, int timeLimit, int memLimit) {
		super(cid, pno, code, runId, timeLimit, memLimit);
	}

	@Override
	protected String getCodeFileName() {
		return sourceFileName;
	}

	@Override
	protected String getCompileFileName() {
		return outFileName;
	}

	@Override
	protected ArrayList<String> getCompileCommands() {
		ArrayList<String> compileCommands = new ArrayList<>();
		/*用docker编译程序*/
		compileCommands.add(compileShell);
		compileCommands.add(String.format("%s:%s", codeDir, codeDir));
		compileCommands.add("gcc:7");
		compileCommands.add(String.format("cd %s&&gcc %s -o %s", codeDir, sourceFileName, outFileName));

		return compileCommands;
	}

	@Override
	protected ArrayList<String> getExecuteCommands() {
		ArrayList<String> executeCommands = new ArrayList<>();
		/*用docker执行程序*/
		executeCommands.add(executeShell);
		executeCommands.add(String.format("%s:%s", codeDir, codeDir));
		executeCommands.add("gcc:7");
		executeCommands.add(String.format("cd %s&&./%s", codeDir, outFileName));
		executeCommands.add(timeLimit + "");
		executeCommands.add(memLimit + "");

		return executeCommands;
	}

}

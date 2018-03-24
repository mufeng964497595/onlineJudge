package com.hwf.deal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import com.hwf.bean.InputOutputDataBean;
import com.hwf.util.ResultType;

/**
 * 处理提交代码的抽象类，进行生成本地文件、编译、运行、评判的操作
 */
public abstract class DealSubmitCode {
	private String code;
	String codeDir;
	private String codeFileName;
	private String compileFileName;
	private boolean isCompiled = false;
	int timeLimit, memLimit;
	private ArrayList<String> compileCommands;
	private ArrayList<String> executeCommands;

	final String compileShell = "/onlineJudge/docker/compile";
	final String executeShell = "/onlineJudge/docker/run";

	DealSubmitCode(String cid, String pno, String code, long runId, int timeLimit, int memLimit) {
		this.code = code;
		this.timeLimit = timeLimit;
		this.memLimit = memLimit;

		/*生成文件路径及文件名，避免重复*/
		String tmpDir = "/onlineJudge/judge";
		this.codeDir = String.format("%s/%s/%s/%s/", tmpDir, cid, pno, runId);
		this.codeFileName = String.format("%s/%s", codeDir, getCodeFileName());
		this.compileFileName = String.format("%s/%s", codeDir, getCompileFileName());

		/*获取编译、运行指令*/
		this.compileCommands = getCompileCommands();
		this.executeCommands = getExecuteCommands();
	}

	public String compile() {
		File codeFile = new File(codeFileName);
		if (!codeFile.exists()) {
			codeFile.getParentFile().mkdirs();
			try {
				codeFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			FileWriter fw = new FileWriter(codeFile);
			fw.write(code);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ProcessBuilder processBuilder = new ProcessBuilder(compileCommands);
		processBuilder.directory(new File(codeDir));
		processBuilder.redirectErrorStream(true);
		StringBuilder result = new StringBuilder();

		try {
			Process process = processBuilder.start();
			InputStreamReader isr = new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(isr);

			String line;
			while ((line = br.readLine()) != null) {
				result.append(line).append("\n");
			}
			br.close();
			isr.close();

			File file = new File(compileFileName);
			isCompiled = file.exists();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		codeFile.delete();

		return result.toString().isEmpty() ? null : result.toString();
	}

	public int executeCode(ArrayList<InputOutputDataBean> inputOutputDatas) {
		// TODO tle mem re
		if (!isCompiled) {
			return ResultType.CE;
		}

		int result = ResultType.AC;
		for (InputOutputDataBean bean : inputOutputDatas) {
			result = getExecuteResult(bean.getInputData(), bean.getOutputData());
			if (result != ResultType.AC)
				break;
		}

		/*删除文件*/
		File compileFile = new File(compileFileName);
		compileFile.delete();

		return result;
	}

	private int getExecuteResult(String inputData, String outputData) {
		ProcessBuilder processBuilder = new ProcessBuilder(executeCommands);
		processBuilder.directory(new File(codeDir));
		processBuilder.redirectErrorStream(true);
		Process process;
		StringBuilder runResult;

		int result;
		try {
			process = processBuilder.start();
			OutputStream os = process.getOutputStream();
			os.write(inputData.getBytes("UTF-8"));
			os.flush();
			os.close();

			runResult = new StringBuilder();
			Scanner in = new Scanner(process.getInputStream());
			while (in.hasNextLine()) {
				runResult.append(in.nextLine()).append("\n");
			}
			in.close();

			if (runResult.toString().isEmpty()) {// 系统错误
				result = ResultType.SE;
			} else {
				String output = runResult.toString();
				output = output.substring(0, output.lastIndexOf("\n"));
				String res = output.substring(output.length() - 1);
				output = output.substring(0, output.length() - 1);

				int exitVal = Integer.parseInt(res);
				if (exitVal == 0) {
					if (output.equals(outputData)) {
						result = ResultType.AC;
					} else {
						result = ResultType.WA;
					}
				} else if (exitVal == 3) {
					result = ResultType.TLE;
				} else if (exitVal == 4) {
					result = ResultType.MLE;
				} else {
					result = ResultType.RE;
				}
			}
		} catch (Exception e) {
			result = ResultType.SE;
			e.printStackTrace();
		}

		return result;
	}

	protected abstract String getCodeFileName();

	protected abstract String getCompileFileName();

	protected abstract ArrayList<String> getCompileCommands();

	protected abstract ArrayList<String> getExecuteCommands();

}

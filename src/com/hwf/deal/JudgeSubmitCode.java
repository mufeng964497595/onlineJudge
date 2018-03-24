package com.hwf.deal;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.hwf.dao.ConnectionDao;
import com.hwf.dao.GetInfoFromDatabaseDao;
import com.hwf.util.ResultType;

/**
 * 评判提交的代码
 */
public class JudgeSubmitCode {
	private String cid, pno;
	private String code;
	private long runId;
	private int timeLimit, memLimit;
	private String compileResult;

	public JudgeSubmitCode(String cid, String pno, String code, long runId) {
		this.cid = cid;
		this.pno = pno;
		this.code = code;
		this.runId = runId;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call showProblem(?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			ps.setString(2, pno);
			rs = ps.executeQuery();

			if (rs.next()) {
				this.timeLimit = rs.getInt(3);
				this.memLimit = rs.getInt(4);
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.compileResult = null;
	}

	public void testCCode() {
		DealSubmitCodeInC deal = new DealSubmitCodeInC(cid, pno, code, runId, timeLimit, memLimit);
		testCode(deal);
	}

	public void testCppCode() {
		DealSubmitCodeInCpp deal = new DealSubmitCodeInCpp(cid, pno, code, runId, timeLimit, memLimit);
		testCode(deal);
	}

	public void testJavaCode() {
		DealSubmitCodeInJava deal = new DealSubmitCodeInJava(cid, pno, code, runId, timeLimit, memLimit);
		testCode(deal);
	}

	private void testCode(DealSubmitCode deal) {
		int result;

		compileResult = deal.compile();
		if (compileResult != null) {
			result = ResultType.CE;
		} else {
			result = deal.executeCode(GetInfoFromDatabaseDao.getInputOutputData(cid, pno));
		}

		updateSubmitResult(result + "");
	}

	private void updateSubmitResult(String result) {
		/*更新数据库*/
		ConnectionDao conn = null;
		PreparedStatement ps = null;

		try {
			conn = new ConnectionDao();
			conn.connection();

			String sql = "{call updateSubmitResult(?,?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setLong(1, runId);
			ps.setString(2, result);
			ps.setString(3, compileResult);
			ps.executeQuery();
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}

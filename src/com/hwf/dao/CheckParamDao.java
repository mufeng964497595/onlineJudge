package com.hwf.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * 工具类，用以检查参数
 */
public class CheckParamDao {

	public static boolean checkCid(String cid) throws ParserConfigurationException {
		if (cid == null || "".equals(cid))
			return false;

		boolean result = false;
		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call isCidCorrect(?)}";

			ps = conn.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(cid));
			rs = ps.executeQuery();
			if (rs.next() && rs.getInt(1) == 1) {
				return true;
			}

		} catch (ClassNotFoundException | SQLException | NumberFormatException | SAXException | IOException e) {
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
				e.printStackTrace();
			}
		}

		return result;
	}

	public static boolean checkPno(String cid, String pno) {
		if (cid == null || pno == null)
			return false;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		boolean result = false;
		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call isPnoCorrect(?,?)}";

			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			ps.setString(2, pno);
			rs = ps.executeQuery();
			if (rs.next() && rs.getInt(1) == 1) {
				result = true;
			}

		} catch (ClassNotFoundException | SQLException | NumberFormatException | ParserConfigurationException | SAXException | IOException e) {
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
				e.printStackTrace();
			}
		}

		return result;
	}

	public static boolean isUserInfoCorrect(String username, String passwd, String email) {
		if (username == null || passwd == null || email == null)
			return false;

		boolean result = false;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call checkUserInfo(?,?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, passwd);
			ps.setString(3, email);

			System.out.println(username + "---" + passwd + "---" + email);

			rs = ps.executeQuery();
			if (rs.next() && rs.getInt(1) == 1) {
				result = true;
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

		return result;
	}
}

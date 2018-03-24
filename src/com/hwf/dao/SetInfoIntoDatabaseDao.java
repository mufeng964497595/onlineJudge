package com.hwf.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * 工具类，向数据库添加信息
 */
public class SetInfoIntoDatabaseDao {
	public static void addProblemTestData(String pid, String inputData, String outputData) {
		if (pid == null || inputData == null || outputData == null)
			return;
		ConnectionDao conn = null;
		PreparedStatement ps = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call addProblemTestData(?,?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pid);
			ps.setString(2, inputData);
			ps.setString(3, outputData);

			ps.executeQuery();
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void updateProblemInputData(String id, String inputData) {
		if (id == null || inputData == null)
			return;
		ConnectionDao conn = null;
		PreparedStatement ps = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call updateProblemInputData(?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, inputData);

			ps.executeQuery();
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void updateProblemOutputData(String id, String outputData) {
		if (id == null || outputData == null)
			return;
		ConnectionDao conn = null;
		PreparedStatement ps = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call updateProblemOutputData(?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, outputData);

			ps.executeQuery();
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void deleteTestData(String id) {
		if (id == null)
			return;

		ConnectionDao conn = null;
		PreparedStatement ps = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call deleteTestData(?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);

			ps.executeQuery();
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void updateUserNickname(String username, String nickname) throws ClassNotFoundException, SQLException, ParserConfigurationException, SAXException, IOException {
		ConnectionDao conn;
		PreparedStatement ps;

		conn = new ConnectionDao();
		conn.connection();
		String sql = "{call updateUserNickname(?,?)}";
		ps = conn.prepareStatement(sql);
		ps.setString(1, username);
		ps.setString(2, nickname);

		ps.executeQuery();
		ps.close();
		conn.close();

	}

	public static void updateUserPassword(String username, String password) throws ClassNotFoundException, SQLException, ParserConfigurationException, SAXException, IOException {
		ConnectionDao conn;
		PreparedStatement ps;

		conn = new ConnectionDao();
		conn.connection();
		String sql = "{call updateUserPassword(?,?)}";
		ps = conn.prepareStatement(sql);
		ps.setString(1, username);
		ps.setString(2, password);

		ps.executeQuery();
		ps.close();
		conn.close();
	}
}

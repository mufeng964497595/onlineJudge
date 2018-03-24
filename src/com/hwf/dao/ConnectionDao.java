package com.hwf.dao;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hwf.listener.ConfigListener;

public class ConnectionDao {

	/**
	 * 连接数据库
	 */
	private Connection conn = null;

	public void connection() throws ClassNotFoundException, SQLException, ParserConfigurationException, SAXException, IOException {
		// 读取databaseInfo.xml中数据库相关的信息
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(ConfigListener.getDatabaseInfoXmlPath()));

		NodeList list = document.getElementsByTagName("driverClassName");
		String driverClassName = ((Element) list.item(0)).getAttribute("value");

		list = document.getElementsByTagName("dbUrl");
		String dbUrl = ((Element) list.item(0)).getAttribute("value");

		list = document.getElementsByTagName("dbUser");
		String dbUser = ((Element) list.item(0)).getAttribute("value");

		list = document.getElementsByTagName("dbPasswd");
		String dbPasswd = ((Element) list.item(0)).getAttribute("value");

		// System.out.println(ConfigListener.getDatabaseInfoXmlPath() + "----");

		Class.forName(driverClassName);
		conn = DriverManager.getConnection(dbUrl, dbUser, dbPasswd);
	}

	public void close() throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}

	public Statement createStatement() throws SQLException {
		return conn.createStatement();
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}
}

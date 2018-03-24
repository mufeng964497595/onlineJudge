package com.hwf.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 监听databaseInfo.xml这个文件，获取文件的绝对路径
 */
@WebListener
public class ConfigListener implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	private static String databaseInfoXmlPath = "";

	public static String getDatabaseInfoXmlPath() {
		return databaseInfoXmlPath;
	}

	public ConfigListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {
		databaseInfoXmlPath = sce.getServletContext().getRealPath("WEB-INF/databaseInfo.xml");
	}

}

package com.parkit.parkingsystem.integration.config;

import java.sql.*;

import org.apache.logging.log4j.*;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;

public class DataBaseTestConfig extends DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		return DriverManager.getConnection(DBConstants.MYSQL_URL_TEST, DBConstants.MYSQL_USER,
				DBConstants.MYSQL_PASSWORD);
	}

}

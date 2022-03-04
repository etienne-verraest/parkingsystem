package com.parkit.parkingsystem.integration.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;

public class DataBaseTestConfig extends DataBaseConfig {

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		return DriverManager.getConnection(DBConstants.MYSQL_URL_TEST, DBConstants.MYSQL_USER,
				DBConstants.MYSQL_PASSWORD);
	}

}

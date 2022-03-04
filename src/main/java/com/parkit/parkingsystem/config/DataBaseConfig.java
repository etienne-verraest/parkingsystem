package com.parkit.parkingsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.DBConstants;

public class DataBaseConfig {

	private static final Logger LOGGER = LogManager.getLogger(DataBaseConfig.class);

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		return DriverManager.getConnection(DBConstants.MYSQL_URL, DBConstants.MYSQL_USER, DBConstants.MYSQL_PASSWORD);
	}

	// Order of closing : Result set (1) , Prepared Statement (2), Connection (3)
	// Close connection
	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error("Error while closing connection", e);
			}
		}
	}

	// Close statement
	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				LOGGER.error("Error while closing prepared statement", e);
			}
		}
	}

	// Close result-set
	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				LOGGER.error("Error while closing result set", e);
			}
		}
	}
}

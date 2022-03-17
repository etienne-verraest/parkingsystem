package com.parkit.parkingsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.util.ApplicationProperties;

/**
 * 
 * This class handles all the database functionalities
 *
 */
public class DataBaseConfig {

	private static final Logger LOGGER = LogManager.getLogger(DataBaseConfig.class);

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		return DriverManager.getConnection(DBConstants.MYSQL_URL, ApplicationProperties.INSTANCE.getDatabaseUsername(),
				ApplicationProperties.INSTANCE.getDatabasePassword());
	}

	/**
	 * Close a SQL connection
	 * 
	 * @param con is an object of type Connection
	 */
	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				LOGGER.error("Error while closing connection", e);
			}
		}
	}

	/**
	 * Close a SQL prepared statement
	 * 
	 * @param ps is an object of type PreparedStatement
	 */
	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				LOGGER.error("Error while closing prepared statement", e);
			}
		}
	}

	/**
	 * Close a SQL result set
	 * 
	 * @param rs is an object of type ResultSet
	 */
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

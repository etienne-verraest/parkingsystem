package com.parkit.parkingsystem.integration.config;

import org.apache.logging.log4j.*;

public class DataBaseTestConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");

//	public Connection getConnection() throws ClassNotFoundException, SQLException {
//		return DriverManager.getConnection(DBConstants.MYSQL_URL_TEST, DBConstants.MYSQL_USER,
//				DBConstants.MYSQL_PASSWORD);
//	}
//
//	public void closeConnection(Connection con) {
//		if (con != null) {
//			try {
//				con.close();
//				logger.info("Closing DB connection");
//			} catch (SQLException e) {
//				logger.error("Error while closing connection", e);
//			}
//		}
//	}
//
//	public void closePreparedStatement(PreparedStatement ps) {
//		if (ps != null) {
//			try {
//				ps.close();
//				logger.info("Closing Prepared Statement");
//			} catch (SQLException e) {
//				logger.error("Error while closing prepared statement", e);
//			}
//		}
//	}
//
//	public void closeResultSet(ResultSet rs) {
//		if (rs != null) {
//			try {
//				rs.close();
//				logger.info("Closing Result Set");
//			} catch (SQLException e) {
//				logger.error("Error while closing result set", e);
//			}
//		}
//	}

}

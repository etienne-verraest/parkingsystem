package com.parkit.parkingsystem.integration.service;

import java.sql.Connection;

import org.apache.logging.log4j.*;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

public class DataBasePrepareService {

	private static final Logger logger = LogManager.getLogger("DataBasePrepareService");
	private static DataBaseTestConfig dataBaseConfig = new DataBaseTestConfig();

	public void clearDataBaseEntries() {
		Connection connection = null;
		try {
			connection = dataBaseConfig.getConnection();

			// set parking entries to available
			connection.prepareStatement("update parking set available = true").execute();

			// clear ticket entries;
			connection.prepareStatement("truncate table ticket").execute();

			logger.info("Entries in test database have been cleaned");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dataBaseConfig.closeConnection(connection);
		}
	}

}

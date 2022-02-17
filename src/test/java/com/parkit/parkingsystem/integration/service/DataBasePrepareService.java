package com.parkit.parkingsystem.integration.service;

import java.sql.Connection;

import com.parkit.parkingsystem.config.DataBaseConfig;

public class DataBasePrepareService {

	private static DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public void clearDataBaseEntries() {
		Connection connection = null;
		try {
			connection = dataBaseConfig.getConnection();

			// set parking entries to available
			connection.prepareStatement("update parking set available = true").execute();

			// clear ticket entries;
			connection.prepareStatement("truncate table ticket").execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dataBaseConfig.closeConnection(connection);
		}
	}

}

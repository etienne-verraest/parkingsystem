package com.parkit.parkingsystem.integration.config;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;

@ExtendWith(MockitoExtension.class)
public class DataBaseConfigTest {

	private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");

	@Mock
	DataBaseConfig dataBaseConfig;

	@Mock
	Connection connection;

	@Test
	@DisplayName("Check for MySQL connection")
	void connectTo_MySQL() throws Exception {

		dataBaseConfig.getConnection();
		verify(dataBaseConfig, times(1)).getConnection();
	}

	@Test
	@DisplayName("Check for MySQL close connection")
	void disconnectFrom_MySQL() throws Exception {

		dataBaseConfig.closeConnection(connection);
		verify(dataBaseConfig, times(1)).closeConnection(connection);

	}

}

package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.*;

import org.apache.logging.log4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;

@ExtendWith(MockitoExtension.class)
class TicketDAOTest {

	private static final Logger logger = LogManager.getLogger("TicketDAOTest");
	private static DataBasePrepareService dataBasePrepareService;

	private static DataBaseTestConfig databaseTestConfig = new DataBaseTestConfig();

	private static TicketDAO ticketDAO;

	static Connection con;

	@BeforeAll
	static void setUp_TestEnvironment() throws Exception {

		// Set Up DataBase Test environment
		con = databaseTestConfig.getConnection();
		logger.info("Test environment database has been set up");

		// Initialize our DAO with test environment
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = databaseTestConfig;

		// Clearing previous tested entries
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	static void tearDown_TestEnvironment() throws Exception {
		con.close();
	}

	@Test
	@DisplayName("Vehicle is not a regular one")
	void checkIfVehicleIsRegular_ReturnsFalse() throws Exception {
		// ARRANGE
		String PLATE_ID = "ABCDEF";
		boolean expectedValue = false;

		// ACT
		boolean actualValue = ticketDAO.checkIfVehicleIsRegular(PLATE_ID);

		// ASSERT
		assertEquals(expectedValue, actualValue);
	}

	@Test
	@DisplayName("Vehicle is regular")
	void checkIfVehicleIsRegular_ReturnsTrue() throws Exception {
		// ARRANGE
		// Creating a ticket in test environment
		Timestamp timestampIn = new Timestamp(1645953567);
		Timestamp timestampOut = new Timestamp(1645971571);
		String PLATE_ID = "QSDFG";

		PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
		ps.setInt(1, 3);
		ps.setString(2, PLATE_ID);
		ps.setDouble(3, 15.0);
		ps.setTimestamp(4, timestampIn);
		ps.setTimestamp(5, timestampOut);
		ps.execute();
		boolean expectedValue = true;

		// ACT
		boolean actualValue = ticketDAO.checkIfVehicleIsRegular(PLATE_ID);

		// ASSERT
		assertEquals(expectedValue, actualValue);

	}

}

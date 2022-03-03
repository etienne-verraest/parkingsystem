package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;

import org.apache.logging.log4j.*;
import org.junit.jupiter.api.*;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;

class ParkingSpotDAOTest {

	private static final Logger logger = LogManager.getLogger("ParkingSpotDAOTest");

	private static DataBasePrepareService dataBasePrepareService;

	private static DataBaseTestConfig databaseTestConfig = new DataBaseTestConfig();

	private static ParkingSpotDAO parkingSpotDAO;

	static Connection con;

	@BeforeAll
	static void setUp_testEnvironment() throws Exception {

		// Set Up DataBase Test environment
		con = databaseTestConfig.getConnection();
		logger.info("Test environment database has been set up");

		// Initialize our DAO with test environment
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = databaseTestConfig;

		// Clearing previous tested entries
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	static void tearDown_testEnvironment() throws Exception {
		con.close();
	}

	@Test
	@DisplayName("Getting number one slot when checking for availability")
	void getNextAvailableSlot_shouldReturnInteger_slotNumberOne() throws Exception {
		// ARRANGE
		ParkingSpot parkingSpot = new ParkingSpot(0, ParkingType.CAR, true);
		int expectedValue = 1;

		// ACT
		int actualValue = parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());

		// ASSERT
		assertEquals(expectedValue, actualValue);
	}

	@Test
	@DisplayName("Updating parking should return true")
	void updateParking_shouldReturnTrue() throws Exception {
		// ARRANGE
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		parkingSpot.setAvailable(true);
		boolean expectedValue = true;

		// ACT
		boolean actualValue = parkingSpotDAO.updateParking(parkingSpot);

		// ASSERT
		assertEquals(expectedValue, actualValue);
	}

}

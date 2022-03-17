package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

class TicketDAOTest {

	private static final String REGISTERED_PLATE = "QSDFG";
	private static final String NEW_VEHICLE_PLATE = "NEWVE";

	private static final Logger LOGGER = LogManager.getLogger(TicketDAOTest.class);
	private static DataBasePrepareService dataBasePrepareService;

	private static DataBaseConfig databaseTestConfig = new DataBaseTestConfig();

	private static TicketDAO ticketDAO;

	static Connection con;

	@BeforeAll
	static void setUp_testEnvironment() throws Exception {

		// Set Up DataBase Test environment
		con = databaseTestConfig.getConnection();
		LOGGER.info("Test environment database has been set up");

		// Initialize our DAO with test environment
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = databaseTestConfig;

		// Clearing previous tested entries
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();

		// CREATING TEST ENTRIES IN DATABASE
		Date inTime = new Date(System.currentTimeMillis());
		Date outTime = new Date(System.currentTimeMillis() + (60 * 60 * 1000));

		Timestamp timestampIn = new Timestamp(inTime.getTime());
		Timestamp timestampOut = new Timestamp(outTime.getTime());

		// Query : Save a ticket in database to check if vehicle is regular or not
		PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
		ps.setInt(1, 1);
		ps.setString(2, REGISTERED_PLATE);
		ps.setDouble(3, 1.0);
		ps.setTimestamp(4, timestampIn);
		ps.setTimestamp(5, timestampOut);
		ps.execute();
	}

	@Nested
	@Tag("Regular")
	@DisplayName("Testing if a vehicle is regular")
	class checkVehicleIsRegularTests {

		@Test
		@DisplayName("Vehicle is not a regular one")
		void checkIfVehicleIsRegular_shouldReturnFalse() throws Exception {
			// ARRANGE
			boolean expectedValue = false;

			// ACT
			boolean actualValue = ticketDAO.checkIfVehicleIsRegular("ABCDE");

			// ASSERT
			assertEquals(expectedValue, actualValue);
		}

		@Test
		@DisplayName("Vehicle is regular")
		void checkIfVehicleIsRegular_shouldReturnTrue() throws Exception {
			// ARRANGE
			boolean expectedValue = true;

			// ACT
			boolean actualValue = ticketDAO.checkIfVehicleIsRegular(REGISTERED_PLATE);

			// ASSERT
			assertEquals(expectedValue, actualValue);

		}
	}

	@Test
	@DisplayName("Ticket is saved in database and returns VEHICLE_REG_NUMBER")
	void saveTicket_shouldReturnTrue() throws Exception {
		// ARRANGE
		Ticket ticket = new Ticket();
		Date inTime = new Date();

		ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR, true);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber(NEW_VEHICLE_PLATE);
		ticket.setPrice(0.0);
		ticket.setInTime(inTime);
		ticket.setOutTime(null);

		// ACT
		ticketDAO.saveTicket(ticket);
		String actualPlate = ticketDAO.getTicket(NEW_VEHICLE_PLATE).getVehicleRegNumber();

		// ASSERT
		assertEquals(NEW_VEHICLE_PLATE, actualPlate);
	}

	@Test
	@DisplayName("Ticket is updated in database")
	void updateTicket_shouldReturnTrue() throws Exception {
		// ARRANGE
		Ticket ticket = new Ticket();
		ticket = ticketDAO.getTicket(REGISTERED_PLATE);
		boolean expectedValue = true;

		// ACT
		boolean actualValue = ticketDAO.updateTicket(ticket);

		// ASSERT
		assertEquals(expectedValue, actualValue);
	}

	@Nested
	@Tag("Checks")
	@DisplayName("Vehicle check plate when entering or exiting")
	class checkVehicleWhenEnteringOrExiting {

		@Test
		@DisplayName("Check if vehicle has already parked out")
		void checkAlreadyOut_shouldReturnTrue() throws Exception {
			// ARRANGE
			boolean expectedValue = true;

			// ACT
			boolean actualValue = ticketDAO.checkIfUserHasAlreadyParkedOut(REGISTERED_PLATE);

			// ASSERT
			assertEquals(expectedValue, actualValue);
		}

		@Test
		@DisplayName("Check if vehicle is parked in")
		void checkAlreadyIn_shouldReturnTrue() throws Exception {
			// ARRANGE
			boolean expectedValue = true;

			// ACT
			boolean actualValue = ticketDAO.checkIfUserIsAlreadyIn(NEW_VEHICLE_PLATE);

			// ASSERT
			assertEquals(expectedValue, actualValue);
		}
	}
}

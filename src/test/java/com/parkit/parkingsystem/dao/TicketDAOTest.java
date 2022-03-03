package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.*;
import java.util.Date;

import org.apache.logging.log4j.*;
import org.junit.jupiter.api.*;

import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.*;

class TicketDAOTest {

	private static final String REGISTERED_PLATE = "QSDFG";
	private static final String UNREGISTERED_PLATE = "ABCDE";
	private static final String NEW_VEHICLE_PLATE = "NEWVE";

	private static final Logger logger = LogManager.getLogger("TicketDAOTest");
	private static DataBasePrepareService dataBasePrepareService;

	private static DataBaseTestConfig databaseTestConfig = new DataBaseTestConfig();

	private static TicketDAO ticketDAO;

	static Connection con;

	@BeforeAll
	static void setUp_testEnvironment() throws Exception {

		// Set Up DataBase Test environment
		con = databaseTestConfig.getConnection();
		logger.info("Test environment database has been set up");

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

	@AfterAll
	static void tearDown_testEnvironment() throws Exception {
		con.close();
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
			boolean actualValue = ticketDAO.checkIfVehicleIsRegular(UNREGISTERED_PLATE);

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

}

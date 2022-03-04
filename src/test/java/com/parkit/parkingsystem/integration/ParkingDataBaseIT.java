package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static final String VEHICLE_PLATE_INTEGRATION = "INT-VEH";

	private static DataBaseConfig dataBaseConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {

		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseConfig;

		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseConfig;

		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_PLATE_INTEGRATION);
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@Test
	@DisplayName("Check that ticket is saved in database and parking table is updated")
	public void testParkingACar() throws Exception {
		// Initialize parking service (inputReaderUtil is a mock)
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// Parking Spot arguments : Slot ID, Type, Availability
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

		// Ticket arguments : Parking Spot, Vehicle Reg. Number, Price, In Time,
		// + outTime to null (user entered parking lot)
		Ticket ticket = new Ticket();

		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber(VEHICLE_PLATE_INTEGRATION);
		ticket.setPrice(0);
		ticket.setInTime(new Date());
		ticket.setOutTime(null);

		// Process incoming vehicle
		parkingService.processIncomingVehicle();

		// Check if ticket is registered in database with getTicket
		ticket = ticketDAO.getTicket(VEHICLE_PLATE_INTEGRATION);
		assertEquals(VEHICLE_PLATE_INTEGRATION, ticket.getVehicleRegNumber());

		// Check if the next available slot is 2 (which means availability has changed)
		int actualSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
		int expectedSlot = 2;
		assertEquals(expectedSlot, actualSlot);
	}

	@Test
	@DisplayName("Check that the fare generated and out time are populated correctly in the database")
	public void testParkingLotExit() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// Process entering vehicle so that we can exit it
		testParkingACar();

		// Process exiting vehicle
		parkingService.processExitingVehicle();

		// Getting ticket from database
		Ticket ticket = ticketDAO.getTicket(VEHICLE_PLATE_INTEGRATION);

		// Ticket and out time shouldn't be null
		assertAll(() -> assertNotNull(ticket), () -> assertNotNull(ticket.getOutTime()));

		// Parking should be free (Dates are almost equal)
		assertEquals(0.0, ticket.getPrice());

	}

}

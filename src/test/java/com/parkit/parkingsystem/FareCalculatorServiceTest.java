package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.apache.logging.log4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.model.*;
import com.parkit.parkingsystem.service.FareCalculatorService;

@DisplayName("Calculate fare for cars and bikes")
public class FareCalculatorServiceTest {

	private static final Logger logger = LogManager.getLogger("FareCalculatorServiceTest");

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	// Call our service before executing tests
	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	// Create a new ticket before every test
	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@Nested
	@Tag("Car")
	@DisplayName("Fare Calculator for Cars")
	class FareCaculatorForCars {

		@DisplayName("Calculate fare with less than one hour of parking time.")
		@ParameterizedTest(name = "{0} minutes should cost {1} €")
		@CsvSource({ "45,1.125", "58,1.45" })
		public void calculateFareCar_WithLessThanOneHourParkingTime(double minutes, double expectedPrice) {
			Date inTime = new Date();

			inTime.setTime((long) (System.currentTimeMillis() - (minutes * 60 * 1000)));

			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			fareCalculatorService.calculateFare(ticket);
			assertEquals(((minutes / 60) * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
		}

		@DisplayName("Calculate fare with more than a day of parking time.")
		@Test
		public void calculateFareCar_WithMoreThanADayParkingTime() {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			fareCalculatorService.calculateFare(ticket);
			assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
		}

	}

	@Nested
	@Tag("Bike")
	@DisplayName("Fare Calculator for Bikes")
	class FareCalculatorForBikes {

		@Test
		@DisplayName("Calculate fare with exactly one hour of parking time")
		void calculateFareBike() {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			fareCalculatorService.calculateFare(ticket);
			assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
		}

		@Test
		@DisplayName("Calculate fare with less than one hour of parking time.")
		void calculateFareBike_WithLessThanOneHourParkingTime() {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));

			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			fareCalculatorService.calculateFare(ticket);
			assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
		}
	}

	@Test
	@DisplayName("Vehicle Type is unknown")
	void calculateFare_UnkownType() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.DEFAULT, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	@DisplayName("Incorrect ticket dates should throw exception")
	@Disabled
	public void calculateFare_WithAfterInTime() {

		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));

		Date outTime = new Date();
		outTime.setTime(System.currentTimeMillis());

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		Executable executable = () -> fareCalculatorService.calculateFare(ticket);

		assertThrows(IllegalArgumentException.class, executable);
	}

	@Test
	@DisplayName("Fare should be free if parking is less than 30 minutes")
	void calculateFare_WithLessThan30Minutes() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000));

		Date outTime = new Date();
		outTime.setTime(System.currentTimeMillis());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(0, ticket.getPrice());
	}

	@Test
	@DisplayName("Calculating fare but out time is null")
	void calculateFare_WithNullOutTime() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = null;

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}
}

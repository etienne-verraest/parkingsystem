package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

@DisplayName("Calculate fare for cars and bikes")
public class FareCalculatorServiceTest {

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

		@Test
		public void calculateFareCar() {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			fareCalculatorService.calculateFare(ticket);
			assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
		}

		@DisplayName("Calculate fare with less than one hour of parking time.")
		@ParameterizedTest(name = "{0} minutes should cost {1} €")
		@CsvSource({ "30,0.75", "45,1.125", "58,1.45" })
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
		public void calculateFareBike() {
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
		public void calculateFare_BikeWithFutureInTime() {
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);
			assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
		}

		@Test
		@Disabled
		@DisplayName("Calculate fare with less than one hour of parking time.")
		public void calculateFareBike_WithLessThanOneHourParkingTime() {
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

	@Disabled
	@Test
	public void calculateFare_UnkownType() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}
}

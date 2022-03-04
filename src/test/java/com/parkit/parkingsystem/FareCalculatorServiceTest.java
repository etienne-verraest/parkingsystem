package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

		@DisplayName("Calculate fare with less than one hour of parking time.")
		@ParameterizedTest(name = "{0} minutes should cost {1} €")
		@CsvSource({ "45,1.125", "58,1.45" })
		void calculateFareCar_WithLessThanOneHourParkingTime(double minutes, double expectedPrice) {

			// ARRANGE
			Date inTime = new Date();
			inTime.setTime((long) (System.currentTimeMillis() - (minutes * 60 * 1000)));

			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// ACT
			fareCalculatorService.calculateFare(ticket, false);

			// ASSERT
			assertEquals(((minutes / 60) * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
		}

		@DisplayName("Calculate fare with more than a day of parking time.")
		@Test
		void calculateFareCar_WithMoreThanADayParkingTime() {
			// ARRANGE
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (48 * 60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// ACT
			fareCalculatorService.calculateFare(ticket, false);

			// ASSERT
			assertEquals((48 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
		}

	}

	@Nested
	@Tag("Bike")
	@DisplayName("Fare Calculator for Bikes")
	class FareCalculatorForBikes {

		@Test
		@DisplayName("Calculate fare with exactly one hour of parking time")
		void calculateFareBike() {
			// ARRANGE
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// ACT
			fareCalculatorService.calculateFare(ticket, false);

			// ASSERT
			assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
		}

		@Test
		@DisplayName("Calculate fare with less than one hour of parking time.")
		void calculateFareBike_WithLessThanOneHourParkingTime() {
			// ARRANGE
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));

			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// ACT
			fareCalculatorService.calculateFare(ticket, false);

			// ASSERT
			assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
		}
	}

	@Nested
	@Tag("Discount")
	@DisplayName("Fare calculator for free parking and 5% discounts")
	class calculateFare_WithDiscount {

		@Test
		@DisplayName("Fare should be free if parking is less than 30 minutes")
		void calculateFare_WithLessThan30Minutes() {
			// ARRANGE
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000));

			Date outTime = new Date();
			outTime.setTime(System.currentTimeMillis());
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// ACT
			fareCalculatorService.calculateFare(ticket, false);

			// ASSERT
			assertEquals(0, ticket.getPrice());
		}

		@Test
		@DisplayName("User has a 5% discount coupon if it's a regular user")
		void calculateFare_With5PercentDiscount() {
			// ARRANGE
			Date inTime = new Date();
			inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));

			Date outTime = new Date();
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			// ACT
			fareCalculatorService.calculateFare(ticket, true);
			double priceDiscount = 0.75 * Fare.CAR_RATE_PER_HOUR;
			priceDiscount = priceDiscount - (priceDiscount * 0.05);

			// ASSERT
			assertEquals(priceDiscount, ticket.getPrice());
		}
	}

	@Test
	@DisplayName("Vehicle Type is unknown")
	void calculateFare_UnkownType() {
		// ARRANGE
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.UNKNOWN, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// ACT & ASSERT
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, false));
	}

	@Test
	@DisplayName("Calculating fare but out time is null")
	void calculateFare_WithNullOutTime() {
		// ARRANGE
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = null;

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// ACT & ASSERT
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, false));
	}
}

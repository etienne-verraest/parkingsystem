package com.parkit.parkingsystem.service;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * 
 * This class handles the logic of the parking. It process entering and exiting
 * vehicles
 *
 */
public class ParkingService {

	private static final Logger LOGGER = LogManager.getLogger(ParkingService.class);

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;

	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
	}

	/**
	 * This method register an incoming vehicle
	 */
	public void processIncomingVehicle() {

		try {
			String vehicleRegNumber = getVehichleRegNumber();

			// We check if a plate is already registered in the tickets, if it's not
			// algorithm will continue
			if (ticketDAO.checkIfUserIsAlreadyIn(vehicleRegNumber) == false) {

				// Getting next parking spot, if not available returns an error
				ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();

				if (parkingSpot != null && parkingSpot.getId() > 0) {

					// If our user is a regular one, we welcome him with a personalized message
					if (ticketDAO.checkIfVehicleIsRegular(vehicleRegNumber)) {
						System.out.println(
								"Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
					}

					// Allot this parking space and mark it's availability as false
					parkingSpot.setAvailable(false);
					parkingSpotDAO.updateParking(parkingSpot);

					Date inTime = new Date();
					Ticket ticket = new Ticket();

					// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(vehicleRegNumber);
					ticket.setPrice(0);
					ticket.setInTime(inTime);
					ticket.setOutTime(null);
					ticketDAO.saveTicket(ticket);

					System.out.println("Please park your vehicle in spot number : " + parkingSpot.getId());
					System.out.println("Recorded in-time for vehicle number : " + vehicleRegNumber + " is : " + inTime);
				}
			} else {
				System.out.println("This vehicle is already parked, did you want to exit vehicle instead ?");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to process incoming vehicle", e);
		}
	}

	/**
	 * This method returns a vehicle registration number
	 * 
	 * @return String of a vehicle registration number
	 * 
	 */
	public String getVehichleRegNumber() throws Exception {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	/**
	 * This method checks if there is an available parking spot
	 * 
	 * @return a parking spot
	 */
	public ParkingSpot getNextParkingNumberIfAvailable() {
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;

		try {
			ParkingType parkingType = getVehichleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);

			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
			} else {
				System.out.println("Error fetching parking number from database. Parking slots might be full");
			}

		} catch (IllegalArgumentException ie) {
			LOGGER.error("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			LOGGER.error("Error fetching next available parking slot", e);
		}

		return parkingSpot;
	}

	/**
	 * This method returns a vehicle type, based on user's input
	 * 
	 * @return Vehicle Type (Car, Bike)
	 */
	public ParkingType getVehichleType() {
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");

		int input = inputReaderUtil.readSelection();
		switch (input) {
		case 1: {
			return ParkingType.CAR;
		}
		case 2: {
			return ParkingType.BIKE;
		}
		default: {
			throw new IllegalArgumentException("Entered input is invalid");
		}
		}
	}

	/**
	 * This method process an exiting vehicle
	 */
	public void processExitingVehicle() {

		try {

			String vehicleRegNumber = getVehichleRegNumber();
			if (ticketDAO.checkIfUserHasAlreadyParkedOut(vehicleRegNumber) == false) {

				boolean isRegular = ticketDAO.checkIfVehicleIsRegular(vehicleRegNumber);

				Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
				ticket.setOutTime(new Date());

				fareCalculatorService.calculateFare(ticket, isRegular);

				if (ticketDAO.updateTicket(ticket)) {

					// Set used parking spot to available
					ParkingSpot parkingSpot = ticket.getParkingSpot();
					parkingSpot.setAvailable(true);
					parkingSpotDAO.updateParking(parkingSpot);

					double ticketPrice = ticket.getPrice();
					// If price equals 0, the parking is free and no payment is needed
					if (ticketPrice > 0) {
						System.out.println("Please pay the parking fare : " + ticketPrice);
					} else {
						System.out.println("Parking is free for the first 30 minutes, no payment needed");
					}

					System.out.println("Recorded out-time for vehicle number : " + ticket.getVehicleRegNumber()
							+ " is : " + ticket.getOutTime());

				} else {
					System.out.println("Unable to update ticket information. Error occurred");
				}
			} else {
				System.out.println("This vehicle has not been found in our parking");
			}
		} catch (Exception e) {
			LOGGER.error("Unable to process exiting vehicle", e);
		}

	}
}

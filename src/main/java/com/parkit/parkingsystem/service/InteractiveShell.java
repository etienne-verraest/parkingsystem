package com.parkit.parkingsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * 
 * This class is used as our console menu and is used to process different
 * inputs
 *
 */
public class InteractiveShell {

	private static final Logger LOGGER = LogManager.getLogger(InteractiveShell.class);

	/**
	 * This method loads every utilities classes and waits for user's input
	 */
	public static void loadInterface() {

		LOGGER.info("Parking system have been initialized");
		System.out.println("Welcome to Parking System!");

		boolean continueApp = true;
		InputReaderUtil inputReaderUtil = new InputReaderUtil();
		ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
		TicketDAO ticketDAO = new TicketDAO();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		while (continueApp) {
			loadMenu();
			int option = inputReaderUtil.readSelection();
			switch (option) {
			case 1: {
				parkingService.processIncomingVehicle();
				break;
			}
			case 2: {
				parkingService.processExitingVehicle();
				break;
			}
			case 3: {
				System.out.println("Exiting from the system!");
				continueApp = false;
				break;
			}
			default:
				System.out.println("Unsupported option. Please enter a number corresponding to the provided menu");
			}
		}
	}

	/**
	 * This method loads our GUI
	 */
	private static void loadMenu() {
		System.out.println("Please select an option : simply enter the number to choose an action");
		System.out.println("1 - New Vehicle Entering - Allocate Parking Space");
		System.out.println("2 - Vehicle Exiting - Generate Ticket Price");
		System.out.println("3 - Shutdown System");
	}

}

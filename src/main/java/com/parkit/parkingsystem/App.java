package com.parkit.parkingsystem;

import org.apache.logging.log4j.*;

import com.parkit.parkingsystem.service.InteractiveShell;

public class App {
	private static final Logger logger = LogManager.getLogger("App");

	public static void main(String args[]) {
		logger.info("Initializing Parking System");
		InteractiveShell.loadInterface();
	}
}

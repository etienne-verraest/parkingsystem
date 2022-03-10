package com.parkit.parkingsystem.util;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum ApplicationProperties {
	INSTANCE;

	private final Properties properties;

	ApplicationProperties() {
		properties = new Properties();
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public String getDatabasePassword() {
		return properties.getProperty("database.password");
	}

	public String getDatabaseUsername() {
		return properties.getProperty("database.username");
	}
}
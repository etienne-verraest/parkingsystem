package com.parkit.parkingsystem.dao;

import java.sql.*;

import org.apache.logging.log4j.*;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.*;
import com.parkit.parkingsystem.model.*;

public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	// Check if it's a recurring user
	public boolean checkIfVehicleIsRegular(String plateNumber) throws Exception {
		Connection con = dataBaseConfig.getConnection();

		try {
			PreparedStatement ps = con.prepareStatement(DBConstants.CHECK_FOR_REGULAR_USER);
			ps.setString(1, plateNumber);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (Exception ex) {
			logger.error("There was an error while checking if this vehicle is a regular one : " + ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	// Save ticket to database
	public boolean saveTicket(Ticket ticket) throws Exception {
		Connection con = dataBaseConfig.getConnection();

		try {
			PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
			ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
			return ps.execute();

		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	// Get ticket from database
	public Ticket getTicket(String vehicleRegNumber) throws Exception {

		Connection con = dataBaseConfig.getConnection();
		Ticket ticket = null;

		try {
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ticket = new Ticket();
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));
				ticket.setInTime(rs.getTimestamp(4));
				ticket.setOutTime(rs.getTimestamp(5));
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}

		return ticket;
	}

	// Update ticket in database
	public boolean updateTicket(Ticket ticket) throws Exception {
		Connection con = dataBaseConfig.getConnection();

		try {
			PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
			System.out.println(ticket.getId());
			ps.setInt(3, ticket.getId());
			ps.execute();
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}

		return false;
	}
}

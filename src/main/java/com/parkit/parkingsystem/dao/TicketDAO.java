package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

	private static final Logger LOGGER = LogManager.getLogger(TicketDAO.class);

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * This method checks if it's a recurring user
	 * 
	 * @param plateNumber is a String of a vehicle number registration
	 * @return boolean (if true our user is regular)
	 * 
	 */
	public boolean checkIfVehicleIsRegular(String plateNumber) throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.CHECK_FOR_REGULAR_USER);
			ps.setString(1, plateNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				int count = rs.getInt("ticketcounts");
				if (count > 0) {
					return true;
				}
			}
		} catch (Exception ex) {
			LOGGER.error("There was an error while checking if this vehicle is a regular one : " + ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	/**
	 * This method checks if plate is already in the parking (in order to avoid
	 * duplicates plates)
	 * 
	 * @param plateNumber is a String of a vehicle number registration
	 * @return boolean (if true our user is already in the parking)
	 *
	 */
	public boolean checkIfUserIsAlreadyIn(String plateNumber) throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.CHECK_IS_ALREADY_IN);
			ps.setString(1, plateNumber);

			rs = ps.executeQuery();
			if (rs.next()) {
				int count = rs.getInt("vehicleAlreadyIn");
				if (count > 0) {
					return true;
				}
			}
		} catch (Exception ex) {
			LOGGER.error("There was an error while checking if this vehicle is a regular one : " + ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	/**
	 * This method checks if a user has already parked out
	 * 
	 * @param plateNumber is a String of a vehicle number registration
	 * @return boolean (if true our user has already parked out)
	 * 
	 */
	public boolean checkIfUserHasAlreadyParkedOut(String plateNumber) throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.CHECK_IS_ALREADY_OUT);
			ps.setString(1, plateNumber);

			rs = ps.executeQuery();
			if (rs.next()) {
				int count = rs.getInt("vehicleAlreadyOut");
				if (count == 0) {
					return true;
				}
			}
		} catch (Exception ex) {
			LOGGER.error("There was an error while checking if this vehicle is a regular one : " + ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	/**
	 * This method saves a ticket to database
	 * 
	 * @param ticket is an object of type Ticket
	 * @return boolean (if true our ticket is correctly saved)
	 * 
	 */
	public boolean saveTicket(Ticket ticket) throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
			ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
			return ps.execute();

		} catch (Exception ex) {
			LOGGER.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}

		return false;
	}

	/**
	 * This method gets a ticket from database
	 * 
	 * @param vehicleRegNumber is a String of a vehicle number registration
	 * @return an object of type Ticket
	 *
	 */
	public Ticket getTicket(String vehicleRegNumber) throws Exception {

		Connection con = null;
		Ticket ticket = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_TICKET);

			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();
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
		} catch (Exception ex) {
			LOGGER.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}

		return ticket;
	}

	/**
	 * This method updates a ticket in database
	 * 
	 * @param ticket an object of type Ticket
	 * @return boolean (if true our ticket has been correctly updated)
	 *
	 */
	public boolean updateTicket(Ticket ticket) throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			return true;

		} catch (Exception ex) {
			LOGGER.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}
}

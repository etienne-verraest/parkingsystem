package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

/**
 * 
 * This class handles datas for a parking spot
 *
 */
public class ParkingSpotDAO {

	private static final Logger LOGGER = LogManager.getLogger(ParkingSpotDAO.class);

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * This method checks if there is an empty parking slot
	 * 
	 * @param parkingType is an object of type ParkingType (CAR, BIKE)
	 * @return a slot number (integer)
	 * 
	 */
	public int getNextAvailableSlot(ParkingType parkingType) throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = -1;

		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
			ps.setString(1, parkingType.toString());

			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception ex) {
			LOGGER.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		return result;
	}

	/**
	 * Update availability of a parking slot
	 * 
	 * @param parkingSpot is an object of type ParkingSpot
	 * @return boolean (if true parking spot is updated in database)
	 * 
	 */
	public boolean updateParking(ParkingSpot parkingSpot) throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
			ps.setBoolean(1, parkingSpot.isAvailable());
			ps.setInt(2, parkingSpot.getId());
			int updateRowCount = ps.executeUpdate();
			return (updateRowCount == 1);

		} catch (Exception ex) {
			LOGGER.error("Error updating parking info", ex);
			return false;
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}

	}

}

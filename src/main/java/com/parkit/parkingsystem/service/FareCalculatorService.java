package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// 1 - We convert In and Out time in milliseconds
		double inHour = ticket.getInTime().getTime();
		double outHour = ticket.getOutTime().getTime();

		// 2 - We calculate the elapsed time between In and Out Time
		double duration = (outHour - inHour);

		// 3 - We convert milliseconds to hours
		duration = duration / 3600000;

		// 4 - We calculate ticket price depending on parking type
		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
			break;
		}
		case BIKE: {
			ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}
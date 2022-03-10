package com.parkit.parkingsystem.service;

import static com.parkit.parkingsystem.constants.Fare.BIKE_RATE_PER_HOUR;
import static com.parkit.parkingsystem.constants.Fare.CAR_RATE_PER_HOUR;

import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, boolean isRegularUser) {

		if ((ticket.getOutTime() != null) || (ticket.getOutTime().after(ticket.getInTime()))) {

			// 1 - We convert In and Out time in milliseconds
			double inHour = ticket.getInTime().getTime();
			double outHour = ticket.getOutTime().getTime();

			// 2 - We calculate the elapsed time between In and Out Time
			double duration = (outHour - inHour);

			// 3 - We convert milliseconds to hours, and round to 2 decimals places
			duration = duration / 3600000;
			duration = Math.round(duration * 100.0) / 100.0;

			// 4.1 - We check if duration is over 30 minutes, if it's not parking is free
			// 4.2 - We calculate ticket price depending on Vehicle Type
			if (duration > 0.5) {

				switch (ticket.getParkingSpot().getParkingType()) {
				case CAR: {
					ticket.setPrice(duration * CAR_RATE_PER_HOUR);
					break;
				}
				case BIKE: {
					ticket.setPrice(duration * BIKE_RATE_PER_HOUR);
					break;
				}
				default:
					throw new IllegalArgumentException("Unkown Parking Type");
				}

				// If it is a regular user, we apply a 5% discount
				if (isRegularUser) {
					double priceDiscount = calculatePriceWithDiscount(ticket.getPrice());
					ticket.setPrice(priceDiscount);
					System.out.println("5% discount has been applied to ticket price");
				}

			} else {
				ticket.setPrice(0.0);
			}
		} else {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
	}

	// Calculates a 5% discount
	public double calculatePriceWithDiscount(double price) {
		price = price - (price * 0.05);
		price = Math.round(price * 10.0) / 10.0;
		return price;
	}
}
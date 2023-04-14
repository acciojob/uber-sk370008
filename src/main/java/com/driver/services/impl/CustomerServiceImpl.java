package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
//		customerRepository2.deleteById(customerId);
		customerRepository2.delete(customerRepository2.getOne(customerId));
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		TripBooking tripBooking = new TripBooking();
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		int bill = distanceInKm * 10;
		tripBooking.setBill(bill);
		tripBooking.setCustomer(customerRepository2.findById(customerId).get());
		List<Driver> driverList = driverRepository2.findAll();
		Driver firstAvailableDriver = null;
		for (Driver driver : driverList){
			if (driver.getCab().getAvailable() == true){
				firstAvailableDriver = driver;
				break;
			}
		}

		if (firstAvailableDriver == null){
			throw new Exception("No cab available!");
		}
		tripBooking.setDriver(firstAvailableDriver);
		firstAvailableDriver.getCab().setAvailable(false);
		tripBookingRepository2.save(tripBooking);
		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripToCancel = tripBookingRepository2.findById(tripId).get();
		tripToCancel.setStatus(TripStatus.CANCELED);
		tripToCancel.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripToCancel);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripCompleted = tripBookingRepository2.findById(tripId).get();

		tripCompleted.setStatus(TripStatus.COMPLETED);
		tripCompleted.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripCompleted);
	}
}

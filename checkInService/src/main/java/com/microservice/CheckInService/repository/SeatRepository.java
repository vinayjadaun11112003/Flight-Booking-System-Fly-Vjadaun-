package com.microservice.CheckInService.repository;

import com.microservice.CheckInService.entity.Seat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface SeatRepository extends MongoRepository<Seat, Long> {

    List<Seat> findByFlightId(String flightId);

    Optional<Seat> findByFlightIdAndSeatNumber(String flightId, String seatNumber);
    List<Seat> findByPassengerId(String passengerId);
    List<Seat> findByFlightIdAndIsBookedFalse(String flightId);

    List<Seat> findByFlightIdAndPassengerIdIsNull(String flightId);
}

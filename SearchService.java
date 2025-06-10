package com.microservice.SearchFlights.service;

import com.microservice.SearchFlights.client.CheckinServiceClient;
import com.microservice.SearchFlights.client.FareServiceClient;
import com.microservice.SearchFlights.dto.FareRequest;
import com.microservice.SearchFlights.dto.FlightDTO;
import com.microservice.SearchFlights.dto.SearchFlightRequestDTO;
import com.microservice.SearchFlights.entity.FlightEntity;
import com.microservice.SearchFlights.exception.InvalidFlightTimeException;
import com.microservice.SearchFlights.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FareServiceClient fareServiceClient;

    @Autowired
    private CheckinServiceClient checkinServiceClient;

    public FlightDTO addFlight(FlightDTO flight) {
        logger.info("Add Flight Called");
        if (flight.getDepartureDate().after(flight.getArrivalDate())) {
            throw new InvalidFlightTimeException("Departure date cannot be after arrival date.");
        }

        UUID uuid = UUID.randomUUID();
        flight.setFlightId(uuid.toString());

        FlightEntity flightEntity = new FlightEntity(
                flight.getFlightId(),
                flight.getFlightName(),
                flight.getFromCity(),
                flight.getToCity(),
                flight.getDepartureDate(),
                flight.getArrivalDate(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getFareRequest()
        );
        FlightEntity savedFlight = flightRepository.save(flightEntity);

        FareRequest fareRequest = flight.getFareRequest();
        Map<String, Object> fareData = new HashMap<>();
        fareData.put("flightId", savedFlight.getFlightId());
        fareData.put("economyFare", fareRequest.getEconomyFare());
        fareData.put("businessFare", fareRequest.getBusinessFare());

        checkinServiceClient.createSeats(savedFlight.getFlightId(), 40);
        fareServiceClient.addFare(fareData);

        logger.info("Flight Added Successfully with ID: {}", flight.getFlightId());
        return flight;
    }

    public List<FlightDTO> getAllFlights() {
        logger.info("Get All Flights Called");
        List<FlightEntity> flights = flightRepository.findAll();
        List<FlightDTO> dtoList = new ArrayList<>();
        for (FlightEntity flightEntity : flights) {
            FlightDTO dto = new FlightDTO(
                    flightEntity.getFlightId(),
                    flightEntity.getFlightName(),
                    flightEntity.getFromCity(),
                    flightEntity.getToCity(),
                    flightEntity.getDepartureDate(),
                    flightEntity.getArrivalDate(),
                    flightEntity.getDepartureTime(),
                    flightEntity.getArrivalTime(),
                    flightEntity.getFareRequest()
            );
            FareRequest fareRequest = fareServiceClient.getFare(flightEntity.getFlightId());
            dto.setFareRequest(fareRequest);
            dtoList.add(dto);
        }
        logger.info("All Flights fetched and returned");
        return dtoList;
    }

    public String updateFlight(String id, FlightDTO updatedFlight) {
        logger.info("Update Flight Called");
        if (updatedFlight.getDepartureDate().after(updatedFlight.getArrivalDate())) {
            throw new InvalidFlightTimeException("Departure date cannot be after arrival date.");
        }

        return flightRepository.findById(id).map(flight -> {
            flight.setArrivalDate(updatedFlight.getArrivalDate());
            flight.setDepartureDate(updatedFlight.getDepartureDate());
            flight.setDepartureTime(updatedFlight.getDepartureTime());
            flight.setArrivalTime(updatedFlight.getArrivalTime());
            flight.setFlightName(updatedFlight.getFlightName());
            flight.setFromCity(updatedFlight.getFromCity());
            flight.setToCity(updatedFlight.getToCity());
            flightRepository.save(flight);

            Map<String, Object> fareData = new HashMap<>();
            fareData.put("flightId", id);
            fareData.put("economyFare", updatedFlight.getFareRequest().getEconomyFare());
            fareData.put("businessFare", updatedFlight.getFareRequest().getBusinessFare());

            fareServiceClient.updateFare(id, fareData);

            logger.info("Flight and Fare updated successfully for ID: {}", id);
            return "Flight and Fare updated successfully for ID " + id;
        }).orElse("Flight not found with ID " + id);
    }

    public FlightDTO getFlightById(String id) {
        logger.info("Get Flight By Id Called");
        Optional<FlightEntity> flightEntityOpt = flightRepository.findById(id);
        if (flightEntityOpt.isPresent()) {
            FlightEntity flightEntity = flightEntityOpt.get();
            FlightDTO flightDTO = new FlightDTO(
                    flightEntity.getFlightId(),
                    flightEntity.getFlightName(),
                    flightEntity.getFromCity(),
                    flightEntity.getToCity(),
                    flightEntity.getDepartureDate(),
                    flightEntity.getArrivalDate(),
                    flightEntity.getDepartureTime(),
                    flightEntity.getArrivalTime(),
                    flightEntity.getFareRequest()
            );
            FareRequest fareRequest = fareServiceClient.getFare(flightEntity.getFlightId());
            flightDTO.setFareRequest(fareRequest);
            logger.info("Flight fetched successfully with ID: {}", id);
            return flightDTO;
        } else {
            logger.warn("Flight not found with ID: {}", id);
            return null;
        }
    }

    public String deleteFlight(String id) {
        if (flightRepository.existsById(id)) {
            flightRepository.deleteById(id);
            fareServiceClient.deleteFare(id);
            logger.info("Flight and Fare deleted successfully for ID: {}", id);
            return "Flight with ID " + id + " and associated fare deleted successfully.";
        } else {
            logger.warn("Flight not found with ID: {}", id);
            return "Flight with ID " + id + " not found.";
        }
    }

    private FlightDTO convertToDTO(FlightEntity entity) {
        FlightDTO dto = new FlightDTO();
        BeanUtils.copyProperties(entity, dto);
        try {
            FareRequest fare = fareServiceClient.getFare(entity.getFlightId());
            dto.setFareRequest(fare);
        } catch (Exception e) {
            logger.error("Error fetching fare: {}", e.getMessage());
            dto.setFareRequest(null);
        }
        return dto;
    }

    public List<FlightDTO> searchFlights(SearchFlightRequestDTO request) {
        logger.info("Search Flights Called");
        List<FlightEntity> flights = flightRepository.findByFromCityAndToCityAndDepartureDate(
                request.getFromCity(),
                request.getToCity(),
                request.getDepartureDate()
        );
        return flights.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}

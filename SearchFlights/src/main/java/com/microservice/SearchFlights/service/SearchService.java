package com.microservice.SearchFlights.service;

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
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private RestTemplate restTemplate;

    // Method to add flight to the system[VJ]
    public FlightDTO addFlight(FlightDTO flight){
        logger.info("Add Flight Called");
        if (flight.getDepartureDate().after(flight.getArrivalDate())) {
            throw new InvalidFlightTimeException("Departure date cannot be after arrival date.");
        }

        UUID uuid = UUID.randomUUID();
        flight.setFlightId(uuid.toString());

        FlightEntity flightEntity = new FlightEntity(flight.getFlightId(),flight.getFlightName(),flight.getFromCity(),flight.getToCity(),flight.getDepartureDate(),flight.getArrivalDate(),flight.getDepartureTime(),flight.getArrivalTime(),flight.getFareRequest());
        // Save flight first
        FlightEntity savedFlight = flightRepository.save(flightEntity);

        // Extract fare data and add flightId to it
        FareRequest fareRequest = flight.getFareRequest();

        // Create a new object to send to FareService
        Map<String, Object> fareData = new HashMap<>();
        fareData.put("flightId", savedFlight.getFlightId());
        fareData.put("economyFare", fareRequest.getEconomyFare());
        fareData.put("businessFare", fareRequest.getBusinessFare());
        String checkInUrl = "http://localhost:8087/CheckinService/createSeats/{flightId}/{totalSeats}";
        restTemplate.postForObject(checkInUrl, null, Void.class, savedFlight.getFlightId(), 40);
        // Call FareService
        restTemplate.postForObject("http://localhost:8081/FareService/addNewFare", fareData, FareRequest.class);
        logger.info("Flight Added to the system Successfully with id : "+flight.getFlightId());
        return flight;
    }




    // Method to fetch all the flights from the system[VJ]
    public List<FlightDTO> getAllFlights(){
        logger.info("Get All Flight Called");
        List<FlightEntity> flights = flightRepository.findAll();
        List<FlightDTO> dto = new ArrayList<>();
        for(FlightEntity flightEntity : flights){
            FlightDTO d = new FlightDTO(flightEntity.getFlightId(),flightEntity.getFlightName(),flightEntity.getFromCity(),flightEntity.getToCity(),flightEntity.getDepartureDate(),flightEntity.getArrivalDate(),flightEntity.getDepartureTime(),flightEntity.getArrivalTime(),flightEntity.getFareRequest());
            dto.add(d);
        }
        dto.forEach(flight -> {
            FareRequest fareRequest = restTemplate.getForObject(
                    "http://localhost:8081/FareService/getFare/" + flight.getFlightId(),
                    FareRequest.class
            );
            flight.setFareRequest(fareRequest);
        });

        logger.info("All Flights fetch from the system and sent to the frontend");
        return dto;

    }


    // Method to update the flight in the system[VJ]
    public String updateFlight(String id, FlightDTO updatedFlight){
        logger.info("Update Flight By Id Called");
        if (updatedFlight.getDepartureDate().after(updatedFlight.getArrivalDate())) {
            logger.error("Invalid flight time exception triggered");
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
            restTemplate.put(
                    "http://localhost:8081/FareService/updateFare/" + id,
                    fareData
            );
            System.out.println(fareData.get("flightId"));


            logger.info("Flight and Fare updated successfully for ID " + id);
            return "Flight and Fare updated successfully for ID " + id;
        }).orElse("Flight not found with ID " + id);
    }



    // Method to fetch the flight by Id[VJ]
    public FlightDTO getFlightById(String id){
        logger.info("Get Flight By Id Called");
        Optional<FlightEntity> flightEntity = flightRepository.findById(id);
        FlightDTO flightDTO = null;
        if(flightEntity!=null){
            flightDTO = new FlightDTO(flightEntity.get().getFlightId(),flightEntity.get().getFlightName(),flightEntity.get().getFromCity(),flightEntity.get().getToCity(),flightEntity.get().getDepartureDate(),flightEntity.get().getArrivalDate(),flightEntity.get().getDepartureTime(),flightEntity.get().getArrivalTime(),flightEntity.get().getFareRequest());
            FareRequest fareRequest = restTemplate.getForObject(
                    "http://localhost:8081/FareService/getFare/" + flightEntity.get().getFlightId(),
                    FareRequest.class
            );
            flightDTO.setFareRequest(fareRequest);

            logger.info("Flight fetched and sent to the frontend with id : " + id);
            return flightDTO;
        }else{
            logger.warn("Flight not found in the system with id : "+ id);
            return flightDTO;
        }
    }



    // Method to delete the flight by id[VJ]
    public String deleteFlight(String id){
        if (flightRepository.existsById(id)) {
            flightRepository.deleteById(id);

            // Call FareService to delete fare
            restTemplate.delete("http://localhost:8081/FareService/deleteById/" + id);
            logger.info("Flight with ID \" + id + \" and associated fare deleted successfully. " );
            return "Flight with ID " + id + " and associated fare deleted successfully.";
        } else {
            logger.warn("Flight with ID : "+ id + " not found");
            return "Flight with ID " + id + " not found.";
        }
    }


    // Method to convert the entity to dto[VJ]
    private FlightDTO convertToDTO(FlightEntity entity) {
        FlightDTO dto = new FlightDTO();
        BeanUtils.copyProperties(entity, dto);

        // Fetch fare from FareService
        try {
            FareRequest fare = restTemplate.getForObject(
                    "http://localhost:8081/FareService/getFare/" + entity.getFlightId(),
                    FareRequest.class
            );
            dto.setFareRequest(fare);
        } catch (Exception e) {
            System.out.println("Fare Service error: " + e.getMessage());
            dto.setFareRequest(null); // default or fallback
        }

        return dto;
    }


    // Method to search the flight by location and date[VJ]
    public List<FlightDTO> searchFlights(SearchFlightRequestDTO request) {
        logger.info("Search Flight Called");
        List<FlightEntity> flights = flightRepository.findByFromCityAndToCityAndDepartureDate(
                request.getFromCity(),
                request.getToCity(),
                request.getDepartureDate()
        );
        logger.info("Flight Fetched from the server and sent to the frontend");
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}

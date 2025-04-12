package com.microservice.SearchFlights.service;

import com.microservice.SearchFlights.dto.FareRequest;
import com.microservice.SearchFlights.dto.FlightDTO;
import com.microservice.SearchFlights.dto.SearchFlightRequestDTO;
import com.microservice.SearchFlights.entity.FlightEntity;
import com.microservice.SearchFlights.repository.FlightRepository;
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
    private RestTemplate restTemplate;

    public FlightDTO addFlight(FlightDTO flight){
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

        return flight;
    }

    public List<FlightDTO> getAllFlights(){
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

        return dto;
    }

    public String updateFlight(String id, FlightDTO updatedFlight){
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
            System.out.println(fareData.get("economyFare"));
            System.out.println(fareData.get("bussinessFare"));


            return "Flight and Fare updated successfully for ID " + id;
        }).orElse("Flight not found with ID " + id);
    }


    public FlightDTO getFlightById(String id){
        Optional<FlightEntity> flightEntity = flightRepository.findById(id);
        FlightDTO flightDTO = null;
        if(flightEntity!=null){
            flightDTO = new FlightDTO(flightEntity.get().getFlightId(),flightEntity.get().getFlightName(),flightEntity.get().getFromCity(),flightEntity.get().getToCity(),flightEntity.get().getDepartureDate(),flightEntity.get().getArrivalDate(),flightEntity.get().getDepartureTime(),flightEntity.get().getArrivalTime(),flightEntity.get().getFareRequest());
            FareRequest fareRequest = restTemplate.getForObject(
                    "http://localhost:8081/FareService/getFare/" + flightEntity.get().getFlightId(),
                    FareRequest.class
            );
            flightDTO.setFareRequest(fareRequest);


            return flightDTO;
        }else{
            return flightDTO;
        }
    }

    public String deleteFlight(String id){
        if (flightRepository.existsById(id)) {
            flightRepository.deleteById(id);

            // Call FareService to delete fare
            restTemplate.delete("http://localhost:8081/FareService/deleteById/" + id);

            return "Flight with ID " + id + " and associated fare deleted successfully.";
        } else {
            return "Flight with ID " + id + " not found.";
        }
    }

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

    public List<FlightDTO> searchFlights(SearchFlightRequestDTO request) {
        List<FlightEntity> flights = flightRepository.findByFromCityAndToCityAndDepartureDate(
                request.getFromCity(),
                request.getToCity(),
                request.getDepartureDate()
        );

        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


}

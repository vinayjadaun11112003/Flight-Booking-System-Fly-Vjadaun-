package com.microservice.SearchFlights.controller;

import com.microservice.SearchFlights.dto.FareRequest;
import com.microservice.SearchFlights.dto.FlightDTO;
import com.microservice.SearchFlights.dto.SearchFlightRequestDTO;
import com.microservice.SearchFlights.entity.FlightEntity;
import com.microservice.SearchFlights.repository.FlightRepository;
import com.microservice.SearchFlights.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/Search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public String test(){
        return "working fine";
    }

    @PostMapping("/search")
    public ResponseEntity<List<FlightDTO>> searchFlights(@RequestBody SearchFlightRequestDTO request) {
        List<FlightDTO> flights = searchService.searchFlights(request);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/getFlightById/{id}")
    public FlightDTO getFlightById(@PathVariable String id){
        return searchService.getFlightById(id);
    }

    @GetMapping("/getAllFlights")
    public List<FlightDTO> getAllFlights() {
        return searchService.getAllFlights();
    }

    @GetMapping("/test")
    public ResponseEntity<String> adminOnlyAccess() {
        return ResponseEntity.ok("This is ADMIN only data.");
    }

}

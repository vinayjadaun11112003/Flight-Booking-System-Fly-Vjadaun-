package com.microservice.SearchFlights.controller;

import com.microservice.SearchFlights.dto.FlightDTO;
import com.microservice.SearchFlights.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin2")
public class AdminController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/test")
    public ResponseEntity<String> adminOnlyAccess() {
        return ResponseEntity.ok("This is ADMIN only data.");
    }

    @PostMapping("/addNewFlight")
    public FlightDTO addNewFlight(@RequestBody FlightDTO flight) {
        return searchService.addFlight(flight);
    }

    @GetMapping("/getAllFlights")
    public List<FlightDTO> getAllFlights() {
        return searchService.getAllFlights();
    }

    @PutMapping("/updateFlight/{id}")
    public String updateFlight(@PathVariable String id, @RequestBody FlightDTO updatedFlight) {
        return searchService.updateFlight(id,updatedFlight);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteFlight(@PathVariable String id) {
        return searchService.deleteFlight(id);
    }
}

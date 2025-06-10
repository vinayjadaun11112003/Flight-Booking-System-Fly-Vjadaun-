package com.microservice.SearchFlights.client;

import com.microservice.SearchFlights.dto.FareRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "FareService", url = "http://localhost:8081/FareService")
public interface FareServiceClient {
    @GetMapping("/getFare/{flightId}")
    FareRequest getFare(@PathVariable String flightId);

    @PostMapping("/addNewFare")
    void addFare(@RequestBody Map<String, Object> fareData);

    @PutMapping("/updateFare/{flightId}")
    void updateFare(@PathVariable String flightId, @RequestBody Map<String, Object> fareData);

    @DeleteMapping("/deleteById/{flightId}")
    void deleteFare(@PathVariable String flightId);
}

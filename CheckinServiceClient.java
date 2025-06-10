package com.microservice.SearchFlights.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "CheckinService", url = "http://localhost:8087/CheckinService")
public interface CheckinServiceClient {
    @PostMapping("/createSeats/{flightId}/{totalSeats}")
    void createSeats(@PathVariable String flightId, @PathVariable int totalSeats);
}

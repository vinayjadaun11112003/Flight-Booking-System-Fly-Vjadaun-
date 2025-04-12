package com.microservice.FareService.controller;

import com.microservice.FareService.dto.FareDTO;
import com.microservice.FareService.entity.FareEntity;
import com.microservice.FareService.repository.FareRepository;
import com.microservice.FareService.service.FareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/FareService")
public class FareController {

    @Autowired
    private FareService fareService;

    @GetMapping("/test")
    public String test() {
        return "working";
    }

    @PostMapping("/addNewFare")
    public FareDTO addNewFare(@RequestBody FareDTO fare) {
        return fareService.saveFare(fare);
    }

    @GetMapping("/getFare/{id}")
    public FareDTO getFare(@PathVariable String id) {
        return fareService.getFare(id);
    }


    @PutMapping("/updateFare/{id}")
    public String updateFare(@PathVariable String id, @RequestBody FareDTO updatedFare) {
        return fareService.update(id,updatedFare);
    }

    @DeleteMapping("/deleteById/{id}")
    public String deleteById(@PathVariable String id) {
       return fareService.delete(id);
    }

}

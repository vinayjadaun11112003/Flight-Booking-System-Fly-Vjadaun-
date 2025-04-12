package com.microservice.BookingService.controller;

import com.microservice.BookingService.dto.BookingDTO;
import com.microservice.BookingService.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin3")
public class AdminController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/getAllBookings")
    public List<BookingDTO> getAllBookings(){
        return bookingService.getAllBookings();
    }

    @PostMapping("/addBooking")
    public Map<String,String> addNewBooking(@RequestBody BookingDTO dto){
        return bookingService.addNewBooking(dto);
    }

    @GetMapping("/findById/{id}")
    public BookingDTO findById(@PathVariable String id){
        return bookingService.getBookingById(id);
    }

    @PutMapping("/updateById/{id}")
    public Map<String,String> updateById(@PathVariable String id, @RequestBody BookingDTO bookingDTO){
        return bookingService.updateBookingById(id,bookingDTO);
    }

    @DeleteMapping("/deleteById/{id}")
    public Map<String,String> deleteById(@PathVariable String id){
        return bookingService.deleteByBookingId(id);
    }
}

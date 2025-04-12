package com.example.StripeGateway.controller;

import com.example.StripeGateway.entity.Payment;
import com.example.StripeGateway.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin4")
public class AdminController {

    @Autowired
    private StripeService stripeService;

    @GetMapping("/getAllPayments")
    public List<Payment> getAllPayments(){
        return stripeService.getAllPayments();
    }

    @GetMapping("/findById/{id}")
    public Optional<Payment> findById(@PathVariable Long id){
        return stripeService.findById(id);
    }



    @DeleteMapping("/deleteBySessionId/{id}")
    public Map<String,String> deleteById(@PathVariable String id){
        return stripeService.deleteBySessionId(id);
    }

    @PutMapping("/updateById/{id}")
    public Map<String,String> updateById(@PathVariable Long id, @RequestBody Payment payment){
        return stripeService.updateById(id,payment);
    }

    @PostMapping("/addNewPayment")
    public Map<String,String> addNewPayment(@RequestBody Payment payment){
        return stripeService.addNewPayment(payment);
    }
}

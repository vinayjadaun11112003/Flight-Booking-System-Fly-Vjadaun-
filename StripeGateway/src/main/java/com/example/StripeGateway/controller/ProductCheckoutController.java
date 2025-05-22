package com.example.StripeGateway.controller;

import com.example.StripeGateway.dto.ProductRequest;
import com.example.StripeGateway.dto.StripeResponse;
import com.example.StripeGateway.entity.Payment;
import com.example.StripeGateway.service.StripeService;
import jakarta.ws.rs.PUT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product/v1")
public class ProductCheckoutController {
    private StripeService stripeService;

    public ProductCheckoutController(StripeService stripeService){
        this.stripeService=stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse>checkoutProducts(@RequestBody ProductRequest productRequest){
        StripeResponse stripeResponse=stripeService.checkoutProducts(productRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);

    }
    @GetMapping("/findBySessionId/{id}")
    public Optional<Payment> findBySessionId(@PathVariable String id){
        return stripeService.findBySessionId(id);
    }
    @DeleteMapping("/deleteBySessionId/{id}")
    public Map<String,String> deleteById(@PathVariable String id){
        return stripeService.deleteBySessionId(id);
    }
}

package com.example.StripeGateway.service;

//import com.example.StripeGateway.dto.ProductRequest;
//import com.example.StripeGateway.dto.StripeResponse; // Create this DTO
//import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.model.checkout.Session;
//import com.stripe.param.checkout.SessionCreateParams;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class StripeService {
//
//    @Value("${stripe.secretKey}")
//    private String secretKey;
//
//    public StripeResponse checkoutProducts(ProductRequest productRequest) {
//        Stripe.apiKey = secretKey;
//
//        // Define Product Data inside PriceData
//        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
//                .setCurrency(productRequest.getCurrency() == null ? "USD" : productRequest.getCurrency())
//                .setUnitAmount(productRequest.getAmount())
//                .setProductData(
//                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                .setName(productRequest.getName())
//                                .build()
//                )
//                .build();
//
//        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
//                .setQuantity(productRequest.getQuantity())
//                .setPriceData(priceData)
//                .build();
//
//        SessionCreateParams params = SessionCreateParams.builder()
//                .setMode(SessionCreateParams.Mode.PAYMENT)
//                .setSuccessUrl("http://localhost:8080/success")
//                .setCancelUrl("http://localhost:8080/cancel")
//                .addLineItem(lineItem)
//                .build();
//
//        Session session = null;
//        try {
//            session = Session.create(params);
//        } catch (StripeException ex) {
//            System.out.println("Stripe Error: " + ex.getMessage());
//            return new StripeResponse("FAILED", ex.getMessage(), null, null);
//        }
//
//         StripeResponse a = new StripeResponse("SUCCESS", "Payment session created", session.getId(), session.getUrl());
//         System.out.println(session.getId());
//         System.out.println(productRequest.getAmount().toString());
//         return a;
//    }
//}
//
import com.example.StripeGateway.dto.ProductRequest;
import com.example.StripeGateway.dto.StripeResponse;
import com.example.StripeGateway.entity.Payment;
import com.example.StripeGateway.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Autowired
    private final PaymentRepository paymentRepository;

    public List<Payment> getAllPayments(){
        return paymentRepository.findAll();
    }

    public Optional<Payment> findById(Long id){
        return paymentRepository.findById(id);
    }
    public Optional<Payment> findBySessionId(String id){
        return paymentRepository.findBySessionId(id);
    }

    public Map<String,String> updateById(Long id,Payment payment){
        Map<String,String> mp = new HashMap<>();
        return paymentRepository.findById(id).map(paymentt -> {
            paymentt.setCurrency(payment.getCurrency());
            paymentt.setSessionId(payment.getSessionId());
            paymentt.setAmount(payment.getAmount());
            paymentt.setStatus(payment.getStatus());
            paymentRepository.save(paymentt);
            mp.put("message","payment updated succesfully");
            return mp;
        }).orElseGet(() -> {
            mp.put("message","payment not found with this id : "+ id);
            return mp;
        });
    }
    public Map<String,String> addNewPayment(Payment payment){
        Map<String,String> mp = new HashMap<>();
         paymentRepository.save(payment);
        mp.put("message","Payment Created Successfully");
        return mp;
    }

    public Map<String,String> deleteBySessionId(String id){
        Optional<Payment> payment = paymentRepository.findBySessionId(id);
        Map<String,String> mp = new HashMap<>();
        if(payment!=null){
            paymentRepository.deleteById(payment.get().getId());
            mp.put("message","Payment successfully deleted with id : "+id);
            return mp;
        }else{
            mp.put("message","Payment not found with id : "+id);
            return mp;
        }
    }



    public StripeService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(productRequest.getCurrency() == null ? "USD" : productRequest.getCurrency())
                .setUnitAmount(productRequest.getAmount())
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(productRequest.getName())
                                .build()
                )
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(productRequest.getQuantity())
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .addLineItem(lineItem)
                .build();

        Session session;
        try {
            session = Session.create(params);

            // Save payment details to database
            Payment payment = new Payment(
                    session.getId(),
                    "SUCCESS",
                    productRequest.getCurrency(),
                    productRequest.getAmount()
            );
            paymentRepository.save(payment);

            return new StripeResponse("SUCCESS", "Payment session created", session.getId(), session.getUrl());
        } catch (StripeException ex) {
            return new StripeResponse("FAILED", ex.getMessage(), null, null);
        }
    }
}
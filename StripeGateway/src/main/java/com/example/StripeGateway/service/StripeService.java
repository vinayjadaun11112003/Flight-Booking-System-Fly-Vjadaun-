package com.example.StripeGateway.service;

import com.example.StripeGateway.dto.ProductRequest;
import com.example.StripeGateway.dto.StripeResponse;
import com.example.StripeGateway.entity.Payment;
import com.example.StripeGateway.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StripeService {

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(StripeService.class);

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Autowired
    private final PaymentRepository paymentRepository;

    // Method to fetch all the payment from ths system[VJ]
    public List<Payment> getAllPayments(){
        logger.info("Get All Payment called, retured all payment from the system");
        return paymentRepository.findAll();
    }

    // Method to fetch payment by id from the system[VJ]
    public Optional<Payment> findById(Long id){
        logger.info("Get Payment by id called");
        return paymentRepository.findById(id);
    }

    // Method to fetch payment by session Id from the system[VJ]
    public Optional<Payment> findBySessionId(String id){
        logger.info("Get payment by session id called");
        return paymentRepository.findBySessionId(id);
    }

    // Method to update the payment by id to the system[VJ]
    public Map<String,String> updateById(Long id,Payment payment){
        logger.info("Update Payment by id called");
        Map<String,String> mp = new HashMap<>();
        return paymentRepository.findById(id).map(paymentt -> {
            paymentt.setCurrency(payment.getCurrency());
            paymentt.setSessionId(payment.getSessionId());
            paymentt.setAmount(payment.getAmount());
            paymentt.setStatus(payment.getStatus());
            paymentRepository.save(paymentt);
            mp.put("message","payment updated succesfully");
            logger.info("Payment successfully updated with the id : "+payment.getId());
            return mp;
        }).orElseGet(() -> {
            logger.warn("Payment not found or updated with the id : "+payment.getId());
            mp.put("message","payment not found with this id : "+ id);
            return mp;
        });
    }


    // Method to add new payment to the system[VJ]
    public Map<String,String> addNewPayment(Payment payment){
        logger.info("New Payment Add Method called");
        Map<String,String> mp = new HashMap<>();
         paymentRepository.save(payment);
        mp.put("message","Payment Created Successfully");
        return mp;
    }

    // Method to delete the payment by session id to the system.[VJ]
    public Map<String,String> deleteBySessionId(String id){
        logger.info("Delete by session id method called");
        Optional<Payment> payment = paymentRepository.findBySessionId(id);
        Map<String,String> mp = new HashMap<>();
        if(payment!=null){
            paymentRepository.deleteById(payment.get().getId());
            mp.put("message","Payment successfully deleted with id : "+id);
            logger.info("Payment successfully deleted with id : "+id);
            return mp;
        }else{
            logger.warn("Payment not found with the id : "+id);
            mp.put("message","Payment not found with id : "+id);
            return mp;
        }
    }



    // Constructor injection[VJ]
    public StripeService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Checkout Method for payment intent[VJ]
    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        logger.info("Checkout Payment Intent method called for payment");
        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(productRequest.getCurrency() == null ? "USD" : productRequest.getCurrency())
                .setUnitAmount(productRequest.getAmount()*100)
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
                .setSuccessUrl("http://localhost:5173/home")
                .setCancelUrl("http://localhost:5173/home")
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
            logger.info("Payment session created success");
            return new StripeResponse("SUCCESS", "Payment session created", session.getId(), session.getUrl());
        } catch (StripeException ex) {
            logger.warn("payment session failed");
            return new StripeResponse("FAILED", ex.getMessage(), null, null);
        }
    }
}
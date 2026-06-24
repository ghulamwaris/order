package com.springai.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderKafkaListener {

    
    @KafkaListener(topics = "orders-topic", groupId = "${app.kafka.consumer-group:order-consumer-group}")      
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }
    
}


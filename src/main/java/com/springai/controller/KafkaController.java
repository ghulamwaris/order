package com.springai.controller;

import com.springai.dto.PublishRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    //@Autowired
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.default-topic:orders-topic}")
    private String defaultTopic;

    public KafkaController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestBody PublishRequest request) {
        String topic = request.topic() == null || request.topic().isBlank() ? defaultTopic : request.topic();
        String message = request.message() == null ? "" : request.message();

        kafkaTemplate.send(topic, message);
        return ResponseEntity.ok("Message published to topic: " + topic);
    }

    @GetMapping("/publish")
    public ResponseEntity<String> publishGet(@RequestParam(value = "topic", required = false) String topic,
                                              @RequestParam("message") String message) {
        String finalTopic = (topic == null || topic.isBlank()) ? defaultTopic : topic;
        
        kafkaTemplate.send(finalTopic, message);
        return ResponseEntity.ok("Message published to topic: " + finalTopic);
    }
}


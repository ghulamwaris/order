package com.springai.controller;

import com.springai.dto.Order;
import com.springai.dto.QuestionRequest;
import com.springai.dto.QuestionResponse;
import com.springai.dto.QuestionAnswerPair;
import com.springai.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping(value = "/{id}")
    @CircuitBreaker(name = "order-service", fallbackMethod = "getDefaultValue")
    public Order getOrder(@PathVariable("id") long id) {
        System.out.println("Inside getOrder");
        restTemplate.getForObject("http://localhost:8080/hello", String.class);
        return orderService.findOrderById(id);
    }

    public Order getDefaultValue(Exception ex) {
        return new Order(1l, "failedData");
    }

    @GetMapping("/hello")
    public Order hello() {
        System.out.println("Inside getOrder");
        return new Order(1l, "my data");
    }

    //create getHistory endpoint to return list of question and answer pairs for a given conversation id
    //follow end point as base controller + /history/{conversationId}  and return type should be list of question and answer pairs
    // implement circuit breaker for this endpoint as well and return empty list in case of failure
    @GetMapping("/history/{conversationId}")
    @CircuitBreaker(name = "order-service2", fallbackMethod = "getDefaultHistory")
    public List<QuestionAnswerPair> getHistory(@PathVariable("conversationId") String conversationId) {
        return orderService.getConversationHistory(conversationId);
    }

    public List<QuestionAnswerPair> getDefaultHistory(String conversationId, Exception ex) {
        System.out.println("Circuit breaker fallback triggered for getHistory: " + ex.getMessage());
        return Collections.emptyList();
    }

    // CRUD endpoints for Order
    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.findAllOrders();
    }

    @PostMapping("")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") long id, @RequestBody Order order) {
        Order updated = orderService.updateOrder(id, order);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable("id") long id) {
        boolean deleted = orderService.deleteOrder(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/askQuestion")
    public QuestionResponse askQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        if (questionRequest.questionText() == null) {
            questionRequest = new QuestionRequest("what is java", "12");
        }
        return orderService.askQuestion(questionRequest.questionText(), questionRequest.converSationId());
    }

    @PostMapping("/askQuestion2")
    public QuestionResponse askQuestion2(@RequestBody String questionRequest) {
        return orderService.askQuestion(questionRequest, "12");
    }
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@Valid @RequestBody QuestionRequest request) {
        return orderService.stream(request.questionText());
    }
}
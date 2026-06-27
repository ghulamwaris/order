package com.springai.controller;

import com.springai.dto.Order;
import com.springai.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;

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
}
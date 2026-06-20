package com.springai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springai.dto.Order;
import com.springai.dto.QRequest;
import com.springai.dto.QuestionRequest;
import com.springai.dto.QuestionResponse;
import com.springai.service.OrderService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.awt.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping(value = "/{id}")
    public Order getOrder(@PathVariable("id") long id) {
        System.out.println("Inside getOrder");
        return new Order(1l, "my data");
    }

    @GetMapping("/hello")
    public Order hello() {
        System.out.println("Inside getOrder");
        return new Order(1l, "my data");
    }

    @GetMapping("/ask")
    public String ask(@RequestParam("question") String question) {
        //return orderService.askQuestion(question);
        return "";
    }

    @PostMapping("/askQuestion")
    public QuestionResponse askQuestion(@RequestBody QuestionRequest questionRequest) {
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
    public Flux<String> stream(@RequestBody QuestionRequest request) {
        return orderService.stream(request.questionText());
    }
}
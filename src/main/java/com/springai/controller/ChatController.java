package com.springai.controller;

import com.springai.dto.QuestionRequest;
import com.springai.dto.QuestionResponse;
import com.springai.dto.QuestionAnswerPair;
import com.springai.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    OrderService orderService;

    /**
     * Ask a question with JSON request containing questionText and conversationId
     */
    @PostMapping("/ask")
    public QuestionResponse askQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        if (questionRequest.questionText() == null) {
            questionRequest = new QuestionRequest("what is java", "12");
        }
        return orderService.askQuestion(questionRequest.questionText(), questionRequest.converSationId());
    }

    /**
     * Ask a question with raw string body (default conversation id: "12")
     */
    @PostMapping("/ask-raw")
    public QuestionResponse askQuestionRaw(@RequestBody String questionRequest) {
        return orderService.askQuestion(questionRequest, "12");
    }

    /**
     * Stream a response from the AI model (Server-Sent Events)
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@Valid @RequestBody QuestionRequest request) {
        return orderService.stream(request.questionText());
    }

    /**
     * Get conversation history for a given conversation ID
     * Returns list of question and answer pairs
     */
    @GetMapping("/history/{conversationId}")
    @CircuitBreaker(name = "chat-service", fallbackMethod = "getDefaultHistory")
    public List<QuestionAnswerPair> getHistory(@PathVariable("conversationId") String conversationId) {
        return orderService.getConversationHistory(conversationId);
    }

    /**
     * Fallback method for getHistory when circuit breaker is open
     */
    public List<QuestionAnswerPair> getDefaultHistory(String conversationId, Exception ex) {
        System.out.println("Circuit breaker fallback triggered for getHistory: " + ex.getMessage());
        return Collections.emptyList();
    }
}


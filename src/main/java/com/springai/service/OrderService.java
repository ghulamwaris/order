package com.springai.service;

import com.springai.dto.Order;
import com.springai.dto.QuestionResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.swing.plaf.PanelUI;

@Service
public interface OrderService {
    Order findOrderById(long id);
    QuestionResponse askQuestion(String question, String conversationId);

    public Flux<String> stream(String question);
}

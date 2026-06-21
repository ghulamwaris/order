package com.springai.service;

import com.springai.dto.Order;
import com.springai.dto.QuestionResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface OrderService {
    Order findOrderById(long id);

    List<Order> findAllOrders();

    Order createOrder(Order order);

    Order updateOrder(long id, Order order);

    boolean deleteOrder(long id);

    QuestionResponse askQuestion(String question, String conversationId);

    Flux<String> stream(String question);
}

package com.springai.service;

import com.springai.dto.Order;
import com.springai.dto.QuestionResponse;
import com.springai.dto.QuestionAnswerPair;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private ConversationService conversationService;
    @Override
    public Order findOrderById(long id) {
        return orders.get(id);
    }

    @Override
    public List<Order> findAllOrders() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Order createOrder(Order order) {
        long id = order.id() <= 0 ? idGenerator.incrementAndGet() : order.id();
        var created = new Order(id, order.details());
        orders.put(id, created);
        return created;
    }

    @Override
    public Order updateOrder(long id, Order order) {
        if (!orders.containsKey(id)) {
            return null;
        }
        var updated = new Order(id, order.details());
        orders.put(id, updated);
        return updated;
    }

    @Override
    public boolean deleteOrder(long id) {
        return orders.remove(id) != null;
    }

    public static final String systemPrompt = """
                 You are a senior Java 17 expert.

                        Strict Rules:
                        - Give technically correct answers
                        - - Do NOT include markdown code fence
                        - Keep code concise
                        - Avoid unnecessary comments
                        - Use modern Java 17 syntax
                        - Do not generate markdown code fences
                        - Give clean production-quality examples
                        - If user asks coding question, answer with Java code first then short explanation
                """;

    @Override
    public QuestionResponse askQuestion(String question, String conversationId) {

        String template = """
                You are a Java support assistant.

                Customer Question:
                {question}

                Respond professionally.
                """;

        var promptTemplate =
                new PromptTemplate(template);

        var prompt = promptTemplate.create(
                Map.of("question", question)
        );

        MessageChatMemoryAdvisor advisor =
                MessageChatMemoryAdvisor.builder(
                        chatMemory
                ).build();

       // chatClient.prompt().system(systemPrompt).user(question).call().content(); // Warm up the chat client
        String content = chatClient.prompt(prompt)
                .system(systemPrompt)
                .user(question)
                .advisors(advisor)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID,
                        conversationId
                ))
                .options(OllamaOptions.builder()
                        .temperature(0.0)
                        .build())
                .call().content();

        String sanitized = sanitizeResponse(content);
        
        // Auto-save Q/A to conversation history
        conversationService.addToConversation(conversationId, question, sanitized);
        
        return new QuestionResponse(sanitized);
    }

    @Override
    public Flux<String> stream(String question) {
      return chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .stream()
                .content()
                .map(this::sanitizeResponse);
    }

    /**
     * Simple sanitizer to remove markdown fences and inline backticks, and normalize whitespace.
     */
    private String sanitizeResponse(String input) {
        if (input == null) return null;
        // remove triple backtick blocks and tildes
        String withoutFences = input.replaceAll("(?m)```.*?```", "");
        withoutFences = withoutFences.replaceAll("(?m)~~~.*?~~~", "");
        // remove any remaining fence markers like ```java or ```txt
        withoutFences = withoutFences.replaceAll("(?m)```.*", "");
        // remove inline backticks (replace with empty string, not space)
        withoutFences = withoutFences.replace('`', '\u0000');  // placeholder
        // normalize multiple spaces to single space
        withoutFences = withoutFences.replaceAll(" +", " ");
        // replace placeholder with empty
        withoutFences = withoutFences.replace('\u0000', ' ');
        // normalize multiple blank lines to maximum two
        withoutFences = withoutFences.replaceAll("(?m)\\n\\s*\\n\\s*\\n+", "\n\n");
        // trim
        return withoutFences.trim();
    }

    // Simple in-memory store for orders
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public List<QuestionAnswerPair> getConversationHistory(String conversationId) {
        return conversationService.getHistory(conversationId);
    }
}

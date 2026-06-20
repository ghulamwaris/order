package com.springai.service;

import com.springai.dto.Order;
import com.springai.dto.QuestionResponse;
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

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Override
    public Order findOrderById(long id) {
        return null;
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

        return new QuestionResponse(chatClient.prompt(prompt)
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
                .call().content());
    }

    @Override
    public Flux<String> stream(String question) {
      return   chatClient.prompt().
                system(systemPrompt)
                .user(question).stream().content();
    }
}

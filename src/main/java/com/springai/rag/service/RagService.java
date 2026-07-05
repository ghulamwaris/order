package com.springai.rag.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springai.rag.entity.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RagService {

    @Autowired
    private DocumentService documentService;

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RagService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String answerQuestion(String question) {
        log.info("Processing question: {}", question);
        
        List<Document> contextDocs = documentService.searchSimilar(question, 3);

        if (contextDocs.isEmpty()) {
            log.warn("No relevant documents found for question: {}", question);
            return "No relevant documents found to answer your question.";
        }

        String context = contextDocs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n---\n"));

        log.info("Using {} documents as context", contextDocs.size());

        String systemPrompt = "You are a helpful Q&A assistant. Use the provided context to answer questions accurately. If the context doesn't contain relevant information, say so.";
        String userPrompt = "Context:\n" + context + "\n\nQuestion: " + question;

        String answer = callOpenAI(systemPrompt, userPrompt);

        log.info("Question answered successfully");
        return answer;
    }

    private String callOpenAI(String systemPrompt, String userPrompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("temperature", 0.7);
        body.put("max_tokens", 500);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", systemPrompt));
        messages.add(createMessage("user", userPrompt));
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            String response = restTemplate.postForObject(url, request, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            String answer = rootNode.get("choices").get(0).get("message").get("content").asText();
            return answer;
        } catch (Exception e) {
            log.error("Error calling OpenAI: {}", e.getMessage());
            throw new RuntimeException("Failed to get answer from LLM", e);
        }
    }

    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }
}

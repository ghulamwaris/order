package com.springai.service;

import com.springai.dto.QuestionAnswerPair;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ConversationService {
    
    private final Map<String, List<QuestionAnswerPair>> conversations = new ConcurrentHashMap<>();
    
    /**
     * Add a Q/A pair to a conversation.
     */
    public void addToConversation(String conversationId, String question, String answer) {
        conversations.computeIfAbsent(conversationId, k -> new CopyOnWriteArrayList<>())
                .add(new QuestionAnswerPair(question, answer, Instant.now()));
    }
    
    /**
     * Get the conversation history (list of Q/A pairs) for a conversation ID.
     */
    public List<QuestionAnswerPair> getHistory(String conversationId) {
        return conversations.getOrDefault(conversationId, new CopyOnWriteArrayList<>());
    }
    
    /**
     * Clear a conversation history.
     */
    public void clearConversation(String conversationId) {
        conversations.remove(conversationId);
    }
    
    /**
     * Get all conversation IDs.
     */
    public Set<String> getAllConversationIds() {
        return conversations.keySet();
    }
}


package com.springai.rag.controller;

import com.springai.rag.dto.*;
import com.springai.rag.entity.Document;
import com.springai.rag.service.DocumentService;
import com.springai.rag.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/rag")
@Slf4j
public class RagController {

    @Autowired
    private RagService ragService;

    @Autowired
    private DocumentService documentService;

    @PostMapping("/ingest")
    public ResponseEntity<?> ingestDocument(@RequestBody DocumentRequest request) {
        try {
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Content cannot be empty"));
            }

            Document doc = documentService.ingestDocument(request.getContent());
            return ResponseEntity.ok(new DocumentResponse(
                doc.getId(),
                "Document ingested successfully",
                doc.getCreatedAt()
            ));
        } catch (Exception e) {
            log.error("Error ingesting document: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to ingest document: " + e.getMessage()));
        }
    }

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestBody QuestionRequest request) {
        try {
            if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Question cannot be empty"));
            }

            String answer = ragService.answerQuestion(request.getQuestion());
            return ResponseEntity.ok(new AnswerResponse(
                request.getQuestion(),
                answer,
                LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error processing question: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to process question: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(new AnswerResponse(
            "Health check",
            "RAG service is running",
            LocalDateTime.now()
        ));
    }
}

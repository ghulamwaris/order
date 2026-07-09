package com.springai.rag.controller;

import com.springai.rag.dto.*;
import com.springai.rag.entity.Document;
import com.springai.rag.service.DocumentService;
import com.springai.rag.service.RagService;
import com.springai.rag.service.SpringAIChunkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document as AIDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rag")
@Slf4j
public class RagController {

    @Autowired
    private RagService ragService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SpringAIChunkingService springAIChunkingService;

    /**
     * Ingest a document without chunking (legacy endpoint)
     * WARNING: Not recommended for large documents as it creates a single embedding
     */
    @PostMapping("/ingest")
    public ResponseEntity<?> ingestDocument(@RequestBody DocumentRequest request) {
        try {
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Content cannot be empty"));
            }

            Document doc = documentService.ingestDocument(request.getContent());
            return ResponseEntity.ok(new DocumentResponse(
                doc.getId(),
                "Document ingested successfully (not chunked)",
                doc.getCreatedAt()
            ));
        } catch (Exception e) {
            log.error("Error ingesting document: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to ingest document: " + e.getMessage()));
        }
    }

    /**
     * Preview Spring AI chunking strategy without saving to database
     * Useful for testing and validating chunking before ingestion
     * 
     * @param request Content to chunk
     * @return Preview of chunks with chunk count
     */
    @PostMapping("/preview-chunks")
    public ResponseEntity<?> previewChunking(@RequestBody IngestChunkedDocumentRequest request) {
        try {
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Content cannot be empty"));
            }

            String strategy = request.getChunkingStrategy() != null ? request.getChunkingStrategy() : "default";
            List<AIDocument> aiChunks = new ArrayList<>();

            switch (strategy.toLowerCase()) {
                case "small":
                    aiChunks = springAIChunkingService.chunkByTokensSmall(request.getContent());
                    break;
                case "large":
                    aiChunks = springAIChunkingService.chunkByTokensLarge(request.getContent());
                    break;
                case "strict":
                    aiChunks = springAIChunkingService.chunkByTokensStrict(request.getContent());
                    break;
                case "default":
                default:
                    aiChunks = springAIChunkingService.chunkByTokensDefault(request.getContent());
                    break;
            }

            List<String> chunkContents = new ArrayList<>();
            for (AIDocument doc : aiChunks) {
                chunkContents.add(doc.getContent());
            }

            return ResponseEntity.ok(new ChunkingResponse(
                chunkContents.size(),
                "Spring AI TokenTextSplitter (" + strategy + ")",
                chunkContents,
                "Chunking preview completed using Spring AI TokenTextSplitter"
            ));
        } catch (Exception e) {
            log.error("Error previewing chunks: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to preview chunks: " + e.getMessage()));
        }
    }

    /**
     * Ingest chunked document using Spring AI TokenTextSplitter
     * RECOMMENDED APPROACH for RAG systems
     * 
     * Each chunk is saved as a separate document with its own embedding.
     * This enables precise vector similarity search and better context retrieval.
     * 
     * Strategies:
     * - default: 500 tokens per chunk (recommended for most use cases)
     * - small: 200 tokens per chunk (for short documents)
     * - large: 1000 tokens per chunk (for long documents like books)
     * - strict: 250 tokens per chunk (for strict token limits)
     * 
     * @param request Content and chunking strategy
     * @return Chunked document IDs and chunk count
     */
    @PostMapping("/ingest-chunked")
    public ResponseEntity<?> ingestChunkedDocument(@RequestBody IngestChunkedDocumentRequest request) {
        try {
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Content cannot be empty"));
            }

            String strategy = request.getChunkingStrategy() != null ? request.getChunkingStrategy() : "default";
            List<AIDocument> aiChunks = new ArrayList<>();

            switch (strategy.toLowerCase()) {
                case "small":
                    log.info("Using small chunking strategy (200 tokens per chunk)");
                    aiChunks = springAIChunkingService.chunkByTokensSmall(request.getContent());
                    break;
                case "large":
                    log.info("Using large chunking strategy (1000 tokens per chunk)");
                    aiChunks = springAIChunkingService.chunkByTokensLarge(request.getContent());
                    break;
                case "strict":
                    log.info("Using strict chunking strategy (250 tokens per chunk)");
                    aiChunks = springAIChunkingService.chunkByTokensStrict(request.getContent());
                    break;
                case "default":
                default:
                    log.info("Using default chunking strategy (500 tokens per chunk)");
                    aiChunks = springAIChunkingService.chunkByTokensDefault(request.getContent());
                    break;
            }

            // Save each chunk as a separate document with its own embedding
            List<Long> documentIds = new ArrayList<>();
            for (int i = 0; i < aiChunks.size(); i++) {
                AIDocument aiDoc = aiChunks.get(i);
                Document doc = documentService.ingestDocument(aiDoc.getContent());
                documentIds.add(doc.getId());
                log.debug("Saved chunk {} with ID: {}", i + 1, doc.getId());
            }

            log.info("Successfully ingested {} chunks using Spring AI TokenTextSplitter ({})", 
                    documentIds.size(), strategy);

            return ResponseEntity.ok(new IngestChunkedDocumentResponse(
                aiChunks.size(),
                documentIds,
                "Document chunked and ingested successfully using Spring AI TokenTextSplitter (" + strategy + ")",
                LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error ingesting chunked document: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to ingest chunked document: " + e.getMessage()));
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

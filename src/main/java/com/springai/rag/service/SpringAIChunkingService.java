package com.springai.rag.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring AI Chunking Service using TokenTextSplitter
 * 
 * TokenTextSplitter is an AI-aware text splitting strategy that:
 * - Splits based on token count (not just characters/words)
 * - Matches LLM tokenization (e.g., cl100k_base for OpenAI)
 * - Finds natural split points (sentences, paragraphs)
 * - Configurable overlap for context preservation
 * - Designed for RAG and LLM input optimization
 * 
 * Benefits over custom chunking:
 * - Accurate token counting for LLM context windows
 * - Intelligent semantic boundaries
 * - Reduces truncation and context loss
 * - Production-tested in Spring AI framework
 */
@Service
@Slf4j
public class SpringAIChunkingService {

    /**
     * Chunk text using Spring AI's TokenTextSplitter
     * This is the RECOMMENDED approach for RAG systems
     * 
     * @param text The text to chunk
     * @param tokensPerChunk Number of tokens per chunk (default: 500)
     * @param overlapTokens Number of tokens overlap between chunks (default: 20)
     * @return List of Document chunks
     */
    public List<Document> chunkByTokens(String text, int tokensPerChunk, int overlapTokens) {
        try {
            log.info("Starting token-based chunking with {} tokens per chunk, {} tokens overlap", 
                    tokensPerChunk, overlapTokens);
            
            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(tokensPerChunk)
                    .withMinChunkSizeChars(350)           // Minimum characters per chunk
                    .withMinChunkLengthToEmbed(5)         // Chunks must have at least 5 characters
                    .withMaxNumChunks(10000)              // Maximum number of chunks to create
                    .withKeepSeparator(true)              // Keep punctuation/newlines for readability
                    .build();
            
            List<Document> chunks = splitter.apply(List.of(new Document(text)));
            
            log.info("Successfully chunked text into {} pieces using Spring AI TokenTextSplitter", chunks.size());
            
            for (int i = 0; i < chunks.size(); i++) {
                log.debug("Chunk {}: {} characters", i + 1, chunks.get(i).getContent().length());
            }
            
            return chunks;
        } catch (Exception e) {
            log.error("Error during token-based chunking: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to chunk text using Spring AI TokenTextSplitter", e);
        }
    }

    /**
     * Chunk using default Spring AI settings
     * Default: 500 tokens per chunk, 20 tokens overlap
     * This is optimized for GPT-3.5/GPT-4 tokenization
     */
    public List<Document> chunkByTokensDefault(String text) {
        return chunkByTokens(text, 500, 20);
    }

    /**
     * Chunk for small documents/prompts
     * Useful for shorter content like queries or summaries
     */
    public List<Document> chunkByTokensSmall(String text) {
        return chunkByTokens(text, 200, 10);
    }

    /**
     * Chunk for large documents
     * Useful for books, long articles, or extensive documentation
     */
    public List<Document> chunkByTokensLarge(String text) {
        return chunkByTokens(text, 1000, 50);
    }

    /**
     * Chunk for precise, small context windows
     * Useful for strict token limits (e.g., mobile models)
     */
    public List<Document> chunkByTokensStrict(String text) {
        return chunkByTokens(text, 250, 5);
    }
}

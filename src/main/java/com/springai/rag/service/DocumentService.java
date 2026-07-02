package com.springai.rag.service;

import com.pgvector.PGvector;
import com.springai.rag.entity.Document;
import com.springai.rag.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private EmbeddingService embeddingService;

    public Document ingestDocument(String content) {
        log.info("Ingesting document with content length: {}", content.length());
        
        double[] embedding = embeddingService.getEmbedding(content);

        Document doc = new Document();
        doc.setContent(content);
        doc.setEmbedding(new PGvector(embedding));

        Document saved = documentRepository.save(doc);
        log.info("Document saved with ID: {}", saved.getId());
        return saved;
    }

    public List<Document> searchSimilar(String query, int limit) {
        log.info("Searching similar documents for query: {}", query);
        
        double[] queryEmbedding = embeddingService.getEmbedding(query);
        PGvector pgvectorQuery = new PGvector(queryEmbedding);

        List<Document> results = documentRepository.findSimilarDocuments(pgvectorQuery, limit);
        log.info("Found {} similar documents", results.size());
        return results;
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }
}

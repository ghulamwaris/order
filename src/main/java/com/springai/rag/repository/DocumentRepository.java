package com.springai.rag.repository;

import com.pgvector.PGvector;
import com.springai.rag.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query(value = "SELECT * FROM documents ORDER BY embedding <-> ?1::vector LIMIT ?2", 
           nativeQuery = true)
    List<Document> findSimilarDocuments(PGvector embedding, int limit);
}

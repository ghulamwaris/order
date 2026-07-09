package com.springai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngestChunkedDocumentResponse {
    private Integer totalChunks;
    private List<Long> documentIds;
    private String message;
    private LocalDateTime createdAt;
}

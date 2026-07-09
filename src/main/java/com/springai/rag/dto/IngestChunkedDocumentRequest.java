package com.springai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngestChunkedDocumentRequest {
    private String content;
    private String chunkingStrategy; // "default", "small", "large", "strict" (default: "default")
}

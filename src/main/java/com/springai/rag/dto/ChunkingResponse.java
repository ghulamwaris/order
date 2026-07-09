package com.springai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkingResponse {
    private Integer totalChunks;
    private String chunkingStrategy;
    private List<String> chunks;
    private String message;
}

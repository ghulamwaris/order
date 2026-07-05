-- Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Create documents table
CREATE TABLE IF NOT EXISTS documents (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    embedding vector(1536),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for faster similarity search
CREATE INDEX IF NOT EXISTS idx_documents_embedding 
    ON documents USING ivfflat (embedding vector_cosine_ops) 
    WITH (lists = 100);

-- Create index on created_at for time-based queries
CREATE INDEX IF NOT EXISTS idx_documents_created_at 
    ON documents (created_at DESC);

# RAG Q&A Implementation with PgVector

This module implements a Retrieval-Augmented Generation (RAG) system using Spring Boot and PostgreSQL with pgvector extension for vector similarity search.

## Overview

The RAG system combines document retrieval with LLM capabilities to provide accurate, context-aware answers to questions.

### Architecture

1. **Document Ingestion**: Documents are ingested, converted to embeddings using OpenAI's API, and stored in PostgreSQL with pgvector.
2. **Semantic Search**: User questions are converted to embeddings and matched against stored documents using vector similarity.
3. **Answer Generation**: Retrieved documents are used as context for an LLM (OpenAI's GPT-3.5-turbo) to generate accurate answers.

## Prerequisites

- Java 17+
- Spring Boot 3.4.5+
- PostgreSQL 14+ with pgvector extension
- OpenAI API key

## Setup

### 1. Install PostgreSQL with pgvector

```bash
# On macOS with Homebrew
brew install postgresql@15
brew services start postgresql@15

# Create database
createdb order_db

# Install pgvector extension
psql -U postgres -d order_db -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

### 2. Configure Environment

Set your OpenAI API key:

```bash
export OPENAI_API_KEY="your-openai-api-key"
```

Or update `src/main/resources/application.properties`:

```properties
openai.api.key=your-openai-api-key
spring.datasource.url=jdbc:postgresql://localhost:5432/order_db
spring.datasource.username=postgres
spring.datasource.password=your-password
```

### 3. Run Database Migration

The migration script will automatically run when the application starts:

```sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE documents (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    embedding vector(1536),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX ON documents USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
```

## API Endpoints

### 1. Ingest Document

**POST** `/api/rag/ingest`

Ingest a new document for the knowledge base.

**Request:**
```json
{
  "content": "Machine learning is a subset of artificial intelligence that enables systems to learn and improve from experience without being explicitly programmed."
}
```

**Response:**
```json
{
  "id": 1,
  "message": "Document ingested successfully",
  "createdAt": "2024-07-02 10:30:45"
}
```

### 2. Ask Question

**POST** `/api/rag/ask`

Ask a question about the ingested documents.

**Request:**
```json
{
  "question": "What is machine learning?"
}
```

**Response:**
```json
{
  "question": "What is machine learning?",
  "answer": "Machine learning is a subset of artificial intelligence that enables systems to learn and improve from experience without being explicitly programmed...",
  "timestamp": "2024-07-02 10:31:20"
}
```

### 3. Health Check

**GET** `/api/rag/health`

Check if the RAG service is running.

## Usage Example

### Using cURL

```bash
# Ingest a document
curl -X POST http://localhost:8080/api/rag/ingest \
  -H "Content-Type: application/json" \
  -d '{"content":"Your document content here"}'

# Ask a question
curl -X POST http://localhost:8080/api/rag/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"What is the document about?"}'
```

## Dependencies Added

- Spring Data JPA
- PostgreSQL JDBC Driver 42.6.0
- PgVector 0.1.0
- Lombok

## Next Steps

1. Update your database connection settings in application.properties
2. Set your OPENAI_API_KEY environment variable
3. Run the application: `mvn spring-boot:run`
4. Test the endpoints using the examples above

## Error Handling

Common issues and solutions are documented in the README_RAG.md file.

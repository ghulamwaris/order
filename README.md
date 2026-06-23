# Order Management & AI Chat System

A Spring Boot application with REST APIs for Order CRUD operations, AI-powered Q&A chat, and Kafka message publishing.

## Features

- **Order CRUD APIs** - Create, Read, Update, Delete orders with in-memory storage
- **Chat/AI Integration** - Ask questions and get AI responses using Spring AI + Ollama
- **Web UI** - jQuery + JSP-based frontend for managing orders and asking questions
- **Kafka Integration** - Publish messages to Kafka topics
- **Resilience4j** - Circuit breaker pattern for fault tolerance
- **Server-Sent Events (SSE)** - Streaming responses from AI

## Tech Stack

- **Framework**: Spring Boot 3.4.5
- **Language**: Java 17
- **View Layer**: JSP + jQuery
- **Message Queue**: Apache Kafka
- **AI/LLM**: Spring AI + Ollama
- **Resilience**: Resilience4j Circuit Breaker

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker & Docker Compose (for Kafka/Zookeeper)
- Ollama (for AI chat features)

## Setup & Running

### 1. Start Kafka & Zookeeper

```bash
docker-compose up -d
```

This starts:
- **Zookeeper** on port 2181
- **Kafka** on port 9092

### 2. Build the Application

```bash
./mvnw -DskipTests package
```

### 3. Run the Application

```bash
java -jar target/order-0.0.1-SNAPSHOT.jar
```

The application starts on port `9090`.

### 4. Access the UI

Open your browser and go to:
```
http://localhost:9090/
```

## API Endpoints

### Order CRUD APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/order` | ❌ Redirect to `/order/all` |
| GET | `/order/all` | List all orders |
| GET | `/order/{id}` | Get order by ID |
| POST | `/order` | Create new order (HTTP 201) |
| PUT | `/order/{id}` | Update order (HTTP 200 or 404) |
| DELETE | `/order/{id}` | Delete order (HTTP 204 or 404) |

### Chat/Ask APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/order/ask` | Ask question (query params) |
| POST | `/order/askQuestion` | Ask question (JSON body) |
| POST | `/order/askQuestion2` | Ask question (raw string body) |
| POST | `/order/stream` | Stream response (SSE) |

### Kafka APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/kafka/publish` | Publish message to Kafka topic |

## Example API Calls

### Create Order

```bash
curl -X POST http://localhost:9090/order \
  -H "Content-Type: application/json" \
  -d '{"id":0,"details":"Order for 10 laptops"}'
```

### List All Orders

```bash
curl http://localhost:9090/order/all
```

### Update Order

```bash
curl -X PUT http://localhost:9090/order/1 \
  -H "Content-Type: application/json" \
  -d '{"id":1,"details":"Updated order details"}'
```

### Delete Order

```bash
curl -X DELETE http://localhost:9090/order/1
```

### Ask AI Question

```bash
curl -X POST http://localhost:9090/order/askQuestion \
  -H "Content-Type: application/json" \
  -d '{"questionText":"What is Java 17?","converSationId":"conv-123"}'
```

### Publish to Kafka

```bash
curl -X POST http://localhost:9090/kafka/publish \
  -H "Content-Type: application/json" \
  -d '{"topic":"orders-topic","message":"Order placed: #12345"}'
```

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server & AI
server.port=9090
spring.ai.ollama.chat.model=phi3

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
app.kafka.default-topic=orders-topic

# Zookeeper (for reference)
app.zookeeper.connect=localhost:2181

# JSP
spring.mvc.view.prefix=/jsp/
spring.mvc.view.suffix=.jsp

# Circuit Breaker
resilience4j.circuitbreaker.instances.order-service.sliding-window-size=10
resilience4j.circuitbreaker.instances.order-service.failure-rate-threshold=50
```

## Project Structure

```
src/main/
├── java/com/springai/
│   ├── config/
│   │   ├── OrderApplication.java        (Spring Boot main)
│   │   └── KafkaConfig.java             (Kafka producer config)
│   ├── configuration/
│   │   └── AIConfig.java                (AI/Chat config)
│   ├── controller/
│   │   ├── HomeController.java          (JSP routing)
│   │   ├── OrderController.java         (Order CRUD + Chat APIs)
│   │   └── KafkaController.java         (Kafka publish API)
│   ├── dto/
│   │   ├── Order.java
│   │   ├── PublishRequest.java          (Kafka payload)
│   │   ├── QuestionRequest.java
│   │   └── QuestionResponse.java
│   └── service/
│       ├── OrderService.java            (Interface)
│       └── OrderServiceImpl.java         (Implementation)
├── resources/
│   ├── application.properties
│   ├── static/
│   └── templates/
└── webapp/jsp/
    └── index.jsp                        (Main UI - Order & Chat)
```

## Storage

- **Orders**: In-memory storage using ConcurrentHashMap (resets on restart)
- **For persistence**: Can be upgraded to JPA + H2/MySQL

## Troubleshooting

### Kafka Connection Issues
- Ensure `docker-compose up` is running
- Check `spring.kafka.bootstrap-servers` in application.properties
- Verify ports: Zookeeper (2181), Kafka (9092)

### AI Chat Not Working
- Ensure Ollama is running (typically on port 11434)
- Check `spring.ai.ollama.chat.model` is available in Ollama

### JSP Not Loading
- Verify `tomcat-embed-jasper` and `spring-boot-starter-jstl` are in pom.xml
- Check `spring.mvc.view.prefix` and `spring.mvc.view.suffix` in application.properties

### Port Already in Use
- Change `server.port` in application.properties

## Next Steps

- Add database persistence (Spring Data JPA + H2/PostgreSQL)
- Add Kafka consumer listener
- Add API documentation (Swagger/OpenAPI)
- Add unit/integration tests
- Add authentication & authorization
- Add request validation & error handling

## License

MIT


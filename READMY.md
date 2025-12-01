# Kafka Chat Service

A simple multi-room chat built with **Spring Boot**, **WebSocket (STOMP)**, and **Apache Kafka**.

* Clients connect via WebSocket to `/ws-chat`.
* Room messages are sent to `/app/rooms/{roomId}/send`.
* New messages are broadcast to `/topic/rooms/{roomId}`.
* The last N messages of each room are stored in memory and in a compacted Kafka topic `chat.room_state`.
* There is a REST endpoint to fetch history: `GET /api/rooms/{roomId}/history`.

---

## Stack

* **Java 17+**
* **Spring Boot 3.x**

  * `spring-boot-starter-web`
  * `spring-boot-starter-websocket`
  * `spring-kafka`
* **Apache Kafka 3.8 (KRaft, without ZooKeeper)** – in Docker
* **Kafka UI** (`provectuslabs/kafka-ui`) – to inspect topics/messages
* **Maven** – build tool
* (optional) Lombok, DevTools, Actuator

---

## Architecture (briefly)

1. **Kafka:**

   * `chat.messages` – main chat events topic (key = `roomId`).
   * `chat.room_state` – compacted topic with snapshots of the last N messages per room.

2. **Spring Boot service:**

   * WebSocket/STOMP endpoint `/ws-chat`.
   * `@MessageMapping("/rooms/{roomId}/send")` → Kafka `chat.messages`.
   * `@KafkaListener(chat.messages)` → `SimpMessagingTemplate` → `/topic/rooms/{roomId}`.
   * `RoomStateService` listens to `chat.messages`, updates an in-memory buffer and sends snapshots to `chat.room_state`.
   * REST: `GET /api/rooms/{roomId}/history` → returns the list of the latest messages in the room.

3. **Client (`test-client.html`):**

   * Connects to `/ws-chat` using SockJS + STOMP.
   * SUBSCRIBE to `/topic/rooms/{roomId}`.
   * SEND to `/app/rooms/{roomId}/send` with JSON `{ "text": "..." }`.
   * On page load, performs `GET /api/rooms/{roomId}/history`.

---

## Requirements

* JDK 17+
* Maven 3+
* Docker / Docker Compose

---

## Running Kafka + Kafka UI

Start:

```bash
docker compose up -d
```

After that:

* Kafka is available at `localhost:9092`
* Kafka UI is available at `http://localhost:8081`

The topics `chat.messages` and `chat.room_state` can be created automatically (if `KAFKA_AUTO_CREATE_TOPICS_ENABLE=true`) or manually via Kafka UI.

---

## Spring Boot configuration

`src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      auto-offset-reset: latest
    producer:
      acks: all

logging:
  level:
    com.example.chat: DEBUG
```

---

## Build and run the application

```bash
mvn clean package
mvn spring-boot:run
# or
java -jar target/chat-service-0.0.1-SNAPSHOT.jar
```

By default, the service will be available at `http://localhost:8080`.

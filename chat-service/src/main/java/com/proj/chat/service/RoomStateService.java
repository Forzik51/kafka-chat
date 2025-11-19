package com.proj.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.chat.model.ChatMessage;
import com.proj.chat.model.RoomSnapshot;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomStateService {
    private static final int MAX_MESSAGES_PER_ROOM = 100;

    private final Map<String, Deque<ChatMessage>> buffers = new HashMap<>();

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RoomStateService(KafkaTemplate<String,String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    //Get the message history from memory
    public List<ChatMessage> getHistory(String roomId) {
        Deque<ChatMessage> messages = buffers.get(roomId);
        if (messages == null) {
            return List.of();
        }
        return new ArrayList<>(messages);
    }

    @KafkaListener(topics = "chat.messages", groupId = "room-state-service")
    public void handleChatMessage(String message) throws JsonProcessingException {
        ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
        String roomId = chatMessage.getRoomId();

        Deque<ChatMessage> messages = buffers.computeIfAbsent(roomId, k -> new LinkedList<>());
        messages.add(chatMessage);
        if (messages.size() > MAX_MESSAGES_PER_ROOM) {
            messages.removeFirst();
        }


        RoomSnapshot snapshot = new RoomSnapshot(roomId, new ArrayList<>(messages));
        String snapshotJson = objectMapper.writeValueAsString(snapshot);

        kafkaTemplate.send("chat.room_state", snapshotJson);
    }
}

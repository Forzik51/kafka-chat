package com.proj.chat.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.chat.model.ChatMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatKafkaListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatKafkaListener(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper, SimpMessagingTemplate simpMessagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @KafkaListener(topics = "chat.messages", groupId = "ws-gateway")
    public void listen(String value) throws JsonProcessingException {
        ChatMessage chatMessage = objectMapper.readValue(value, ChatMessage.class);
        String roomId = chatMessage.getRoomId();

        //Send to all followers
        simpMessagingTemplate.convertAndSend("/topic/rooms/" + roomId, chatMessage);
    }
}

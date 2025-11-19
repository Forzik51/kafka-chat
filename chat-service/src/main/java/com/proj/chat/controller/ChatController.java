package com.proj.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.chat.model.ChatMessage;
import com.proj.chat.model.IncomingChatMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class ChatController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ChatController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    //User sends on /rooms/{roomId}/send
    @MessageMapping("/rooms/{roomId}/send")
    public void send(@DestinationVariable String roomId, IncomingChatMessage incoming,
                     Principal principal) throws Exception {
        ChatMessage msg = new ChatMessage();
        msg.setMessageId(UUID.randomUUID().toString());
        msg.setRoomId(roomId);
        msg.setUserId(principal != null ? principal.getName() : "anonymous");
        msg.setText(incoming.getText());
        msg.setTs(System.currentTimeMillis());

        String json = objectMapper.writeValueAsString(msg);
        kafkaTemplate.send("chat.messages", roomId, json); //key-roomId
    }
}

package com.proj.chat.model;

import lombok.Getter;
import lombok.Setter;

import java.security.Principal;

@Setter
@Getter
public class ChatMessage {
    private String messageId;
    private String roomId;
    private String userId;
    private String text;
    private long ts;

    public ChatMessage(){}



}

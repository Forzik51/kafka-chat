package com.proj.chat.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomSnapshot {
    private String roomId;
    private List<ChatMessage> messages;

    public RoomSnapshot(String roomId, List<ChatMessage> messages) {
        this.roomId = roomId;
        this.messages = messages;
    }
}

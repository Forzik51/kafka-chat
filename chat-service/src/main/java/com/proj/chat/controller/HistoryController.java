package com.proj.chat.controller;

import com.proj.chat.model.ChatMessage;
import com.proj.chat.service.RoomStateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class HistoryController {
    private final RoomStateService roomStateService;

    public HistoryController(RoomStateService roomStateService) {
        this.roomStateService = roomStateService;
    }

    @GetMapping("/{roomId}/history")
    public List<ChatMessage> getHistory(@PathVariable String roomId) {
        return roomStateService.getHistory(roomId);
    }
}

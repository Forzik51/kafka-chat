package com.proj.chat.model;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IncomingChatMessage {

    private String text;

    public IncomingChatMessage() {}
}

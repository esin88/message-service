package com.example.message.service.message;

import java.util.Collections;
import java.util.List;

public class MessageListResponse {
    public List<MessageResponse> messages = Collections.emptyList();

    public MessageListResponse() {
    }

    public MessageListResponse(List<MessageResponse> messages) {
        this.messages = messages;
    }
}

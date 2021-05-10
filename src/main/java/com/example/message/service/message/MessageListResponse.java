package com.example.message.service.message;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

@Schema(title = "Message list response")
public class MessageListResponse {
    public List<MessageResponse> messages = Collections.emptyList();

    public MessageListResponse() {
    }

    public MessageListResponse(List<MessageResponse> messages) {
        this.messages = messages;
    }
}

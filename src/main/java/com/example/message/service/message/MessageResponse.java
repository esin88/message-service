package com.example.message.service.message;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(title = "Message response")
public class MessageResponse {
    public long id;
    public String author;
    public String header;
    public String body;
    public LocalDateTime createdAt;
    public LocalDateTime editedAt;

    public MessageResponse(long id, String author, String header, String body, LocalDateTime createdAt, LocalDateTime editedAt) {
        this.id = id;
        this.author = author;
        this.header = header;
        this.body = body;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }
}

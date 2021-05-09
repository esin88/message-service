package com.example.message.service.message;

import java.time.LocalDateTime;

public class MessageEntity {
    private long id;
    private final String ownerName;
    private String header;
    private String body;
    private final LocalDateTime createdAt;
    private LocalDateTime editedAt;

    public MessageEntity(String ownerName, String header, String body) {
        this.ownerName = ownerName;
        this.header = header;
        this.body = body;
        this.createdAt = LocalDateTime.now();
        this.editedAt = null;
    }

    public void update(String header, String body) {
        this.header = header;
        this.body = body;
        this.editedAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }
}

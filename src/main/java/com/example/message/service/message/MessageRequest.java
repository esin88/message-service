package com.example.message.service.message;

public class MessageRequest {
    public String header;
    public String body;

    public MessageRequest(String header, String body) {
        this.header = header;
        this.body = body;
    }
}

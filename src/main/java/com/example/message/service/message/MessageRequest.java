package com.example.message.service.message;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(title = "Request for Creating/Updating message")
public class MessageRequest {
    @Schema(required = true)
    public String header;
    @Schema(required = true)
    public String body;

    public MessageRequest(String header, String body) {
        this.header = header;
        this.body = body;
    }
}

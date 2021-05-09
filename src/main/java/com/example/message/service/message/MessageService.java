package com.example.message.service.message;

import io.quarkus.security.UnauthorizedException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;

@ApplicationScoped
public class MessageService {
    @Inject
    private MessageRepository repository;

    public MessageListResponse getAll(int limit, int offset) {
        return repository.getAll(limit, offset).stream()
            .map(MessageService::entityToResponse)
            .collect(collectingAndThen(Collectors.toList(), MessageListResponse::new));
    }

    public MessageResponse getById(long messageId) {
        var message = getMessageOrThrow(messageId);
        return entityToResponse(message);
    }

    public void create(String userName, MessageRequest request) {
        repository.create(new MessageEntity(userName, request.header, request.body));
    }

    public void update(String userName, long messageId, MessageRequest request) {
        var message = getMessageForOwner(userName, messageId);
        message.update(request.header, request.body);
        repository.update(message);
    }

    public void delete(String userId, long messageId) {
        getMessageForOwner(userId, messageId);
        repository.delete(messageId);
    }

    private MessageEntity getMessageForOwner(String userName, long messageId) {
        var message = getMessageOrThrow(messageId);
        if (!message.getOwnerName().equals(userName)) {
            throw new UnauthorizedException();
        }
        return message;
    }

    private MessageEntity getMessageOrThrow(long messageId) {
        var message = repository.getById(messageId);
        return message.orElseThrow(NotFoundException::new);
    }

    private static MessageResponse entityToResponse(MessageEntity entity) {
        return new MessageResponse(
            entity.getId(),
            entity.getOwnerName(),
            entity.getHeader(),
            entity.getBody(),
            entity.getCreatedAt(),
            entity.getEditedAt()
        );
    }
}

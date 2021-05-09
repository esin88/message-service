package com.example.message.service.message;

import javax.enterprise.context.ApplicationScoped;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MessageRepositoryInMemory implements MessageRepository {
    private final LinkedHashMap<Long, MessageEntity> messages = new LinkedHashMap<>();

    @Override
    public List<MessageEntity> getAll(int limit, int offset) {
        final List<MessageEntity> result;
        synchronized (messages) {
            result = messages.values().stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public MessageEntity getById(long id) {
        return messages.get(id);
    }

    @Override
    public void create(MessageEntity message) {
        synchronized (messages) {
            messages.put(message.getId(), message);
        }
    }

    @Override
    public void update(MessageEntity message) {
        synchronized (messages) {
            messages.put(message.getId(), message);
        }
    }

    @Override
    public void delete(long id) {
        synchronized (messages) {
            messages.remove(id);
        }
    }
}

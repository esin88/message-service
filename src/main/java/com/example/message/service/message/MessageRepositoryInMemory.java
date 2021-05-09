package com.example.message.service.message;

import javax.inject.Singleton;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class MessageRepositoryInMemory implements MessageRepository {
    private static final LinkedHashMap<Long, MessageEntity> MESSAGES = new LinkedHashMap<>();

    @Override
    public List<MessageEntity> getAll(int limit, int offset) {
        final List<MessageEntity> result;
        synchronized (MESSAGES) {
            result = MESSAGES.values().stream()
                .limit(limit)
                .skip(offset)
                .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public MessageEntity getById(long id) {
        return MESSAGES.get(id);
    }

    @Override
    public void create(MessageEntity message) {
        synchronized (MESSAGES) {
            MESSAGES.put(message.getId(), message);
        }
    }

    @Override
    public void update(MessageEntity message) {
        synchronized (MESSAGES) {
            MESSAGES.put(message.getId(), message);
        }
    }

    @Override
    public void delete(long id) {
        synchronized (MESSAGES) {
            MESSAGES.remove(id);
        }
    }
}

package com.example.message.service.message;

import javax.enterprise.context.ApplicationScoped;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@ApplicationScoped
public class MessageRepositoryInMemory implements MessageRepository {
    private static final AtomicLong ID_GENERATOR = new AtomicLong();
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
    public Optional<MessageEntity> getById(long id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public long create(MessageEntity message) {
        message.setId(ID_GENERATOR.getAndIncrement());
        synchronized (messages) {
            messages.put(message.getId(), message);
        }
        return message.getId();
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

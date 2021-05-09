package com.example.message.service.message;

import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    List<MessageEntity> getAll(int limit, int offset);

    Optional<MessageEntity> getById(long id);

    long create(MessageEntity message);

    void update(MessageEntity message);

    void delete(long id);
}

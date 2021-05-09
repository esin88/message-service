package com.example.message.service.message;

import java.util.List;

public interface MessageRepository {
    List<MessageEntity> getAll(int limit, int offset);

    MessageEntity getById(long id);

    void create(MessageEntity message);

    void update(MessageEntity message);

    void delete(long id);
}

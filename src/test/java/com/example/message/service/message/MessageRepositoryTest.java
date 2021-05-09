package com.example.message.service.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class MessageRepositoryTest {
    private MessageRepositoryInMemory repository;

    @BeforeEach
    void setupRepository() {
        repository = new MessageRepositoryInMemory();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    void getAllUsesCorrectLimit(int limit) {
        preCreateEntities(20);

        var entities = repository.getAll(limit, 0);
        assertEquals(limit, entities.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    void getAllUsesCorrectOffset(int offset) {
        var ids = preCreateEntities(20);

        var entities = repository.getAll(10, offset);
        assertEquals(10, entities.size());
        assertEquals(ids.get(offset), entities.get(0).getId());
    }

    @Test
    void getByIdReturnsEmptyForNonExistingMessage() {
        var entity = repository.getById(1L);
        assertTrue(entity.isEmpty());
    }

    @Test
    void getByIdReturnsNonEmptyForExistingMessage() {
        var id = preCreateEntities(1).get(0);

        var entity = repository.getById(id);
        assertTrue(entity.isPresent());
    }

    @Test
    void createAssignsIdAndCreatesEntity() {
        var id = repository.create(getEntity());

        var entity = repository.getById(id);
        assertTrue(entity.isPresent());
        var message = entity.get();
        assertNotNull(message.getCreatedAt());
        assertNull(message.getEditedAt());
    }

    @Test
    void updateSavesMessage() {
        var entity = getEntity();
        repository.create(entity);
        entity.update("new header", "new body");

        repository.update(entity);
        var updatedEntity = repository.getById(entity.getId());

        assertTrue(updatedEntity.isPresent());
        var message = updatedEntity.get();
        assertEquals(entity.getHeader(), message.getHeader());
        assertEquals(entity.getBody(), message.getBody());
    }

    @Test
    void deleteRemovesMessage() {
        var id = preCreateEntities(1).get(0);

        repository.delete(id);

        var entity = repository.getById(id);
        assertTrue(entity.isEmpty());
    }

    private List<Long> preCreateEntities(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> repository.create(getEntity()))
            .collect(Collectors.toList());
    }

    private static MessageEntity getEntity() {
        return new MessageEntity("owner", "header", "body");
    }
}

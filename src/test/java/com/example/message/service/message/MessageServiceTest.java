package com.example.message.service.message;

import io.quarkus.security.UnauthorizedException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class MessageServiceTest {
    private static final int LIMIT = 10;
    private static final int OFFSET = 20;
    private static final long MESSAGE_ID = 1L;
    private static final String USER_NAME = "bob";
    private static final String WRONG_USER = "tester";
    private static final String HEADER = "header";
    private static final String BODY = "body";

    private static final ArgumentMatcher<MessageEntity> ENTITY_MATCHER = entity ->
        entity.getOwnerName().equals(USER_NAME)
            && entity.getHeader().equals(HEADER)
            && entity.getBody().equals(BODY);

    @Inject
    MessageService service;
    @InjectMock
    MessageRepository repository;

    @Test
    void getAllCallsRepositoryGetAll() {
        service.getAll(LIMIT, OFFSET);
        verify(repository).getAll(eq(LIMIT), eq(OFFSET));
    }

    @Test
    void getByIdThrowsExceptionIfMessageIdDoesntExist() {
        doReturn(Optional.empty()).when(repository).getById(anyInt());

        assertThrows(NotFoundException.class, () -> service.getById(MESSAGE_ID));
        verify(repository).getById(eq(MESSAGE_ID));
    }

    @Test
    void createCallsRepositoryCreate() {
        service.create(USER_NAME, getRequest());

        verify(repository).create(argThat(ENTITY_MATCHER));
    }

    @Test
    void updateThrowsExceptionIfMessageIdDoesntExist() {
        doReturn(Optional.empty()).when(repository).getById(anyInt());

        assertThrows(NotFoundException.class, () -> service.update(USER_NAME, MESSAGE_ID, getRequest()));

        verify(repository).getById(eq(MESSAGE_ID));
        verify(repository, never()).update(argThat(ENTITY_MATCHER));
    }

    @Test
    void updateThrowsExceptionIfWrongOwner() {
        doReturn(Optional.of(getEntity())).when(repository).getById(eq(MESSAGE_ID));

        assertThrows(UnauthorizedException.class, () -> service.update(WRONG_USER, MESSAGE_ID, getRequest()));

        verify(repository).getById(eq(MESSAGE_ID));
        verify(repository, never()).update(argThat(ENTITY_MATCHER));
    }

    @Test
    void updateCallsRepositoryUpdate() {
        doReturn(Optional.of(getEntity())).when(repository).getById(eq(MESSAGE_ID));

        service.update(USER_NAME, MESSAGE_ID, getRequest());

        var order = inOrder(repository);
        order.verify(repository).getById(eq(MESSAGE_ID));
        order.verify(repository).update(argThat(ENTITY_MATCHER));
    }

    @Test
    void deleteThrowsExceptionIfMessageIdDoesntExist() {
        doReturn(Optional.empty()).when(repository).getById(anyInt());

        assertThrows(NotFoundException.class, () -> service.delete(USER_NAME, MESSAGE_ID));

        verify(repository).getById(eq(MESSAGE_ID));
        verify(repository, never()).delete(eq(MESSAGE_ID));
    }

    @Test
    void deleteThrowsExceptionIfWrongOwner() {
        doReturn(Optional.of(getEntity())).when(repository).getById(eq(MESSAGE_ID));

        assertThrows(UnauthorizedException.class, () -> service.delete(WRONG_USER, MESSAGE_ID));

        verify(repository).getById(eq(MESSAGE_ID));
        verify(repository, never()).delete(eq(MESSAGE_ID));
    }

    @Test
    void deleteCallsRepositoryUpdate() {
        doReturn(Optional.of(getEntity())).when(repository).getById(eq(MESSAGE_ID));

        service.delete(USER_NAME, MESSAGE_ID);

        var order = inOrder(repository);
        order.verify(repository).getById(eq(MESSAGE_ID));
        order.verify(repository).delete(eq(MESSAGE_ID));
    }


    private static MessageRequest getRequest() {
        return new MessageRequest(HEADER, BODY);
    }

    private static MessageEntity getEntity() {
        return new MessageEntity(USER_NAME, HEADER, BODY);
    }
}

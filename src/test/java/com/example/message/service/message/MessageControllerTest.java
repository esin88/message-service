package com.example.message.service.message;

import io.quarkus.security.UnauthorizedException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class MessageControllerTest {
    private static final String EXISTING_USER = "bob";
    private static final String NEW_USER = "tester";

    private static final long EXISTING_MESSAGE_ID = 1L;
    private static final long NON_EXISTING_MESSAGE_ID = 888L;
    private static final long LIST_MESSAGE_ID_2 = 2L;
    private static final long LIST_MESSAGE_ID_3 = 3L;

    @InjectMock
    MessageService service;

    @BeforeEach
    void setupGetMessageById() {
        doReturn(createResponse(EXISTING_MESSAGE_ID)).when(service).getById(eq(EXISTING_MESSAGE_ID));
    }

    @Test
    void getAllWorksWithoutAuthentication() {
        doReturn(createListResponse(createResponse(LIST_MESSAGE_ID_2), createResponse(LIST_MESSAGE_ID_3)))
            .when(service).getAll(anyInt(), anyInt());

        var response = given()
            .when().get("/message/all");

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        final var messageListResponse = response.as(MessageListResponse.class);
        assertNotNull(messageListResponse);
        assertNotNull(messageListResponse.messages);
        assertEquals(2, messageListResponse.messages.size());
    }

    @Test
    void getLimitWorksWithoutAuthentication() {
        doReturn(createListResponse(createResponse(LIST_MESSAGE_ID_2)))
            .when(service).getAll(eq(1), anyInt());

        var response = given()
            .when().get("/message/all?limit=1");

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        final var messageListResponse = response.as(MessageListResponse.class);
        assertEquals(1, messageListResponse.messages.size());
        assertEquals(LIST_MESSAGE_ID_2, messageListResponse.messages.get(0).id);
    }

    @Test
    void getLimitOffsetWorksWithoutAuthentication() {
        doReturn(createListResponse(createResponse(LIST_MESSAGE_ID_3)))
            .when(service).getAll(eq(1), eq(1));

        var response = given()
            .when().get("/message/all?limit=1&offset=1");

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        final var messageListResponse = response.as(MessageListResponse.class);
        assertEquals(1, messageListResponse.messages.size());
        assertEquals(LIST_MESSAGE_ID_3, messageListResponse.messages.get(0).id);
    }

    @Test
    void getByIdWorksWithoutAuthentication() {
        var response = given()
            .when().get("/message/" + EXISTING_MESSAGE_ID);

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        final var messageResponse = response.as(MessageResponse.class);
        assertEquals(EXISTING_MESSAGE_ID, messageResponse.id);
    }

    @Test
    void getByIdReturns404ForNonExistingId() {
        doThrow(NotFoundException.class)
            .when(service).getById(anyLong());

        var response = given()
            .when().get("/message/" + NON_EXISTING_MESSAGE_ID);

        assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode());
    }

    @Test
    void createDoesntWorkWithoutAuthentication() {
        var messageRequest = new MessageRequest("head", "body");

        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(messageRequest)
            .post("/message/");

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    @TestSecurity(user = NEW_USER, roles = {"user"})
    void createWorksWithAuthentication() {
        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .post("/message/");

        assertEquals(HttpStatus.SC_OK, response.statusCode());
    }

    @Test
    void updateDoesntWorkWithoutAuthentication() {
        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .put("/message/" + EXISTING_MESSAGE_ID);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    @TestSecurity(user = NEW_USER, roles = {"user"})
    void updateDoesntWorkWithWrongUser() {
        doThrow(UnauthorizedException.class)
            .when(service).update(anyString(), anyLong(), any());

        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .put("/message/" + EXISTING_MESSAGE_ID);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    @TestSecurity(user = NEW_USER, roles = {"user"})
    void updateThrows404ForWrongMessageId() {
        doThrow(NotFoundException.class)
            .when(service).update(anyString(), anyLong(), any());

        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .put("/message/" + NON_EXISTING_MESSAGE_ID);

        assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode());
    }

    @Test
    @TestSecurity(user = EXISTING_USER, roles = {"user"})
    void updateWorksWithRightUser() {
        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .put("/message/" + EXISTING_MESSAGE_ID);

        assertEquals(HttpStatus.SC_OK, response.statusCode());
    }

    @Test
    void deleteDoesntWorkWithoutAuthentication() {
        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .delete("/message/" + EXISTING_MESSAGE_ID);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    @TestSecurity(user = NEW_USER, roles = {"user"})
    void deleteDoesntWorkWithWrongUser() {
        doThrow(UnauthorizedException.class)
            .when(service).delete(anyString(), anyLong());

        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .delete("/message/" + EXISTING_MESSAGE_ID);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    @TestSecurity(user = NEW_USER, roles = {"user"})
    void deleteThrows404ForWrongMessageId() {
        doThrow(NotFoundException.class)
            .when(service).delete(anyString(), anyLong());

        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .delete("/message/888");

        assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode());
    }

    @Test
    @TestSecurity(user = EXISTING_USER, roles = {"user"})
    void deleteWorksWithRightUser() {
        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .delete("/message/" + EXISTING_MESSAGE_ID);

        assertEquals(HttpStatus.SC_OK, response.statusCode());
    }

    private static MessageResponse createResponse(long id) {
        return new MessageResponse(id, EXISTING_USER, "header", "body", LocalDateTime.now(), null);
    }

    private static MessageListResponse createListResponse(MessageResponse... responses) {
        return new MessageListResponse(List.of(responses));
    }
}

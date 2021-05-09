package com.example.message.service.message;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTest
public class MessageControllerTest {
    private static final String EXISTING_USER = "bob";
    private static final String NEW_USER = "tester";

    @Inject
    MessageRepository repository;
    private long existingId1;
    private long existingId2;

    @BeforeAll
    void setup() {
        var entity1 = createMessageEntity(1);
        existingId1 = entity1.getId();
        repository.create(entity1);
        var entity2 = createMessageEntity(2);
        existingId2 = entity2.getId();
        repository.create(entity2);
    }

    @Test
    void getAllWorksWithoutAuthentication() {
        var response = given()
            .when().get("/message/all");

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        final var messageListResponse = response.as(MessageListResponse.class);
        assertNotNull(messageListResponse);
        assertNotNull(messageListResponse.messages);
        assertTrue(messageListResponse.messages.size() >= 2);
    }

    @Test
    void getLimitWorksWithoutAuthentication() {
        var response = given()
            .when().get("/message/all?limit=1");

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        final var messageListResponse = response.as(MessageListResponse.class);
        assertEquals(1, messageListResponse.messages.size());
    }

    @Test
    void getLimitOffsetWorksWithoutAuthentication() {
        var response = given()
            .when().get("/message/all?limit=1&offset=1");

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        final var messageListResponse = response.as(MessageListResponse.class);
        assertEquals(1, messageListResponse.messages.size());
    }

    @Test
    void getByIdWorksWithoutAuthentication() {
        var response = given()
            .when().get("/message/" + existingId1);

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        final var messageResponse = response.as(MessageResponse.class);
        assertEquals(existingId1, messageResponse.id);
    }

    @Test
    void getByIdReturns404ForNonExistingId() {
        var response = given()
            .when().get("/message/888");

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
            .put("/message/" + existingId2);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    @TestSecurity(user = NEW_USER, roles = {"user"})
    void updateDoesntWorkWithWrongUser() {
        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .put("/message/" + existingId2);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    @TestSecurity(user = NEW_USER, roles = {"user"})
    void updateThrows404ForWrongMessageId() {
        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .put("/message/888");

        assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode());
    }

    @Test
    @TestSecurity(user = EXISTING_USER, roles = {"user"})
    void updateWorksWithRightUser() {
        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .put("/message/" + existingId2);

        assertEquals(HttpStatus.SC_OK, response.statusCode());
    }

    @Test
    void deleteDoesntWorkWithoutAuthentication() {
        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .delete("/message/" + existingId2);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    @TestSecurity(user = NEW_USER, roles = {"user"})
    void deleteDoesntWorkWithWrongUser() {
        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .delete("/message/" + existingId2);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
    }

    @Test
    @TestSecurity(user = NEW_USER, roles = {"user"})
    void deleteThrows404ForWrongMessageId() {
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
        var newEntity = createMessageEntity(3);
        var newEntityId = newEntity.getId();
        repository.create(newEntity);

        var response = given()
            .when()
            .contentType(ContentType.JSON)
            .body(new MessageRequest("head", "body"))
            .delete("/message/" + newEntityId);

        assertEquals(HttpStatus.SC_OK, response.statusCode());
    }

    private static MessageEntity createMessageEntity(int number) {
        return new MessageEntity(
            EXISTING_USER,
            "header " + number,
            "body " + number
        );
    }
}

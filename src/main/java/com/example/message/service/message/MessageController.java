package com.example.message.service.message;

import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SecuritySchemes(value = {
    @SecurityScheme(securitySchemeName = "basic", type = SecuritySchemeType.HTTP, scheme = "Basic")
})
@Path("/message")
public class MessageController {
    @Inject
    MessageService messageService;
    @Inject
    SecurityIdentity identity;

    @Operation(summary = "Get all messages with limit/offset")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "List of messages",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageListResponse.class))
        )
    })
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public MessageListResponse getAll(
        @Parameter(description = "Message number limit") @DefaultValue("100") @QueryParam("limit") int limit,
        @Parameter(description = "Message list offset") @DefaultValue("0") @QueryParam("offset") int offset
    ) {
        return messageService.getAll(limit, offset);
    }

    @Operation(summary = "Get message by id")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Message for specified id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))
        ),
        @APIResponse(
            responseCode = "404",
            description = "Message id not found"
        )
    })
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public MessageResponse getById(@PathParam long id) {
        return messageService.getById(id);
    }

    @Operation(summary = "Create message")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Message successfully created. Response content is new message id",
            content = {@Content(mediaType = "text/plain", example = "0, 1, 2 etc.")}
        ),
        @APIResponse(
            responseCode = "400",
            description = "Either message header or body is null or empty"
        ),
        @APIResponse(
            responseCode = "401",
            description = "User is not authenticated"
        )
    })
    @SecurityRequirement(name = "basic")
    @POST
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(MessageRequest request) {
        if (request.header == null || request.header.isEmpty()
            || request.body == null || request.body.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        var messageId = this.messageService.create(identity.getPrincipal().getName(), request);
        return Response.ok(messageId).build();
    }

    @Operation(summary = "Update message with specified id")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Message successfully updated"
        ),
        @APIResponse(
            responseCode = "400",
            description = "Either message header or body is null or empty"
        ),
        @APIResponse(
            responseCode = "401",
            description = "User is not authenticated"
        ),
        @APIResponse(
            responseCode = "403",
            description = "User is not authorized to update this message"
        ),
        @APIResponse(
            responseCode = "404",
            description = "Message id not found"
        )
    })
    @SecurityRequirement(name = "basic")
    @PUT
    @RolesAllowed("user")
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam int id, MessageRequest request) {
        if (request.header == null || request.header.isEmpty()
            || request.body == null || request.body.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        messageService.update(identity.getPrincipal().getName(), id, request);
        return Response.ok().build();
    }

    @Operation(summary = "Update message with specified id")
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Message successfully deleted"
        ),
        @APIResponse(
            responseCode = "401",
            description = "User is not authenticated"
        ),
        @APIResponse(
            responseCode = "403",
            description = "User is not authorized to delete this message"
        ),
        @APIResponse(
            responseCode = "404",
            description = "Message id not found"
        )
    })
    @SecurityRequirement(name = "basic")
    @DELETE
    @RolesAllowed("user")
    @Path("/{id}")
    public Response delete(@PathParam int id) {
        messageService.delete(identity.getPrincipal().getName(), id);
        return Response.ok().build();
    }
}

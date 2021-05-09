package com.example.message.service.message;

import io.quarkus.security.identity.SecurityIdentity;
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

@Path("/message")
public class MessageController {
    @Inject
    MessageService messageService;
    @Inject
    SecurityIdentity identity;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public MessageListResponse getAll(
        @DefaultValue("100") @QueryParam("limit") int limit,
        @DefaultValue("0") @QueryParam("offset") int offset
    ) {
        return messageService.getAll(limit, offset);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public MessageResponse getById(@PathParam long id) {
        return messageService.getById(id);
    }

    @POST
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(MessageRequest request) {
        messageService.create(identity.getPrincipal().getName(), request);
        return Response.ok().build();
    }

    @PUT
    @RolesAllowed("user")
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam int id, MessageRequest request) {
        messageService.update(identity.getPrincipal().getName(), id, request);
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed("user")
    @Path("/{id}")
    public Response delete(@PathParam int id) {
        messageService.delete(identity.getPrincipal().getName(), id);
        return Response.ok().build();
    }
}

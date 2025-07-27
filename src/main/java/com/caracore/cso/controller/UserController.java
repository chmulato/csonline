package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.caracore.cso.service.UserService;
import com.caracore.cso.entity.User;
import java.util.List;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Inject
    UserService userService;

    @GET
    public List<User> getAll() {
        try {
            return userService.findAll();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os usuários", e);
            throw e;
        }
    }

    @GET
    @Path("/{id}")
    public User getById(@PathParam("id") Long id) {
        try {
            return userService.findById(id);
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário por id: " + id, e);
            throw e;
        }
    }

    @POST
    public Response create(User user) {
        try {
            userService.save(user);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            logger.error("Erro ao criar usuário", e);
            return Response.serverError().entity("Erro ao criar usuário").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, User user) {
        try {
            user.setId(id);
            userService.update(user);
            return Response.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário id: " + id, e);
            return Response.serverError().entity("Erro ao atualizar usuário").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            userService.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            logger.error("Erro ao deletar usuário id: " + id, e);
            return Response.serverError().entity("Erro ao deletar usuário").build();
        }
    }
}

package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.caracore.cso.service.UserService;
import com.caracore.cso.entity.User;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.inject.Inject;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Inject
    private UserService userService;

    // Construtor padrão para CDI
    public UserController() {}

    // Construtor para injeção manual em testes
    public UserController(UserService userService) {
        this.userService = userService;
    }

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
            // Monta a URI do novo recurso criado
            String location = "/users/" + user.getId();
            return Response.status(Response.Status.CREATED)
                .header("Location", location)
                .build();
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
        } catch (RuntimeException e) {
            logger.error("Erro ao deletar usuário id: " + id, e);
            // Se for violação de integridade, retorna 409 e mensagem JSON
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar usuário id: " + id, e);
            return Response.serverError().entity("{\"error\": \"Erro ao deletar usuário\"}").type(MediaType.APPLICATION_JSON).build();
        }
    }
}

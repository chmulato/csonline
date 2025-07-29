package com.caracore.cso.controller;

import com.caracore.cso.entity.Team;
import com.caracore.cso.service.TeamService;
import com.caracore.cso.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/team")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamController {
    private final TeamService teamService = new TeamService();
    private final UserService userService = new UserService();

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Team team = teamService.findById(id);
        if (team == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(team).build();
    }

    @GET
    public List<Team> getAll() {
        return teamService.findAll();
    }

    @POST
    public Response create(Team team) {
        // Persistir usuários associados se necessário
        if (team.getBusiness() != null && team.getBusiness().getId() == null) {
            userService.save(team.getBusiness());
        }
        if (team.getCourier() != null && team.getCourier().getId() == null) {
            userService.save(team.getCourier());
        }
        try {
            teamService.save(team);
            return Response.status(Response.Status.CREATED).entity(team).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Team team) {
        Team existing = teamService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        // Persistir usuários associados se necessário
        if (team.getBusiness() != null && team.getBusiness().getId() == null) {
            userService.save(team.getBusiness());
        }
        if (team.getCourier() != null && team.getCourier().getId() == null) {
            userService.save(team.getCourier());
        }
        team.setId(id);
        try {
            teamService.save(team);
            return Response.ok(team).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Team existing = teamService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            teamService.delete(id);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            // Se for violação de integridade, retorna 409 e mensagem JSON
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"Erro ao deletar time\"}").type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).build();
        }
    }
}

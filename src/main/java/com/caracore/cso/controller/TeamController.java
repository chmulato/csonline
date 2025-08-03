package com.caracore.cso.controller;

import com.caracore.cso.entity.Team;
import com.caracore.cso.service.TeamService;
import com.caracore.cso.service.UserService;
import com.caracore.cso.service.CourierService;
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
    private final CourierService courierService = new CourierService();

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
        try {
            // Buscar business e courier pelos IDs recebidos do JSON
            Long businessId = team.getBusinessId();
            Long courierId = team.getCourierId();
            
            if (businessId != null) {
                team.setBusiness(userService.findById(businessId));
            }
            
            if (courierId != null) {
                team.setCourier(courierService.findById(courierId));
            }
            
            // Validações
            if (team.getBusiness() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Business não encontrado ou não especificado\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
            }
            
            if (team.getCourier() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Courier não encontrado ou não especificado\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
            }
            
            teamService.save(team);
            return Response.status(Response.Status.CREATED).entity(team).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Erro ao criar team: " + e.getMessage() + "\"}")
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
        
        try {
            // Buscar business e courier pelos IDs recebidos do JSON
            Long businessId = team.getBusinessId();
            Long courierId = team.getCourierId();
            
            if (businessId != null) {
                team.setBusiness(userService.findById(businessId));
            } else {
                team.setBusiness(existing.getBusiness());
            }
            
            if (courierId != null) {
                team.setCourier(courierService.findById(courierId));
            } else {
                team.setCourier(existing.getCourier());
            }
            
            // Validações
            if (team.getBusiness() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Business não encontrado ou não especificado\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
            }
            
            if (team.getCourier() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Courier não encontrado ou não especificado\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
            }
            
            team.setId(id);
            teamService.save(team);
            return Response.ok(team).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Erro ao atualizar team: " + e.getMessage() + "\"}")
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

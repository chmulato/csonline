package com.caracore.cso.controller;

import com.caracore.cso.entity.Courier;
import com.caracore.cso.service.CourierService;
import com.caracore.cso.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/couriers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourierController {
    private static final Logger logger = LogManager.getLogger(CourierController.class);

    private CourierService courierService;
    private UserService userService;

    public CourierController() {
        this.courierService = new CourierService();
        this.userService = new UserService();
    }

    // Construtor para injeção manual em testes
    public CourierController(CourierService courierService) {
        this.courierService = courierService;
        this.userService = new UserService();
    }

    @GET
    public List<Courier> getAll() {
        try {
            return courierService.findAll();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os couriers", e);
            throw e;
        }
    }

    @GET
    @Path("/{id}")
    public Courier getById(@PathParam("id") Long id) {
        try {
            return courierService.findById(id);
        } catch (Exception e) {
            logger.error("Erro ao buscar courier por id: " + id, e);
            throw e;
        }
    }

    @POST
    public Response create(Courier courier) {
        try {
            // Obter usuários pelos IDs
            Long businessId = courier.getBusinessId();
            Long userId = courier.getUserId();
            
            if (businessId != null) {
                courier.setBusiness(userService.findById(businessId));
            }
            
            if (userId != null) {
                courier.setUser(userService.findById(userId));
            }
            
            courierService.save(courier);
            return Response.status(Response.Status.CREATED).entity(courier).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Violação de unicidade ao criar courier: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            logger.error("Erro ao criar courier", e);
            return Response.serverError().entity("Erro ao criar courier").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Courier courier) {
        try {
            // Buscar o courier existente
            Courier existingCourier = courierService.findById(id);
            if (existingCourier == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Courier não encontrado\"}").build();
            }
            
            // Atualizar apenas os campos fornecidos
            if (courier.getFactorCourier() != null) {
                existingCourier.setFactorCourier(courier.getFactorCourier());
            }
            
            // Atualizar referências se IDs foram fornecidos
            Long businessId = courier.getBusinessId();
            Long userId = courier.getUserId();
            
            if (businessId != null) {
                existingCourier.setBusiness(userService.findById(businessId));
            }
            
            if (userId != null) {
                existingCourier.setUser(userService.findById(userId));
            }
            
            courierService.update(existingCourier);
            return Response.ok(existingCourier).build();
        } catch (Exception e) {
            logger.error("Erro ao atualizar courier id: " + id, e);
            return Response.serverError().entity("Erro ao atualizar courier").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            courierService.delete(id);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Erro ao deletar courier id: " + id, e);
            // Se for violação de integridade, retorna 409 e mensagem JSON
            return Response.status(jakarta.ws.rs.core.Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar courier id: " + id, e);
            return Response.serverError().entity("{\"error\": \"Erro ao deletar courier\"}").type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).build();
        }
    }
}


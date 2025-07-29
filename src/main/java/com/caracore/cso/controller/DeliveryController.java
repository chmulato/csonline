package com.caracore.cso.controller;

import com.caracore.cso.entity.Delivery;
import com.caracore.cso.service.DeliveryService;
import com.caracore.cso.service.UserService;
import com.caracore.cso.service.CustomerService;
import com.caracore.cso.service.CourierService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/deliveries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeliveryController {
    private static final Logger logger = LogManager.getLogger(DeliveryController.class);

    private DeliveryService deliveryService = new DeliveryService();
    private UserService userService = new UserService();
    private CustomerService customerService = new CustomerService();
    private CourierService courierService = new CourierService();

    @GET
    public List<Delivery> getAll() {
        try {
            return deliveryService.findAll();
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as deliveries", e);
            throw e;
        }
    }

    @GET
    @Path("/{id}")
    public Delivery getById(@PathParam("id") Long id) {
        try {
            return deliveryService.findById(id);
        } catch (Exception e) {
            logger.error("Erro ao buscar delivery por id: " + id, e);
            throw e;
        }
    }

    @POST
    public Response create(Delivery delivery) {
        try {
            // Persistir entidades associadas se necessário
            if (delivery.getBusiness() != null && delivery.getBusiness().getId() == null) {
                userService.save(delivery.getBusiness());
            }
            if (delivery.getCustomer() != null && delivery.getCustomer().getId() == null) {
                customerService.save(delivery.getCustomer());
            }
            if (delivery.getCourier() != null && delivery.getCourier().getId() == null) {
                courierService.save(delivery.getCourier());
            }
            deliveryService.save(delivery);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            logger.error("Erro ao criar delivery", e);
            return Response.serverError().entity("Erro ao criar delivery").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Delivery delivery) {
        try {
            // Persistir entidades associadas se necessário
            if (delivery.getBusiness() != null && delivery.getBusiness().getId() == null) {
                userService.save(delivery.getBusiness());
            }
            if (delivery.getCustomer() != null && delivery.getCustomer().getId() == null) {
                customerService.save(delivery.getCustomer());
            }
            if (delivery.getCourier() != null && delivery.getCourier().getId() == null) {
                courierService.save(delivery.getCourier());
            }
            delivery.setId(id);
            deliveryService.update(delivery);
            return Response.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao atualizar delivery id: " + id, e);
            return Response.serverError().entity("Erro ao atualizar delivery").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            deliveryService.delete(id);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Erro ao deletar delivery id: " + id, e);
            // Se for violação de integridade, retorna 409 e mensagem JSON
            return Response.status(jakarta.ws.rs.core.Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar delivery id: " + id, e);
            return Response.serverError().entity("{\"error\": \"Erro ao deletar delivery\"}").type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).build();
        }
    }
}

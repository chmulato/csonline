package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.caracore.cso.service.DeliveryService;
import com.caracore.cso.entity.Delivery;
import java.util.List;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/deliveries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeliveryController {
    private static final Logger logger = LogManager.getLogger(DeliveryController.class);

    @Inject
    DeliveryService deliveryService;

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
        } catch (Exception e) {
            logger.error("Erro ao deletar delivery id: " + id, e);
            return Response.serverError().entity("Erro ao deletar delivery").build();
        }
    }
}

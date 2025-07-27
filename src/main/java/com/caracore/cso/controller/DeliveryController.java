package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.caracore.cso.service.DeliveryService;
import com.caracore.cso.entity.Delivery;
import java.util.List;
import jakarta.inject.Inject;

@Path("/deliveries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeliveryController {
    @Inject
    DeliveryService deliveryService;

    @GET
    public List<Delivery> getAll() {
        return deliveryService.findAll();
    }

    @GET
    @Path("/{id}")
    public Delivery getById(@PathParam("id") Long id) {
        return deliveryService.findById(id);
    }

    @POST
    public Response create(Delivery delivery) {
        deliveryService.save(delivery);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Delivery delivery) {
        delivery.setId(id);
        deliveryService.update(delivery);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        deliveryService.delete(id);
        return Response.noContent().build();
    }
}

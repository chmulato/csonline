package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.caracore.cso.service.CourierService;
import com.caracore.cso.entity.Courier;
import java.util.List;
import jakarta.inject.Inject;

@Path("/couriers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourierController {
    @Inject
    CourierService courierService;

    @GET
    public List<Courier> getAll() {
        return courierService.findAll();
    }

    @GET
    @Path("/{id}")
    public Courier getById(@PathParam("id") Long id) {
        return courierService.findById(id);
    }

    @POST
    public Response create(Courier courier) {
        courierService.save(courier);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Courier courier) {
        courier.setId(id);
        courierService.update(courier);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        courierService.delete(id);
        return Response.noContent().build();
    }
}

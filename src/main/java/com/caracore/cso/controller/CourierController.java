package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.caracore.cso.service.CourierService;
import com.caracore.cso.entity.Courier;
import java.util.List;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/couriers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourierController {
    private static final Logger logger = LogManager.getLogger(CourierController.class);

    @Inject
    CourierService courierService;

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
            courierService.save(courier);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            logger.error("Erro ao criar courier", e);
            return Response.serverError().entity("Erro ao criar courier").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Courier courier) {
        try {
            courier.setId(id);
            courierService.update(courier);
            return Response.ok().build();
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
        } catch (Exception e) {
            logger.error("Erro ao deletar courier id: " + id, e);
            return Response.serverError().entity("Erro ao deletar courier").build();
        }
    }
}

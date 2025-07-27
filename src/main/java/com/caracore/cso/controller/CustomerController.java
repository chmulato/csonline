package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.caracore.cso.service.CustomerService;
import com.caracore.cso.entity.Customer;
import java.util.List;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerController {
    private static final Logger logger = LogManager.getLogger(CustomerController.class);

    @Inject
    CustomerService customerService;

    @GET
    public List<Customer> getAll() {
        try {
            return customerService.findAll();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os customers", e);
            throw e;
        }
    }

    @GET
    @Path("/{id}")
    public Customer getById(@PathParam("id") Long id) {
        try {
            return customerService.findById(id);
        } catch (Exception e) {
            logger.error("Erro ao buscar customer por id: " + id, e);
            throw e;
        }
    }

    @POST
    public Response create(Customer customer) {
        try {
            customerService.save(customer);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            logger.error("Erro ao criar customer", e);
            return Response.serverError().entity("Erro ao criar customer").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Customer customer) {
        try {
            customer.setId(id);
            customerService.update(customer);
            return Response.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao atualizar customer id: " + id, e);
            return Response.serverError().entity("Erro ao atualizar customer").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            customerService.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            logger.error("Erro ao deletar customer id: " + id, e);
            return Response.serverError().entity("Erro ao deletar customer").build();
        }
    }
}

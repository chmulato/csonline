package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.caracore.cso.service.CustomerService;
import com.caracore.cso.entity.Customer;
import com.caracore.cso.service.UserService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerController {
    private static final Logger logger = LogManager.getLogger(CustomerController.class);

    // Troque a injeção por instanciamento direto para funcionar nos testes sem CDI
    private CustomerService customerService = new CustomerService();
    private UserService userService = new UserService();

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
    public Response getById(@PathParam("id") Long id) {
        try {
            Customer customer = customerService.findById(id);
            if (customer != null) {
                return Response.ok(customer).build();
            } else {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar customer por id: " + id, e);
            return Response.serverError().entity("Erro ao buscar customer").build();
        }
    }

    @POST
    public Response create(Customer customer) {
        try {
            // Persistir usuários associados se necessário
            if (customer.getBusiness() != null && customer.getBusiness().getId() == null) {
                userService.save(customer.getBusiness());
            }
            if (customer.getUser() != null && customer.getUser().getId() == null) {
                userService.save(customer.getUser());
            }
            customerService.save(customer);
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Violação de unicidade ao criar customer: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            logger.error("Erro ao criar customer", e);
            return Response.serverError().entity("Erro ao criar customer").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Customer customer) {
        try {
            // Persistir usuários associados se necessário
            if (customer.getBusiness() != null && customer.getBusiness().getId() == null) {
                userService.save(customer.getBusiness());
            }
            if (customer.getUser() != null && customer.getUser().getId() == null) {
                userService.save(customer.getUser());
            }
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
        } catch (RuntimeException e) {
            logger.error("Erro ao deletar customer id: " + id, e);
            // Se for violação de integridade, retorna 409 e mensagem JSON
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar customer id: " + id, e);
            return Response.serverError().entity("{\"error\": \"Erro ao deletar customer\"}").type(MediaType.APPLICATION_JSON).build();
        }
    }
}


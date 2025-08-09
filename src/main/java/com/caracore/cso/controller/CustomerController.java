package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.RolesAllowed;
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
    @RolesAllowed({"ADMIN", "BUSINESS"})
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
    @RolesAllowed({"ADMIN", "BUSINESS", "CUSTOMER"})
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
    @RolesAllowed({"ADMIN", "BUSINESS"})
    public Response create(Customer customer) {
        try {
            // Log para debug
            logger.info("Criando customer com usuário: " + (customer.getUser() != null ? 
                        "login=" + customer.getUser().getLogin() + ", email=" + customer.getUser().getEmail() : "null"));
            
            // Verificar duplicidade de usuário
            if (customer.getUser() != null) {
                // Verificar se login já existe - independente se o ID está definido ou não
                if (customer.getUser().getLogin() != null) {
                    logger.info("Verificando duplicidade de login: " + customer.getUser().getLogin());
                    com.caracore.cso.entity.User existingUser = userService.findByLogin(customer.getUser().getLogin());
                    if (existingUser != null) {
                        logger.info("Login já existe: " + customer.getUser().getLogin());
                        return Response.status(Response.Status.CONFLICT)
                               .entity("{\"error\": \"Login já existe: " + customer.getUser().getLogin() + "\"}")
                               .type(MediaType.APPLICATION_JSON)
                               .build();
                    }
                }
                
                // Verificar se email já existe - independente se o ID está definido ou não
                if (customer.getUser().getEmail() != null && !customer.getUser().getEmail().isEmpty()) {
                    logger.info("Verificando duplicidade de email: " + customer.getUser().getEmail());
                    com.caracore.cso.entity.User existingUser = userService.findByEmail(customer.getUser().getEmail());
                    if (existingUser != null) {
                        logger.info("Email já existe: " + customer.getUser().getEmail());
                        return Response.status(Response.Status.CONFLICT)
                               .entity("{\"error\": \"Email já existe: " + customer.getUser().getEmail() + "\"}")
                               .type(MediaType.APPLICATION_JSON)
                               .build();
                    }
                }
            }
            
            // Obter usuários pelos IDs
            Long businessId = customer.getBusinessId();
            Long userId = customer.getUserId();
            
            if (businessId != null) {
                customer.setBusiness(userService.findById(businessId));
            }
            
            if (userId != null) {
                customer.setUser(userService.findById(userId));
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
    @RolesAllowed({"ADMIN", "BUSINESS"})
    public Response update(@PathParam("id") Long id, Customer customer) {
        try {
            // Buscar o customer existente
            Customer existingCustomer = customerService.findById(id);
            if (existingCustomer == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Customer não encontrado\"}").build();
            }
            
            // Atualizar apenas os campos fornecidos
            if (customer.getFactorCustomer() != null) {
                existingCustomer.setFactorCustomer(customer.getFactorCustomer());
            }
            
            if (customer.getPriceTable() != null) {
                existingCustomer.setPriceTable(customer.getPriceTable());
            }
            
            // Atualizar referências se IDs foram fornecidos
            Long businessId = customer.getBusinessId();
            Long userId = customer.getUserId();
            
            if (businessId != null) {
                existingCustomer.setBusiness(userService.findById(businessId));
            }
            
            if (userId != null) {
                existingCustomer.setUser(userService.findById(userId));
            }
            
            customerService.update(existingCustomer);
            return Response.ok(existingCustomer).build();
        } catch (Exception e) {
            logger.error("Erro ao atualizar customer id: " + id, e);
            return Response.serverError().entity("Erro ao atualizar customer").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "BUSINESS"})
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


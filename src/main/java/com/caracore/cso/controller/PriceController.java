package com.caracore.cso.controller;

import com.caracore.cso.entity.Price;
import com.caracore.cso.service.PriceService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/prices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PriceController {
    private static final Logger logger = LogManager.getLogger(PriceController.class);

    private PriceService priceService;

    public PriceController() {
        this.priceService = new PriceService();
    }

    // Construtor para injeção manual em testes
    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GET
    @RolesAllowed({"ADMIN", "BUSINESS"})
    public List<Price> getAll() {
        try {
            logger.info("Buscando todos os preços");
            List<Price> prices = priceService.findAll();
            logger.info("Encontrados {} preços", prices.size());
            return prices;
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os preços", e);
            throw e;
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "BUSINESS"})
    public Response getById(@PathParam("id") Long id) {
        try {
            logger.info("Buscando preço por id: {}", id);
            Price price = priceService.findById(id);
            
            if (price == null) {
                logger.warn("Preço não encontrado para id: {}", id);
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Preço não encontrado\"}")
                    .build();
            }
            
            logger.info("Preço encontrado: {}", price.getId());
            return Response.ok(price).build();
        } catch (Exception e) {
            logger.error("Erro ao buscar preço por id: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\":\"Erro interno do servidor\"}")
                .build();
        }
    }

    @POST
    @RolesAllowed({"ADMIN", "BUSINESS"})
    public Response create(Price price) {
        try {
            logger.info("Criando novo preço para tabela: {}", price.getTableName());
            
            // Validações básicas
            if (price.getTableName() == null || price.getTableName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Nome da tabela é obrigatório\"}")
                    .build();
            }
            
            if (price.getPrice() == null || price.getPrice() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Preço deve ser maior que zero\"}")
                    .build();
            }
            
            if (price.getCustomer() == null || price.getCustomer().getId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Cliente é obrigatório\"}")
                    .build();
            }
            
            if (price.getBusiness() == null || price.getBusiness().getId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Empresa é obrigatória\"}")
                    .build();
            }
            
            priceService.save(price);
            logger.info("Preço criado com sucesso. ID: {}", price.getId());
            
            return Response.status(Response.Status.CREATED).entity(price).build();
        } catch (Exception e) {
            logger.error("Erro ao criar preço", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\":\"Erro interno do servidor\"}")
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "BUSINESS"})
    public Response update(@PathParam("id") Long id, Price price) {
        try {
            logger.info("Atualizando preço id: {}", id);
            
            // Verificar se o preço existe
            Price existingPrice = priceService.findById(id);
            if (existingPrice == null) {
                logger.warn("Preço não encontrado para atualização. ID: {}", id);
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Preço não encontrado\"}")
                    .build();
            }
            
            // Validações básicas
            if (price.getTableName() == null || price.getTableName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Nome da tabela é obrigatório\"}")
                    .build();
            }
            
            if (price.getPrice() == null || price.getPrice() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Preço deve ser maior que zero\"}")
                    .build();
            }
            
            // Atualizar os campos
            price.setId(id);
            priceService.save(price);
            
            logger.info("Preço atualizado com sucesso. ID: {}", id);
            return Response.ok(price).build();
        } catch (Exception e) {
            logger.error("Erro ao atualizar preço id: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\":\"Erro interno do servidor\"}")
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "BUSINESS"})
    public Response delete(@PathParam("id") Long id) {
        try {
            logger.info("Deletando preço id: {}", id);
            
            // Verificar se o preço existe
            Price existingPrice = priceService.findById(id);
            if (existingPrice == null) {
                logger.warn("Preço não encontrado para deleção. ID: {}", id);
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"Preço não encontrado\"}")
                    .build();
            }
            
            priceService.deleteById(id);
            logger.info("Preço deletado com sucesso. ID: {}", id);
            
            return Response.noContent().build();
        } catch (com.caracore.cso.exception.ReferentialIntegrityException e) {
            logger.warn("Tentativa de deletar preço com referências: {}", id);
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"message\":\"" + e.getMessage() + "\"}")
                .build();
        } catch (Exception e) {
            logger.error("Erro ao deletar preço id: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\":\"Erro interno do servidor\"}")
                .build();
        }
    }

    @GET
    @Path("/business/{businessId}")
    @RolesAllowed({"ADMIN", "BUSINESS"})
    public Response getByBusinessId(@PathParam("businessId") Long businessId) {
        try {
            logger.info("Buscando preços por businessId: {}", businessId);
            List<Price> prices = priceService.findAllByBusiness(businessId);
            logger.info("Encontrados {} preços para businessId: {}", prices.size(), businessId);
            return Response.ok(prices).build();
        } catch (Exception e) {
            logger.error("Erro ao buscar preços por businessId: " + businessId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\":\"Erro interno do servidor\"}")
                .build();
        }
    }
}

package com.caracore.cso.controller;

import com.caracore.cso.entity.Delivery;
import com.caracore.cso.dto.DeliveryDTO;
import com.caracore.cso.service.DeliveryService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/deliveries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeliveryController {
    private static final Logger logger = LogManager.getLogger(DeliveryController.class);

    private DeliveryService deliveryService = new DeliveryService();

    @GET
    public Response getAll() {
        try {
            List<Delivery> deliveries = deliveryService.findAll();
            
            // Se não há deliveries, retorna lista vazia
            if (deliveries == null || deliveries.isEmpty()) {
                return Response.ok(new java.util.ArrayList<>()).build();
            }
            
            // Converte para DTO para evitar problemas de serialização
            List<DeliveryDTO> deliveryDTOs = deliveries.stream()
                .map(delivery -> convertToDTO(delivery))
                .collect(Collectors.toList());
            
            return Response.ok(deliveryDTOs).build();
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as deliveries", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro interno do servidor: " + e.getMessage())
                .build();
        }
    }
    
    private DeliveryDTO convertToDTO(Delivery delivery) {
        return new DeliveryDTO(
            delivery.getId(),
            delivery.getBusiness() != null ? delivery.getBusiness().getId() : null,
            delivery.getCustomer() != null ? delivery.getCustomer().getId() : null,
            delivery.getCourier() != null ? delivery.getCourier().getId() : null,
            delivery.getStart(),
            delivery.getDestination(),
            delivery.getContact(),
            delivery.getDescription(),
            delivery.getVolume(),
            delivery.getWeight(),
            delivery.getKm(),
            delivery.getAdditionalCost(),
            delivery.getCost(),
            delivery.getReceived(),
            delivery.getCompleted()
        );
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        try {
            Delivery delivery = deliveryService.findById(id);
            if (delivery == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(convertToDTO(delivery)).build();
        } catch (Exception e) {
            logger.error("Erro ao buscar delivery por id: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro interno do servidor")
                .build();
        }
    }

    @POST
    public Response create(Delivery delivery) {
        try {
            // Verificar se temos IDs para buscar as entidades relacionadas
            if (delivery.getBusinessId() != null || delivery.getCustomerId() != null || delivery.getCourierId() != null) {
                com.caracore.cso.service.UserService userService = new com.caracore.cso.service.UserService();
                com.caracore.cso.service.CustomerService customerService = new com.caracore.cso.service.CustomerService();
                com.caracore.cso.service.CourierService courierService = new com.caracore.cso.service.CourierService();
                
                // Obter as entidades pelos IDs
                if (delivery.getBusinessId() != null) {
                    delivery.setBusiness(userService.findById(delivery.getBusinessId()));
                }
                
                if (delivery.getCustomerId() != null) {
                    delivery.setCustomer(customerService.findById(delivery.getCustomerId()));
                }
                
                if (delivery.getCourierId() != null) {
                    delivery.setCourier(courierService.findById(delivery.getCourierId()));
                }
            }
            
            deliveryService.save(delivery);
            return Response.status(Response.Status.CREATED).entity(convertToDTO(delivery)).build();
        } catch (Exception e) {
            logger.error("Erro ao criar delivery", e);
            return Response.serverError().entity("Erro ao criar delivery").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Delivery delivery) {
        try {
            // Buscar a entrega existente
            Delivery existingDelivery = deliveryService.findById(id);
            if (existingDelivery == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Delivery não encontrada\"}").build();
            }
            
            // Atualizar campos simples se fornecidos
            if (delivery.getStart() != null) existingDelivery.setStart(delivery.getStart());
            if (delivery.getDestination() != null) existingDelivery.setDestination(delivery.getDestination());
            if (delivery.getContact() != null) existingDelivery.setContact(delivery.getContact());
            if (delivery.getDescription() != null) existingDelivery.setDescription(delivery.getDescription());
            if (delivery.getVolume() != null) existingDelivery.setVolume(delivery.getVolume());
            if (delivery.getWeight() != null) existingDelivery.setWeight(delivery.getWeight());
            if (delivery.getKm() != null) existingDelivery.setKm(delivery.getKm());
            if (delivery.getAdditionalCost() != null) existingDelivery.setAdditionalCost(delivery.getAdditionalCost());
            if (delivery.getCost() != null) existingDelivery.setCost(delivery.getCost());
            if (delivery.getReceived() != null) existingDelivery.setReceived(delivery.getReceived());
            if (delivery.getCompleted() != null) existingDelivery.setCompleted(delivery.getCompleted());
            
            // Verificar se temos IDs para buscar as entidades relacionadas
            if (delivery.getBusinessId() != null || delivery.getCustomerId() != null || delivery.getCourierId() != null) {
                com.caracore.cso.service.UserService userService = new com.caracore.cso.service.UserService();
                com.caracore.cso.service.CustomerService customerService = new com.caracore.cso.service.CustomerService();
                com.caracore.cso.service.CourierService courierService = new com.caracore.cso.service.CourierService();
                
                // Obter as entidades pelos IDs
                if (delivery.getBusinessId() != null) {
                    existingDelivery.setBusiness(userService.findById(delivery.getBusinessId()));
                }
                
                if (delivery.getCustomerId() != null) {
                    existingDelivery.setCustomer(customerService.findById(delivery.getCustomerId()));
                }
                
                if (delivery.getCourierId() != null) {
                    existingDelivery.setCourier(courierService.findById(delivery.getCourierId()));
                }
            }
            
            deliveryService.update(existingDelivery);
            return Response.ok(convertToDTO(existingDelivery)).build();
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

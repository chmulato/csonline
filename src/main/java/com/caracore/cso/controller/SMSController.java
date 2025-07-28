package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.caracore.cso.service.SMSService;
import com.caracore.cso.entity.SMS;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/sms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SMSController {
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            smsService.deleteById(id);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Erro ao deletar SMS id: " + id, e);
            // Se for violação de integridade, retorna 409 e mensagem JSON
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar SMS id: " + id, e);
            return Response.serverError().entity("{\"error\": \"Erro ao deletar SMS\"}").type(MediaType.APPLICATION_JSON).build();
        }
    }
    private static final Logger logger = LogManager.getLogger(SMSController.class);
    private SMSService smsService;

    public SMSController() {
        this.smsService = new SMSService();
    }

    // Construtor para injeção manual em testes
    public SMSController(SMSService smsService) {
        this.smsService = smsService;
    }

    @GET
    public List<SMS> getAll() {
        try {
            return smsService.findAll();
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as SMS", e);
            throw e;
        }
    }

    @GET
    @Path("/{id}")
    public SMS getById(@PathParam("id") Long id) {
        try {
            return smsService.findById(id);
        } catch (Exception e) {
            logger.error("Erro ao buscar SMS por id: " + id, e);
            throw e;
        }
    }

    @GET
    @Path("/delivery/{deliveryId}")
    public List<SMS> getByDelivery(@PathParam("deliveryId") Long deliveryId) {
        try {
            return smsService.getDeliverySMSHistory(deliveryId);
        } catch (Exception e) {
            logger.error("Erro ao buscar SMS por deliveryId: " + deliveryId, e);
            throw e;
        }
    }

    @POST
    public Response create(SMS sms) {
        try {
            smsService.save(sms);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            logger.error("Erro ao criar SMS", e);
            return Response.serverError().entity("Erro ao criar SMS").build();
        }
    }
}

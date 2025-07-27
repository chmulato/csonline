package com.caracore.cso.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.caracore.cso.service.SMSService;
import com.caracore.cso.entity.SMS;
import java.util.List;
import jakarta.inject.Inject;

@Path("/sms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SMSController {
    @Inject
    SMSService smsService;

    @GET
    public List<SMS> getAll() {
        return smsService.findAll();
    }

    @GET
    @Path("/{id}")
    public SMS getById(@PathParam("id") Long id) {
        return smsService.findById(id);
    }

    @GET
    @Path("/delivery/{deliveryId}")
    public List<SMS> getByDelivery(@PathParam("deliveryId") Long deliveryId) {
        return smsService.getDeliverySMSHistory(deliveryId);
    }

    @POST
    public Response create(SMS sms) {
        smsService.save(sms);
        return Response.status(Response.Status.CREATED).build();
    }
}

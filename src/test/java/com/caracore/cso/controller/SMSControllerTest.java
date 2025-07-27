package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

public class SMSControllerTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new ResourceConfig(SMSController.class);
    }

    @Test
    public void testGetAllSMS() {
        Response response = target("/sms").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetSMSById() {
        Response response = target("/sms/1").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetSMSByDelivery() {
        Response response = target("/sms/delivery/1").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateSMS() {
        String json = "{\"message\":\"Teste WhatsApp\",\"mobileTo\":\"11999999999\",\"mobileFrom\":\"11888888888\"}";
        Response response = target("/sms").request().post(javax.ws.rs.client.Entity.json(json));
        assertEquals(201, response.getStatus());
    }
}

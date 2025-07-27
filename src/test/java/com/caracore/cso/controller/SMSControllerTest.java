package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SMSControllerTest extends JerseyTest {
    private static final Logger logger = LogManager.getLogger(SMSControllerTest.class);

    @Override
    protected Application configure() {
        return new ResourceConfig(SMSController.class);
    }

    @Test
    public void testGetAllSMS() {
        try {
            Response response = target("/sms").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetAllSMS", e);
            throw e;
        }
    }

    @Test
    public void testGetSMSById() {
        try {
            Response response = target("/sms/1").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetSMSById", e);
            throw e;
        }
    }

    @Test
    public void testGetSMSByDelivery() {
        try {
            Response response = target("/sms/delivery/1").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetSMSByDelivery", e);
            throw e;
        }
    }

    @Test
    public void testCreateSMS() {
        try {
            String json = "{\"message\":\"Teste WhatsApp\",\"mobileTo\":\"11999999999\",\"mobileFrom\":\"11888888888\"}";
            Response response = target("/sms").request().post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testCreateSMS", e);
            throw e;
        }
    }
}

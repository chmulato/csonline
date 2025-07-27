package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeliveryControllerTest extends JerseyTest {
    private static final Logger logger = LogManager.getLogger(DeliveryControllerTest.class);

    @Override
    protected Application configure() {
        return new ResourceConfig(DeliveryController.class)
            .register(com.caracore.cso.service.DeliveryService.class);
    }

    @Test
    public void testGetAllDeliveries() {
        try {
            Response response = target("/deliveries").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetAllDeliveries", e);
            throw e;
        }
    }

    @Test
    public void testGetDeliveryById() {
        try {
            Response response = target("/deliveries/1").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetDeliveryById", e);
            throw e;
        }
    }

    @Test
    public void testCreateDelivery() {
        try {
            String json = "{\"start\":\"Origem Teste\",\"destination\":\"Destino Teste\"}";
            Response response = target("/deliveries").request().post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testCreateDelivery", e);
            throw e;
        }
    }

    @Test
    public void testUpdateDelivery() {
        try {
            String json = "{\"start\":\"Origem Atualizada\",\"destination\":\"Destino Atualizado\"}";
            Response response = target("/deliveries/1").request().put(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testUpdateDelivery", e);
            throw e;
        }
    }

    @Test
    public void testDeleteDelivery() {
        try {
            Response response = target("/deliveries/1").request().delete();
            assertEquals(204, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testDeleteDelivery", e);
            throw e;
        }
    }
}

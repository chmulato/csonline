package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

public class DeliveryControllerTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new ResourceConfig(DeliveryController.class);
    }

    @Test
    public void testGetAllDeliveries() {
        Response response = target("/deliveries").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetDeliveryById() {
        Response response = target("/deliveries/1").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateDelivery() {
        String json = "{\"start\":\"Origem Teste\",\"destination\":\"Destino Teste\"}";
        Response response = target("/deliveries").request().post(javax.ws.rs.client.Entity.json(json));
        assertEquals(201, response.getStatus());
    }

    @Test
    public void testUpdateDelivery() {
        String json = "{\"start\":\"Origem Atualizada\",\"destination\":\"Destino Atualizado\"}";
        Response response = target("/deliveries/1").request().put(javax.ws.rs.client.Entity.json(json));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDeleteDelivery() {
        Response response = target("/deliveries/1").request().delete();
        assertEquals(204, response.getStatus());
    }
}

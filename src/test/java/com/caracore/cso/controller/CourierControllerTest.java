package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

public class CourierControllerTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new ResourceConfig(CourierController.class);
    }

    @Test
    public void testGetAllCouriers() {
        Response response = target("/couriers").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetCourierById() {
        Response response = target("/couriers/1").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateCourier() {
        String json = "{\"factorCourier\":1.5}";
        Response response = target("/couriers").request().post(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(201, response.getStatus());
    }

    @Test
    public void testUpdateCourier() {
        String json = "{\"factorCourier\":2.0}";
        Response response = target("/couriers/1").request().put(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDeleteCourier() {
        Response response = target("/couriers/1").request().delete();
        assertEquals(204, response.getStatus());
    }
}

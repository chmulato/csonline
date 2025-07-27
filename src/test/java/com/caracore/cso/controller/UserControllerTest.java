package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new ResourceConfig(UserController.class);
    }

    @Test
    public void testGetAllUsers() {
        Response response = target("/users").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetUserById() {
        Response response = target("/users/1").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateUser() {
        String json = "{\"login\":\"testuser\",\"password\":\"testpass\",\"role\":\"CUSTOMER\"}";
        Response response = target("/users").request().post(javax.ws.rs.client.Entity.json(json));
        assertEquals(201, response.getStatus());
    }

    @Test
    public void testUpdateUser() {
        String json = "{\"login\":\"updateduser\",\"password\":\"updatedpass\",\"role\":\"COURIER\"}";
        Response response = target("/users/1").request().put(javax.ws.rs.client.Entity.json(json));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDeleteUser() {
        Response response = target("/users/1").request().delete();
        assertEquals(204, response.getStatus());
    }
}

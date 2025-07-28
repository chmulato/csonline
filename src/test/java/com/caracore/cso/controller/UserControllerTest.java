package com.caracore.cso.controller;


import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.caracore.cso.service.UserService;

public class UserControllerTest extends JerseyTest {
    private static final Logger logger = LogManager.getLogger(UserControllerTest.class);

    @Override
    protected Application configure() {
        UserService userService = new com.caracore.cso.service.UserService();
        return new ResourceConfig(UserController.class)
            .register(new org.glassfish.hk2.utilities.binding.AbstractBinder() {
                @Override
                protected void configure() {
                    bind(userService).to(com.caracore.cso.service.UserService.class);
                }
            })
            .register(com.caracore.cso.controller.CustomerController.class)
            .register(com.caracore.cso.service.CustomerService.class);
    }

    @Test
    public void testGetAllUsers() {
        try {
            Response response = target("/users").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetAllUsers", e);
            throw e;
        }
    }

    @Test
    public void testGetUserById() {
        try {
            Response response = target("/users/1").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetUserById", e);
            throw e;
        }
    }

    @Test
    public void testCreateUser() {
        try {
            String json = "{\"login\":\"testuser\",\"password\":\"testpass\",\"role\":\"CUSTOMER\",\"name\":\"Test User\"}";
            Response response = target("/users").request().post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testCreateUser", e);
            throw e;
        }
    }

    @Test
    public void testUpdateUser() {
        try {
            String json = "{\"login\":\"updateduser\",\"password\":\"updatedpass\",\"role\":\"COURIER\",\"name\":\"Updated User\"}";
            Response response = target("/users/1").request().put(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testUpdateUser", e);
            throw e;
        }
    }

}

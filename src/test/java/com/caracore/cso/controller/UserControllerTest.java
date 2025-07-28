package com.caracore.cso.controller;


import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;

import com.caracore.cso.service.UserService;
import com.caracore.cso.util.TestDatabaseUtil;
import com.caracore.cso.repository.JPAUtil;

public class UserControllerTest extends JerseyTest {
    private static final Logger logger = LogManager.getLogger(UserControllerTest.class);

    @Override
    public void setUp() throws Exception {
        var em = JPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
        super.setUp();
    }

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
            // Cria usuário único
            String login = "user_getall_" + System.currentTimeMillis();
            String json = String.format("{\"login\":\"%s\",\"password\":\"pass\",\"role\":\"CUSTOMER\",\"name\":\"User GetAll\"}", login);
            target("/users").request().post(jakarta.ws.rs.client.Entity.json(json));
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
            // Cria usuário único
            String login = "user_getbyid_" + System.currentTimeMillis();
            String json = String.format("{\"login\":\"%s\",\"password\":\"pass\",\"role\":\"CUSTOMER\",\"name\":\"User GetById\"}", login);
            Response createResp = target("/users").request().post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, createResp.getStatus());
            // Recupera todos e pega o último ID
            Response allResp = target("/users").request().get();
            ArrayList<?> users = allResp.readEntity(ArrayList.class);
            int lastId = users.size();
            Response response = target("/users/" + lastId).request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetUserById", e);
            throw e;
        }
    }

    @Test
    public void testCreateUser() {
        try {
            String login = "testuser_" + System.currentTimeMillis();
            String json = String.format("{\"login\":\"%s\",\"password\":\"testpass\",\"role\":\"CUSTOMER\",\"name\":\"Test User\"}", login);
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
            // Cria usuário único
            String login = "user_update_" + System.currentTimeMillis();
            String json = String.format("{\"login\":\"%s\",\"password\":\"pass\",\"role\":\"CUSTOMER\",\"name\":\"User Update\"}", login);
            Response createResp = target("/users").request().post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, createResp.getStatus());
            // Recupera todos e pega o último ID
            Response allResp = target("/users").request().get();
            ArrayList<?> users = allResp.readEntity(ArrayList.class);
            int lastId = users.size();
            String updateJson = String.format("{\"login\":\"updateduser_%d\",\"password\":\"updatedpass\",\"role\":\"COURIER\",\"name\":\"Updated User\"}", lastId);
            Response response = target("/users/" + lastId).request().put(jakarta.ws.rs.client.Entity.json(updateJson));
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testUpdateUser", e);
            throw e;
        }
    }

}

    private String createUserJson(String prefix) {
        String login = prefix + System.currentTimeMillis();
        return String.format("{\"login\":\"%s\",\"password\":\"pass\",\"role\":\"CUSTOMER\",\"name\":\"%s\"}", login, prefix + " Name");
    }
package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Map;
import jakarta.ws.rs.core.GenericType;

import com.caracore.cso.service.UserService;
import com.caracore.cso.util.TestDatabaseUtil;
import com.caracore.cso.repository.JPAUtil;

public class UserControllerTest extends JerseyTest {
    private static final Logger logger = LogManager.getLogger(UserControllerTest.class);

    private String createUserJson(String prefix) {
        String login = prefix + System.currentTimeMillis();
        return String.format("{\"login\":\"%s\",\"password\":\"pass\",\"role\":\"CUSTOMER\",\"name\":\"%s\"}", login, prefix + " Name");
    }

    @BeforeEach
    public void cleanDatabase() {
        var em = JPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
    }

    @Override
    protected Application configure() {
        UserService userService = new com.caracore.cso.service.UserService();
        UserController userController = new UserController(userService);
        return new ResourceConfig()
            .register(userController)
            .register(com.caracore.cso.controller.CustomerController.class)
            .register(com.caracore.cso.service.CustomerService.class);
    }

    @Test
    public void testGetAllUsers() {
        try {
            String json = createUserJson("user_getall_");
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
            String json = createUserJson("user_getbyid_");
            Response createResp = target("/users").request().post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, createResp.getStatus());
            // Recupera todos e pega o último ID
            Response allResp = target("/users").request().get();
            List<Map<String, Object>> users = allResp.readEntity(new GenericType<List<Map<String, Object>>>() {});
            int lastId = ((Number) users.get(users.size() - 1).get("id")).intValue();
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
            String json = createUserJson("testuser_").replace("pass", "testpass").replace("Name", "Test User");
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
            String json = createUserJson("user_update_");
            Response createResp = target("/users").request().post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, createResp.getStatus());
            // Recupera todos e pega o último ID
            Response allResp = target("/users").request().get();
            List<Map<String, Object>> users = allResp.readEntity(new GenericType<List<Map<String, Object>>>() {});
            int lastId = ((Number) users.get(users.size() - 1).get("id")).intValue();
            String updateJson = String.format("{\"login\":\"updateduser_%d\",\"password\":\"updatedpass\",\"role\":\"COURIER\",\"name\":\"Updated User\"}", lastId);
            Response response = target("/users/" + lastId).request().put(jakarta.ws.rs.client.Entity.json(updateJson));
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testUpdateUser", e);
            throw e;
        }
    }
}
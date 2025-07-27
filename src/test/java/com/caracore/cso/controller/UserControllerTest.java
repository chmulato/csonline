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
            });
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

    @Test
    public void testDeleteUser() {
        try {
            // Cria um usuário vinculado a um cliente para simular integridade referencial
            String userJson = "{\"login\":\"refuser\",\"password\":\"refpass\",\"role\":\"BUSINESS\",\"name\":\"Ref User\"}";
            Response createResponse = target("/users").request().post(jakarta.ws.rs.client.Entity.json(userJson));
            assertEquals(201, createResponse.getStatus());

            String location = createResponse.getHeaderString("Location");
            assertNotNull(location);
            String idStr = location.substring(location.lastIndexOf("/") + 1);
            Long userId = Long.parseLong(idStr);

            // Cria um cliente vinculado ao usuário
            String customerJson = "{\"business\":{\"id\":" + userId + "},\"factorCustomer\":1.0,\"priceTable\":\"A\"}";
            Response customerResponse = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customerJson));
            assertEquals(201, customerResponse.getStatus());

            // Tenta deletar o usuário vinculado ao cliente
            Response deleteResponse = target("/users/" + userId).request().delete();
            assertEquals(409, deleteResponse.getStatus());
            String errorJson = deleteResponse.readEntity(String.class);
            assertTrue(errorJson.contains("Não foi possível deletar o usuário"));
        } catch (Exception e) {
            logger.error("Erro em testDeleteUser", e);
            throw e;
        }
    }
}

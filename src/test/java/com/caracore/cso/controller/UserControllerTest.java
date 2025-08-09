
package com.caracore.cso.controller;

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

import com.caracore.cso.entity.User;
import com.caracore.cso.service.UserService;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.security.AuthorizationFilter;
import com.caracore.cso.security.JwtAuthenticationFilter;
import com.caracore.cso.util.JwtUtil;

public class UserControllerTest extends BaseControllerJerseyTest {
    private static final Logger logger = LogManager.getLogger(UserControllerTest.class);


    private String createUserJson(String role) {
        User user = TestDataFactory.createUser(role);
        return String.format("{\"login\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"name\":\"%s\"}",
                user.getLogin(), user.getPassword(), user.getRole(), user.getName());
    }

    @BeforeEach
    public void setup() { /* limpeza feita na base */ }

    @Override
    protected Application configure() {
        UserService userService = new com.caracore.cso.service.UserService();
        UserController userController = new UserController(userService);
        return new ResourceConfig()
            .register(userController)
            .register(JwtAuthenticationFilter.class)  // Registra filtro de autenticação
            .register(AuthorizationFilter.class)      // Registra filtro de autorização
            .register(com.caracore.cso.controller.CustomerController.class)
            .register(com.caracore.cso.service.CustomerService.class);
    }

    @Test
    public void testGetAllUsers() {
        try {
            String json = createUserJson("CUSTOMER");
            String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
            target("/users").request()
                .header("Authorization", "Bearer " + adminToken)
                .post(jakarta.ws.rs.client.Entity.json(json));
            Response response = target("/users").request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetAllUsers", e);
            throw e;
        }
    }

    @Test
    public void testGetUserById() {
        try {
            String json = createUserJson("CUSTOMER");
            String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
            Response createResp = target("/users").request()
                .header("Authorization", "Bearer " + adminToken)
                .post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, createResp.getStatus());
            // Recupera todos e pega o último ID
            Response allResp = target("/users").request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
            List<Map<String, Object>> users = allResp.readEntity(new GenericType<List<Map<String, Object>>>() {});
            int lastId = ((Number) users.get(users.size() - 1).get("id")).intValue();
            Response response = target("/users/" + lastId).request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetUserById", e);
            throw e;
        }
    }

    @Test
    public void testCreateUser() {
        try {
            String json = createUserJson("CUSTOMER");
            String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
            Response response = target("/users").request()
                .header("Authorization", "Bearer " + adminToken)
                .post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testCreateUser", e);
            throw e;
        }
    }

    @Test
    public void testUpdateUser() {
        try {
            String json = createUserJson("CUSTOMER");
            String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
            Response createResp = target("/users").request()
                .header("Authorization", "Bearer " + adminToken)
                .post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, createResp.getStatus());
            // Recupera todos e pega o último ID
            Response allResp = target("/users").request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
            List<Map<String, Object>> users = allResp.readEntity(new GenericType<List<Map<String, Object>>>() {});
            int lastId = ((Number) users.get(users.size() - 1).get("id")).intValue();
            String updateJson = String.format("{\"login\":\"updateduser_%d\",\"password\":\"updatedpass\",\"role\":\"COURIER\",\"name\":\"Updated User\"}", lastId);
            Response response = target("/users/" + lastId).request()
                .header("Authorization", "Bearer " + adminToken)
                .put(jakarta.ws.rs.client.Entity.json(updateJson));
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testUpdateUser", e);
            throw e;
        }
    }

    // ========== TESTES DE AUTORIZAÇÃO ==========

    @Test
    public void testGetAllUsersWithoutToken() {
        Response response = target("/users").request().get();
        assertEquals(401, response.getStatus(), "Acesso sem token deve retornar 401 Unauthorized");
    }

    @Test
    public void testGetAllUsersWithAdminToken() {
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response response = target("/users")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        assertEquals(200, response.getStatus(), "ADMIN deve conseguir listar users");
    }

    @Test
    public void testGetAllUsersWithBusinessToken() {
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response response = target("/users")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .get();
        assertEquals(200, response.getStatus(), "BUSINESS deve conseguir listar users");
    }

    @Test
    public void testGetAllUsersWithCourierToken() {
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/users")
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .get();
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir listar users (só ADMIN/BUSINESS)");
    }

    @Test
    public void testCreateUserWithBusinessToken() {
    String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        String json = createUserJson("CUSTOMER");
        Response response = target("/users")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(json));
    assertEquals(403, response.getStatus(), "Apenas ADMIN pode criar users");
    }

    @Test
    public void testCreateUserWithCourierToken() {
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        String json = createUserJson("CUSTOMER");
        Response response = target("/users")
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .post(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir criar users (só ADMIN/BUSINESS)");
    }

    @Test
    public void testCreateUserOnlyAdminAllowed() {
        // Apenas ADMIN pode criar outros ADMINs
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        String json = createUserJson("ADMIN");
        Response response = target("/users")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(403, response.getStatus(), "Apenas ADMIN pode criar outros ADMINs");
    }

    @Test
    public void testUpdateUserOnlyAdminAllowed() {
        // Primeiro cria um user como ADMIN
    String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        String json = createUserJson("CUSTOMER");
        Response createResp = target("/users")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(json));
    assertEquals(201, createResp.getStatus());
        
        // Recupera todos para pegar o ID
        Response allResp = target("/users")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        List<Map<String, Object>> users = allResp.readEntity(new GenericType<List<Map<String, Object>>>() {});
        int lastId = ((Number) users.get(users.size() - 1).get("id")).intValue();
        
        // Tenta atualizar como BUSINESS - não deve conseguir
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        String updateJson = String.format("{\"login\":\"updateduser_%d\",\"password\":\"updatedpass\",\"role\":\"COURIER\",\"name\":\"Updated User\"}", lastId);
        Response response = target("/users/" + lastId)
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .put(jakarta.ws.rs.client.Entity.json(updateJson));
        assertEquals(403, response.getStatus(), "Apenas ADMIN pode atualizar users");
    }

    @Test
    public void testDeleteUserOnlyAdminAllowed() {
        // Primeiro cria um user como ADMIN
    String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        String json = createUserJson("CUSTOMER");
        Response createResp = target("/users")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(json));
    assertEquals(201, createResp.getStatus());
        
        // Recupera todos para pegar o ID
        Response allResp = target("/users")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        List<Map<String, Object>> users = allResp.readEntity(new GenericType<List<Map<String, Object>>>() {});
        int lastId = ((Number) users.get(users.size() - 1).get("id")).intValue();
        
        // Tenta deletar como BUSINESS - não deve conseguir
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
    Response response = target("/users/" + lastId)
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .delete();
        assertEquals(403, response.getStatus(), "Apenas ADMIN pode deletar users");
    }
}


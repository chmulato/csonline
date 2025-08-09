package com.caracore.cso.controller;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Courier;
import com.caracore.cso.service.UserServiceTestHelper;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.security.AuthorizationFilter;
import com.caracore.cso.security.JwtAuthenticationFilter;
import com.caracore.cso.util.JwtUtil;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import com.caracore.cso.service.TestableCourierService;

public class TeamControllerTest extends BaseControllerJerseyTest {

    @BeforeEach
    void setup() { /* limpeza feita na base */ }

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .register(TeamController.class)
                .register(JwtAuthenticationFilter.class)  // Registra filtro de autenticação
                .register(AuthorizationFilter.class)      // Registra filtro de autorização
                .register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }

    @Test
    void testCreateAndGetById() throws Exception {
        User business = TestDataFactory.createUser("BUSINESS");
        User courierUser = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(business);
        UserServiceTestHelper.persistUser(courierUser);
        
        // Cria um courier
        Courier courier = TestDataFactory.createCourier(business, courierUser);
        new TestableCourierService(true).save(courier);
        
        Team team = TestDataFactory.createTeam(business, courier);

    String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
    Response postResponse = target("/teams").request()
        .header("Authorization", "Bearer " + adminToken)
        .post(jakarta.ws.rs.client.Entity.json(team));
        String postJson = postResponse.readEntity(String.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postJson);
        Team created = new com.fasterxml.jackson.databind.ObjectMapper().readValue(postJson, Team.class);
        assertNotNull(created.getId());

    Response getResponse = target("/teams/" + created.getId()).request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        String getJson = getResponse.readEntity(String.class);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Team found = new com.fasterxml.jackson.databind.ObjectMapper().readValue(getJson, Team.class);
        assertEquals(created.getId(), found.getId());
        assertEquals(team.getFactorCourier(), found.getFactorCourier());
    }

    @Test
    void testGetAll() {
    String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
    Response response = target("/teams").request()
        .header("Authorization", "Bearer " + adminToken)
        .get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdate() throws Exception {
        User business = TestDataFactory.createUser("BUSINESS");
        User courierUser = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(business);
        UserServiceTestHelper.persistUser(courierUser);
        
        // Cria um courier
        Courier courier = TestDataFactory.createCourier(business, courierUser);
        new TestableCourierService(true).save(courier);
        
        Team team = TestDataFactory.createTeam(business, courier);
        team.setFactorCourier(2.0);
    String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
    Response postResponse = target("/teams").request()
        .header("Authorization", "Bearer " + adminToken)
        .post(jakarta.ws.rs.client.Entity.json(team));
        String postJson = postResponse.readEntity(String.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postJson);
        Team created = new com.fasterxml.jackson.databind.ObjectMapper().readValue(postJson, Team.class);
        created.setFactorCourier(3.0);
    Response putResponse = target("/teams/" + created.getId()).request()
            .header("Authorization", "Bearer " + adminToken)
            .put(jakarta.ws.rs.client.Entity.json(created));
        String putJson = putResponse.readEntity(String.class);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus(), putJson);
        Team updated = new com.fasterxml.jackson.databind.ObjectMapper().readValue(putJson, Team.class);
        assertEquals(3.0, updated.getFactorCourier());
    }

    @Test
    void testDelete() throws Exception {
        User business = TestDataFactory.createUser("BUSINESS");
        User courierUser = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(business);
        UserServiceTestHelper.persistUser(courierUser);
        
        // Cria um courier
        Courier courier = TestDataFactory.createCourier(business, courierUser);
        new TestableCourierService(true).save(courier);
        
        Team team = TestDataFactory.createTeam(business, courier);
        team.setFactorCourier(4.0);
    String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
    Response postResponse = target("/teams").request()
        .header("Authorization", "Bearer " + adminToken)
        .post(jakarta.ws.rs.client.Entity.json(team));
        String postJson = postResponse.readEntity(String.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postJson);
        Team created = new com.fasterxml.jackson.databind.ObjectMapper().readValue(postJson, Team.class);
    Response deleteResponse = target("/teams/" + created.getId()).request()
            .header("Authorization", "Bearer " + adminToken)
            .delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        Response getResponse = target("/teams/" + created.getId()).request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }

    @Test
    void testNaoPermiteDuplicidadeDeTeamPorBusinessECourier() throws Exception {
        User business2 = TestDataFactory.createUser("BUSINESS");
        User courierUser2 = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(business2);
        UserServiceTestHelper.persistUser(courierUser2);
        
        // Cria um courier
        Courier courier2 = TestDataFactory.createCourier(business2, courierUser2);
        new TestableCourierService(true).save(courier2);
        
        Team team1 = TestDataFactory.createTeam(business2, courier2);
        Team team2 = TestDataFactory.createTeam(business2, courier2);
        // Cria o primeiro normalmente
    String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
    Response resp1 = target("/teams").request()
        .header("Authorization", "Bearer " + adminToken)
        .post(jakarta.ws.rs.client.Entity.json(team1));
        String resp1Json = resp1.readEntity(String.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), resp1.getStatus(), resp1Json);
        // Tenta criar duplicado
    Response resp2 = target("/teams").request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(team2));
        String msg = resp2.readEntity(String.class).toLowerCase();
        assertEquals(Response.Status.CONFLICT.getStatusCode(), resp2.getStatus());
        assertTrue(msg.contains("team") || msg.contains("business") || msg.contains("courier") || msg.contains("existe"));
        User business = TestDataFactory.createUser("BUSINESS");
        User courierUser = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(business);
        UserServiceTestHelper.persistUser(courierUser);
        
        // Cria um courier
        Courier courier = TestDataFactory.createCourier(business, courierUser);
        new TestableCourierService(true).save(courier);
        
        Team team = TestDataFactory.createTeam(business, courier);
        team.setFactorCourier(4.0);
    Response postResponse = target("/teams").request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(team));
        String postJson = postResponse.readEntity(String.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postJson);
        Team created = new com.fasterxml.jackson.databind.ObjectMapper().readValue(postJson, Team.class);
    Response deleteResponse = target("/teams/" + created.getId()).request()
            .header("Authorization", "Bearer " + adminToken)
            .delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    Response getResponse = target("/teams/" + created.getId()).request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }

    // ========== TESTES DE AUTORIZAÇÃO ==========

    @Test
    public void testGetAllTeamsWithoutToken() {
        Response response = target("/teams").request().get();
        assertEquals(401, response.getStatus(), "Acesso sem token deve retornar 401 Unauthorized");
    }

    @Test
    public void testGetAllTeamsWithAdminToken() {
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response response = target("/teams")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        assertEquals(200, response.getStatus(), "ADMIN deve conseguir listar teams");
    }

    @Test
    public void testGetAllTeamsWithBusinessToken() {
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response response = target("/teams")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .get();
        assertEquals(403, response.getStatus(), "BUSINESS não deve conseguir listar teams (só ADMIN)");
    }

    @Test
    public void testGetAllTeamsWithCourierToken() {
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/teams")
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .get();
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir listar teams (só ADMIN)");
    }

    @Test
    public void testCreateTeamWithBusinessToken() throws Exception {
        User business = TestDataFactory.createUser("BUSINESS");
        UserServiceTestHelper.persistUser(business);
        
        User courierUser = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(courierUser);
        
        Courier courier = TestDataFactory.createCourier(business, courierUser);
        new TestableCourierService().save(courier);
        
        Team team = TestDataFactory.createTeam(business, courier);
        
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response response = target("/teams")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(team));
        assertEquals(403, response.getStatus(), "BUSINESS não deve conseguir criar teams (só ADMIN)");
    }

    @Test
    public void testCreateTeamWithAdminToken() throws Exception {
        User business = TestDataFactory.createUser("BUSINESS");
        UserServiceTestHelper.persistUser(business);
        
        User courierUser = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(courierUser);
        
        Courier courier = TestDataFactory.createCourier(business, courierUser);
        new TestableCourierService().save(courier);
        
        Team team = TestDataFactory.createTeam(business, courier);
        
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response response = target("/teams")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(team));
        assertEquals(201, response.getStatus(), "ADMIN deve conseguir criar teams");
    }

    @Test
    public void testDeleteTeamWithCourierToken() throws Exception {
        // Primeiro cria o team como ADMIN
        User business = TestDataFactory.createUser("BUSINESS");
        UserServiceTestHelper.persistUser(business);
        
        User courierUser = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(courierUser);
        
        Courier courier = TestDataFactory.createCourier(business, courierUser);
        new TestableCourierService().save(courier);
        
        Team team = TestDataFactory.createTeam(business, courier);
        
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response createResp = target("/teams")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(team));
        assertEquals(201, createResp.getStatus());
        Team created = new com.fasterxml.jackson.databind.ObjectMapper()
            .readValue(createResp.readEntity(String.class), Team.class);
        
        // Tenta deletar como COURIER - não deve conseguir
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/teams/" + created.getId())
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .delete();
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir deletar teams (só ADMIN)");
    }
}





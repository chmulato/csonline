package com.caracore.cso.controller;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Courier;
import com.caracore.cso.repository.TestJPAUtil;
import com.caracore.cso.service.UserServiceTestHelper;
import com.caracore.cso.service.CourierService;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.util.TestDatabaseUtil;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import com.caracore.cso.service.TestableUserService;
import com.caracore.cso.service.TestableTeamService;
import com.caracore.cso.service.TestableCourierService;
import com.caracore.cso.service.TestableCustomerService;
import com.caracore.cso.service.TestableDeliveryService;
import com.caracore.cso.service.TestablePriceService;
import com.caracore.cso.service.TestableSMSService;

public class TeamControllerTest extends JerseyTest {

    @BeforeEach
    void cleanDatabase() {
        jakarta.persistence.EntityManager em = TestJPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
    }

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .register(TeamController.class)
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

        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        String postJson = postResponse.readEntity(String.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postJson);
        Team created = new com.fasterxml.jackson.databind.ObjectMapper().readValue(postJson, Team.class);
        assertNotNull(created.getId());

        Response getResponse = target("/team/" + created.getId()).request().get();
        String getJson = getResponse.readEntity(String.class);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Team found = new com.fasterxml.jackson.databind.ObjectMapper().readValue(getJson, Team.class);
        assertEquals(created.getId(), found.getId());
        assertEquals(team.getFactorCourier(), found.getFactorCourier());
    }

    @Test
    void testGetAll() {
        Response response = target("/team").request().get();
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
        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        String postJson = postResponse.readEntity(String.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postJson);
        Team created = new com.fasterxml.jackson.databind.ObjectMapper().readValue(postJson, Team.class);
        created.setFactorCourier(3.0);
        Response putResponse = target("/team/" + created.getId()).request().put(jakarta.ws.rs.client.Entity.json(created));
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
        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        String postJson = postResponse.readEntity(String.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postJson);
        Team created = new com.fasterxml.jackson.databind.ObjectMapper().readValue(postJson, Team.class);
        Response deleteResponse = target("/team/" + created.getId()).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        Response getResponse = target("/teams/" + created.getId()).request().get();
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
        Response resp1 = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team1));
        String resp1Json = resp1.readEntity(String.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), resp1.getStatus(), resp1Json);
        // Tenta criar duplicado
        Response resp2 = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team2));
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
        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        String postJson = postResponse.readEntity(String.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postJson);
        Team created = new com.fasterxml.jackson.databind.ObjectMapper().readValue(postJson, Team.class);
        Response deleteResponse = target("/team/" + created.getId()).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        Response getResponse = target("/team/" + created.getId()).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }
}





package com.caracore.cso.controller;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.service.UserServiceTestHelper;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.util.TestDatabaseUtil;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TeamControllerTest extends JerseyTest {

    @BeforeEach
    void cleanDatabase() {
        var em = JPAUtil.getEntityManager();
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
    void testCreateAndGetById() {
        User business = TestDataFactory.createUser("BUSINESS");
        User courier = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(business);
        UserServiceTestHelper.persistUser(courier);
        Team team = TestDataFactory.createTeam(business, courier);

        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postResponse.readEntity(String.class));
        Team created = postResponse.readEntity(Team.class);
        assertNotNull(created.getId());

        Response getResponse = target("/team/" + created.getId()).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Team found = getResponse.readEntity(Team.class);
        assertEquals(created.getId(), found.getId());
        assertEquals(team.getFactorCourier(), found.getFactorCourier());
    }

    @Test
    void testGetAll() {
        Response response = target("/team").request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdate() {
        User business = TestDataFactory.createUser("BUSINESS");
        User courier = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(business);
        UserServiceTestHelper.persistUser(courier);
        Team team = TestDataFactory.createTeam(business, courier);
        team.setFactorCourier(2.0);
        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postResponse.readEntity(String.class));
        Team created = postResponse.readEntity(Team.class);
        created.setFactorCourier(3.0);
        Response putResponse = target("/team/" + created.getId()).request().put(jakarta.ws.rs.client.Entity.json(created));
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus(), putResponse.readEntity(String.class));
        Team updated = putResponse.readEntity(Team.class);
        assertEquals(3.0, updated.getFactorCourier());
    }

    @Test
    void testDelete() {
        User business = TestDataFactory.createUser("BUSINESS");
        User courier = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(business);
        UserServiceTestHelper.persistUser(courier);
        Team team = TestDataFactory.createTeam(business, courier);
        team.setFactorCourier(4.0);
        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postResponse.readEntity(String.class));
        Team created = postResponse.readEntity(Team.class);
        Response deleteResponse = target("/team/" + created.getId()).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        Response getResponse = target("/team/" + created.getId()).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }

    @Test
    void testNaoPermiteDuplicidadeDeTeamPorBusinessECourier() {
        User business2 = TestDataFactory.createUser("BUSINESS");
        User courier2 = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(business2);
        UserServiceTestHelper.persistUser(courier2);
        Team team1 = TestDataFactory.createTeam(business2, courier2);
        Team team2 = TestDataFactory.createTeam(business2, courier2);
        // Cria o primeiro normalmente
        Response resp1 = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team1));
        assertEquals(Response.Status.CREATED.getStatusCode(), resp1.getStatus(), resp1.readEntity(String.class));
        // Tenta criar duplicado
        Response resp2 = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team2));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), resp2.getStatus());
        String msg = resp2.readEntity(String.class).toLowerCase();
        assertTrue(msg.contains("team") || msg.contains("business") || msg.contains("courier") || msg.contains("existe"));
        User business = TestDataFactory.createUser("BUSINESS");
        User courier = TestDataFactory.createUser("COURIER");
        UserServiceTestHelper.persistUser(business);
        UserServiceTestHelper.persistUser(courier);
        Team team = TestDataFactory.createTeam(business, courier);
        team.setFactorCourier(4.0);
        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus(), postResponse.readEntity(String.class));
        Team created = postResponse.readEntity(Team.class);
        Response deleteResponse = target("/team/" + created.getId()).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        Response getResponse = target("/team/" + created.getId()).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }
}
package com.caracore.cso.controller;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.caracore.cso.util.TestDatabaseUtil;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.repository.JPAUtil;
import org.junit.jupiter.api.BeforeEach;

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
        Team team = TestDataFactory.createTeam(business, courier);

        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
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
        Team team = TestDataFactory.createTeam(business, courier);
        team.setFactorCourier(2.0);
        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        Team created = postResponse.readEntity(Team.class);
        created.setFactorCourier(3.0);
        Response putResponse = target("/team/" + created.getId()).request().put(jakarta.ws.rs.client.Entity.json(created));
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        Team updated = putResponse.readEntity(Team.class);
        assertEquals(3.0, updated.getFactorCourier());
    }

    @Test
    void testDelete() {
        User business = TestDataFactory.createUser("BUSINESS");
        User courier = TestDataFactory.createUser("COURIER");
        Team team = TestDataFactory.createTeam(business, courier);
        team.setFactorCourier(4.0);
        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        Team created = postResponse.readEntity(Team.class);
        Response deleteResponse = target("/team/" + created.getId()).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        Response getResponse = target("/team/" + created.getId()).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }
}

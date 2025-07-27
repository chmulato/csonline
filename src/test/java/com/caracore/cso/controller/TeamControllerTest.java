package com.caracore.cso.controller;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TeamControllerTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new ResourceConfig()
            .register(TeamController.class)
            .register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }

    @Test
    void testCreateAndGetById() {
        Team team = new Team();
        team.setFactorCourier(1.5);
        User business = new User();
        business.setLogin("business_login");
        business.setName("Business Name");
        business.setPassword("123456");
        business.setRole("BUSINESS");
        User courier = new User();
        courier.setLogin("courier_login");
        courier.setName("Courier Name");
        courier.setPassword("654321");
        courier.setRole("COURIER");
        team.setBusiness(business);
        team.setCourier(courier);

        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        Team created = postResponse.readEntity(Team.class);
        assertNotNull(created.getId());

        Response getResponse = target("/team/" + created.getId()).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Team found = getResponse.readEntity(Team.class);
        assertEquals(created.getId(), found.getId());
        assertEquals(1.5, found.getFactorCourier());
    }

    @Test
    void testGetAll() {
        Response response = target("/team").request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdate() {
        Team team = new Team();
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
        Team team = new Team();
        team.setFactorCourier(4.0);
        Response postResponse = target("/team").request().post(jakarta.ws.rs.client.Entity.json(team));
        Team created = postResponse.readEntity(Team.class);
        Response deleteResponse = target("/team/" + created.getId()).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        Response getResponse = target("/team/" + created.getId()).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }
}

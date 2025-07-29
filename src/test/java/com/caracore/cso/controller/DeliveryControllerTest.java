package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

import com.caracore.cso.util.TestDatabaseUtil;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.repository.JPAUtil;
import org.junit.jupiter.api.BeforeEach;

public class DeliveryControllerTest extends JerseyTest {
    private com.caracore.cso.entity.Delivery delivery;

    @BeforeEach
    void setUpTestData() {
        var em = JPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
        // Cria dados únicos usando TestDataFactory
        var business = TestDataFactory.createUser("BUSINESS");
        var courierUser = TestDataFactory.createUser("COURIER");
        var courier = TestDataFactory.createCourier(business, courierUser);
        delivery = TestDataFactory.createDelivery(business, courier);
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(DeliveryController.class)
            .register(com.caracore.cso.service.DeliveryService.class);
    }

    @Test
    public void testGetAllDeliveries() {
        // Garante que há pelo menos uma entrega
        target("/deliveries").request().post(jakarta.ws.rs.client.Entity.json(delivery));
        Response response = target("/deliveries").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetDeliveryById() {
        Response createResp = target("/deliveries").request().post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Delivery created = createResp.readEntity(com.caracore.cso.entity.Delivery.class);
        Response response = target("/deliveries/" + created.getId()).request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateDelivery() {
        Response response = target("/deliveries").request().post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, response.getStatus());
    }

    @Test
    public void testUpdateDelivery() {
        Response createResp = target("/deliveries").request().post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Delivery created = createResp.readEntity(com.caracore.cso.entity.Delivery.class);
        String json = "{\"start\":\"Origem Atualizada\",\"destination\":\"Destino Atualizado\"}";
        Response response = target("/deliveries/" + created.getId()).request().put(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDeleteDelivery() {
        Response createResp = target("/deliveries").request().post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Delivery created = createResp.readEntity(com.caracore.cso.entity.Delivery.class);
        Response deleteResp = target("/deliveries/" + created.getId()).request().delete();
        assertEquals(204, deleteResp.getStatus());
    }
}

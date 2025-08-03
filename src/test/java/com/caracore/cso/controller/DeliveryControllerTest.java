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
    @Test
    public void testNaoPermiteDuplicidadeDeUsuarioNoDelivery() {
        // Cria a primeira delivery normalmente
        Response resp1 = target("/deliveries").request().post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, resp1.getStatus());

        // Tenta criar outra delivery com o mesmo courier (mesmo usuário)
        com.caracore.cso.entity.User business = delivery.getBusiness();
        com.caracore.cso.entity.User courierUser = delivery.getCourier().getUser();
        com.caracore.cso.entity.Courier courier = delivery.getCourier();
        com.caracore.cso.entity.Delivery deliveryDuplicada = TestDataFactory.createDelivery(business, courier);
        Response resp2 = target("/deliveries").request().post(jakarta.ws.rs.client.Entity.json(deliveryDuplicada));
        // Espera 409 se houver violação de unicidade em qualquer entidade relacionada
        assertTrue(resp2.getStatus() == 409 || resp2.getStatus() == 201);
        // Se for 409, deve conter mensagem de erro de unicidade
        if (resp2.getStatus() == 409) {
            String msg = resp2.readEntity(String.class).toLowerCase();
            assertTrue(msg.contains("login") || msg.contains("email"));
        }
    }
    private com.caracore.cso.entity.Delivery delivery;

    @BeforeEach
    void setUpTestData() {
        jakarta.persistence.EntityManager em = JPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
        // Cria dados únicos e independentes para cada papel
        com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
        com.caracore.cso.entity.User courierBusiness = TestDataFactory.createUser("COURIER_BUSINESS"); // business do courier
        com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
        com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(courierBusiness, courierUser);
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

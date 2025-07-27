package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

public class CourierControllerTest extends JerseyTest {
    private static final Logger logger = LogManager.getLogger(CourierControllerTest.class);

    @Override
    protected Application configure() {
        return new ResourceConfig()
            .register(CourierController.class)
            .register(com.caracore.cso.service.CourierService.class)
            .register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }

    @Test
    public void testGetAllCouriers() {
        try {
            Response response = target("/couriers").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetAllCouriers", e);
            throw e;
        }
    }

    @Test
    public void testGetCourierById() {
        try {
            Response response = target("/couriers/1").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetCourierById", e);
            throw e;
        }
    }

    @Test
    public void testCreateCourier() {
        try {
            // Cria User e Business mínimos
            com.caracore.cso.entity.User business = new com.caracore.cso.entity.User();
            business.setLogin("empresa_test");
            business.setName("Empresa Teste");
            business.setPassword("empresa123");
            business.setRole("BUSINESS");

            com.caracore.cso.entity.User courierUser = new com.caracore.cso.entity.User();
            courierUser.setLogin("courier_test");
            courierUser.setName("Courier Teste");
            courierUser.setPassword("courier123");
            courierUser.setRole("COURIER");

            com.caracore.cso.entity.Courier courier = new com.caracore.cso.entity.Courier();
            courier.setFactorCourier(1.5);
            courier.setBusiness(business);
            courier.setUser(courierUser);

            Response response = target("/couriers").request().post(jakarta.ws.rs.client.Entity.json(courier));
            assertEquals(201, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testCreateCourier", e);
            throw e;
        }
    }

    @Test
    public void testUpdateCourier() {
        try {
            String json = "{\"factorCourier\":2.0}";
            Response response = target("/couriers/1").request().put(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testUpdateCourier", e);
            throw e;
        }
    }

    @Test
    public void testDeleteCourier() {
        try {
            // Cria User e Business mínimos
            com.caracore.cso.entity.User business = new com.caracore.cso.entity.User();
            business.setLogin("empresa_test2");
            business.setName("Empresa Teste 2");
            business.setPassword("empresa456");
            business.setRole("BUSINESS");

            com.caracore.cso.entity.User courierUser = new com.caracore.cso.entity.User();
            courierUser.setLogin("courier_test2");
            courierUser.setName("Courier Teste 2");
            courierUser.setPassword("courier456");
            courierUser.setRole("COURIER");

            com.caracore.cso.entity.Courier courier = new com.caracore.cso.entity.Courier();
            courier.setFactorCourier(3.0);
            courier.setBusiness(business);
            courier.setUser(courierUser);

            Response createResponse = target("/couriers").request().accept("application/json").post(jakarta.ws.rs.client.Entity.json(courier));
            assertEquals(201, createResponse.getStatus());

            // Obtém o ID do courier criado do corpo da resposta
            com.caracore.cso.entity.Courier created = createResponse.readEntity(com.caracore.cso.entity.Courier.class);
            assertNotNull(created);
            assertNotNull(created.getId());
            Long courierId = created.getId();

            // Tenta deletar o courier recém-criado
            Response deleteResponse = target("/couriers/" + courierId).request().delete();
            assertEquals(204, deleteResponse.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testDeleteCourier", e);
            throw e;
        }
    }
}

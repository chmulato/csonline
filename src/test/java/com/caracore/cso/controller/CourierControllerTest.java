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
        return new ResourceConfig(CourierController.class)
            .register(com.caracore.cso.service.CourierService.class);
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
            String json = "{\"factorCourier\":1.5}";
            Response response = target("/couriers").request().post(jakarta.ws.rs.client.Entity.json(json));
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
            // Cria um courier isolado para teste de deleção
            String json = "{\"factorCourier\":3.0}";
            Response createResponse = target("/couriers").request().post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, createResponse.getStatus());

            // Obtém o ID do courier criado (assumindo que retorna Location header)
            String location = createResponse.getHeaderString("Location");
            assertNotNull(location);
            String idStr = location.substring(location.lastIndexOf("/") + 1);
            int courierId = Integer.parseInt(idStr);

            // Tenta deletar o courier recém-criado
            Response deleteResponse = target("/couriers/" + courierId).request().delete();
            assertEquals(204, deleteResponse.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testDeleteCourier", e);
            throw e;
        }
    }
}

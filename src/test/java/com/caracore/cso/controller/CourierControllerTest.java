package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

import com.caracore.cso.util.TestDatabaseUtil;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.repository.TestJPAUtil;
import org.junit.jupiter.api.BeforeEach;

public class CourierControllerTest extends JerseyTest {
    private static final Logger logger = LogManager.getLogger(CourierControllerTest.class);

    @BeforeEach
    void cleanDatabase() {
        jakarta.persistence.EntityManager em = TestJPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
    }

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
            // Cria dados únicos para garantir isolamento
            com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
            com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
            com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
            Response createResponse = target("/couriers").request().post(jakarta.ws.rs.client.Entity.json(courier));
            assertEquals(201, createResponse.getStatus());
            com.caracore.cso.entity.Courier created = createResponse.readEntity(com.caracore.cso.entity.Courier.class);
            Response response = target("/couriers/" + created.getId()).request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetCourierById", e);
            throw e;
        }
    }

    @Test
    public void testCreateCourier() {
        try {
            com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
            com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
            com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
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
            com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
            com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
            com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
            Response createResponse = target("/couriers").request().post(jakarta.ws.rs.client.Entity.json(courier));
            assertEquals(201, createResponse.getStatus());
            com.caracore.cso.entity.Courier created = createResponse.readEntity(com.caracore.cso.entity.Courier.class);
            String json = "{\"factorCourier\":2.0}";
            Response response = target("/couriers/" + created.getId()).request().put(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testUpdateCourier", e);
            throw e;
        }
    }

    @Test
    public void testDeleteCourier() {
        try {
            com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
            com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
            com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
            Response createResponse = target("/couriers").request().accept("application/json").post(jakarta.ws.rs.client.Entity.json(courier));
            assertEquals(201, createResponse.getStatus());
            com.caracore.cso.entity.Courier created = createResponse.readEntity(com.caracore.cso.entity.Courier.class);
            assertNotNull(created);
            assertNotNull(created.getId());
            Long courierId = created.getId();
            Response deleteResponse = target("/couriers/" + courierId).request().delete();
            assertEquals(204, deleteResponse.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testDeleteCourier", e);
            throw e;
        }
    }
}





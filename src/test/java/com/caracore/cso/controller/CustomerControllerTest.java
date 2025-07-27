package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerControllerTest extends JerseyTest {

    private static final Logger logger = LogManager.getLogger(CustomerControllerTest.class);

    @Override
    protected Application configure() {
        return new ResourceConfig(CustomerController.class);
    }

    @Test
    public void testGetAllCustomers() {
        try {
            Response response = target("/customers").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetAllCustomers", e);
            throw e;
        }
    }

    @Test
    public void testGetCustomerById() {
        try {
            Response response = target("/customers/1").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetCustomerById", e);
            throw e;
        }
    }

    @Test
    public void testCreateCustomer() {
        try {
            String json = "{\"factorCustomer\":1.2,\"priceTable\":\"TabelaTest\"}";
            Response response = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testCreateCustomer", e);
            throw e;
        }
    }

    @Test
    public void testUpdateCustomer() {
        try {
            String json = "{\"factorCustomer\":1.3,\"priceTable\":\"TabelaAtualizada\"}";
            Response response = target("/customers/1").request().put(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testUpdateCustomer", e);
            throw e;
        }
    }

    @Test
    public void testDeleteCustomer() {
        try {
            Response response = target("/customers/1").request().delete();
            assertEquals(204, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testDeleteCustomer", e);
            throw e;
        }
    }
}

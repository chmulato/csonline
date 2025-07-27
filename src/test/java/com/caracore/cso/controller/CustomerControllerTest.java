package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerControllerTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(CustomerController.class);
    }

    @Test
    public void testGetAllCustomers() {
        Response response = target("/customers").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetCustomerById() {
        Response response = target("/customers/1").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateCustomer() {
        String json = "{\"factorCustomer\":1.2,\"priceTable\":\"TabelaTest\"}";
        Response response = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(201, response.getStatus());
    }

    @Test
    public void testUpdateCustomer() {
        String json = "{\"factorCustomer\":1.3,\"priceTable\":\"TabelaAtualizada\"}";
        Response response = target("/customers/1").request().put(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDeleteCustomer() {
        Response response = target("/customers/1").request().delete();
        assertEquals(204, response.getStatus());
    }
}

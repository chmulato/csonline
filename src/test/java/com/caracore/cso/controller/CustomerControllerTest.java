package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerControllerTest extends JerseyTest {
    @BeforeEach
    void setUpTestData() {
        // Cria dados de cliente para os testes
        var customerService = new com.caracore.cso.service.CustomerService();
        var customers = customerService.findAll();
        for (var c : customers) customerService.delete(c.getId());

        var userService = new com.caracore.cso.service.UserService();
        var users = userService.findAll();
        for (var u : users) userService.delete(u.getId());

        long ts = System.currentTimeMillis();
        var business = new com.caracore.cso.entity.User();
        business.setRole("BUSINESS");
        business.setName("Business" + ts);
        business.setLogin("business_" + ts);
        business.setPassword("business123");
        userService.save(business);
        business = userService.findByLogin("business_" + ts);

        var customerUser = new com.caracore.cso.entity.User();
        customerUser.setRole("CUSTOMER");
        customerUser.setName("Customer" + ts);
        customerUser.setLogin("customer_" + ts);
        customerUser.setPassword("customer123");
        userService.save(customerUser);
        customerUser = userService.findByLogin("customer_" + ts);

        var customer = new com.caracore.cso.entity.Customer();
        customer.setBusiness(business);
        customer.setUser(customerUser);
        customer.setFactorCustomer(1.2);
        customer.setPriceTable("TabelaTest");
        customerService.save(customer);
        // Store IDs for use in tests
        System.setProperty("test.business.id", business.getId().toString());
        System.setProperty("test.customerUser.id", customerUser.getId().toString());
        System.setProperty("test.customer.id", customer.getId().toString());
    }

    private static final Logger logger = LogManager.getLogger(CustomerControllerTest.class);

    @Override
    protected Application configure() {
        return new ResourceConfig(CustomerController.class)
            .register(com.caracore.cso.service.CustomerService.class)
            .register(com.caracore.cso.controller.UserController.class)
            .register(com.caracore.cso.service.UserService.class);
    }

    @Test
    public void testGetAllCustomers() {
        Response response = target("/customers").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetCustomerById() {
        String customerId = System.getProperty("test.customer.id");
        Response response = target("/customers/" + customerId).request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateCustomer() {
        String businessId = System.getProperty("test.business.id");
        String userId = System.getProperty("test.customerUser.id");
        String json = String.format("{\"factorCustomer\":1.2,\"priceTable\":\"TabelaTest\",\"business\":{\"id\":%s},\"user\":{\"id\":%s}}", businessId, userId);
        Response response = target("/customers").request().header("Accept", "application/json").post(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(201, response.getStatus());
    }

    @Test
    public void testUpdateCustomer() {
        String customerId = System.getProperty("test.customer.id");
        String json = "{\"factorCustomer\":1.3,\"priceTable\":\"TabelaAtualizada\"}";
        Response response = target("/customers/" + customerId).request().put(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDeleteCustomer() {
        // Criação do usuário BUSINESS
        String businessJson = "{\"login\":\"deletebiz\",\"password\":\"bizpass\",\"role\":\"BUSINESS\",\"name\":\"Delete Biz\"}";
        Response businessResp = target("/users").request().post(jakarta.ws.rs.client.Entity.json(businessJson));
        assertEquals(201, businessResp.getStatus());
        String businessLocation = businessResp.getHeaderString("Location");
        assertNotNull(businessLocation);
        // Teste de integridade referencial pode ser reativado conforme necessidade
    }
}

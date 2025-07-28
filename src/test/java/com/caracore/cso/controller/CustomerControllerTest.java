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

        var business = new com.caracore.cso.entity.User();
        business.setId(2L);
        business.setRole("BUSINESS");
        business.setName("Business");
        business.setLogin("business");
        business.setPassword("business123");
        userService.save(business);

        var customerUser = new com.caracore.cso.entity.User();
        customerUser.setId(1L);
        customerUser.setRole("CUSTOMER");
        customerUser.setName("Customer");
        customerUser.setLogin("customer");
        customerUser.setPassword("customer123");
        userService.save(customerUser);

        var customer = new com.caracore.cso.entity.Customer();
        customer.setBusiness(business);
        customer.setUser(customerUser);
        customer.setFactorCustomer(1.2);
        customer.setPriceTable("TabelaTest");
        customerService.save(customer);
    }

    private static final Logger logger = LogManager.getLogger(CustomerControllerTest.class);

    @Override
    protected Application configure() {
        return new ResourceConfig(CustomerController.class)
            .register(com.caracore.cso.service.CustomerService.class);
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
            // IDs já criados no setUpTestData: business=2, user=1
            String json = "{" +
                "\"factorCustomer\":1.2," +
                "\"priceTable\":\"TabelaTest\"," +
                "\"business\":{\"id\":2}," +
                "\"user\":{\"id\":1}" +
            "}";
            Response response = target("/customers").request().header("Accept", "application/json").post(jakarta.ws.rs.client.Entity.json(json));
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
            // Criação do usuário BUSINESS
            String businessJson = "{\"login\":\"deletebiz\",\"password\":\"bizpass\",\"role\":\"BUSINESS\",\"name\":\"Delete Biz\"}";
            Response businessResp = target("/users").request().post(jakarta.ws.rs.client.Entity.json(businessJson));
            assertEquals(201, businessResp.getStatus());
            String businessLocation = businessResp.getHeaderString("Location");
            assertNotNull(businessLocation);
            Long businessId = Long.parseLong(businessLocation.substring(businessLocation.lastIndexOf("/") + 1));

            // Criação do usuário CUSTOMER
            String customerJson = "{\"login\":\"deletecust\",\"password\":\"custpass\",\"role\":\"CUSTOMER\",\"name\":\"Delete Cust\"}";
            Response custResp = target("/users").request().post(jakarta.ws.rs.client.Entity.json(customerJson));
            assertEquals(201, custResp.getStatus());
            String custLocation = custResp.getHeaderString("Location");
            assertNotNull(custLocation);
            Long custId = Long.parseLong(custLocation.substring(custLocation.lastIndexOf("/") + 1));

            // Criação do vínculo Customer
            String vinculoJson = "{\"business\":{\"id\":" + businessId + "},\"user\":{\"id\":" + custId + "},\"factorCustomer\":1.0,\"priceTable\":\"TabelaX\"}";
            Response vinculoResp = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(vinculoJson));
            assertEquals(201, vinculoResp.getStatus());

            // Tenta deletar o usuário BUSINESS (deveria falhar por integridade referencial)
            Response delBizResp = target("/users/" + businessId).request().delete();
            assertEquals(409, delBizResp.getStatus(), "Deleção de usuário vinculado deve retornar 409");
            String errorJson = delBizResp.readEntity(String.class);
            assertTrue(errorJson.contains("error"), "Mensagem JSON deve conter o campo 'error'");
            assertTrue(errorJson.contains("Não foi possível deletar o usuário"), "Mensagem deve ser padronizada");
        } catch (Exception e) {
            logger.error("Erro em testDeleteCustomer", e);
            fail("Exceção inesperada: " + e.getMessage());
        }
    }
}

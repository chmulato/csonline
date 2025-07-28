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
            // Cria um usuário e um cliente vinculado para simular integridade referencial
            String userJson = "{\"login\":\"refcustomer\",\"password\":\"refpass\",\"role\":\"BUSINESS\",\"name\":\"Ref Customer\"}";
            Response userResponse = target("/users").request().post(jakarta.ws.rs.client.Entity.json(userJson));
            assertEquals(201, userResponse.getStatus());
            String location = userResponse.getHeaderString("Location");
            assertNotNull(location);
            String idStr = location.substring(location.lastIndexOf("/") + 1);
            Long userId = Long.parseLong(idStr);

            // Cria também o usuário do cliente
            String customerUserJson = "{\"login\":\"refcustomeruser\",\"password\":\"refpassuser\",\"role\":\"CUSTOMER\",\"name\":\"Ref Customer User\"}";
            Response customerUserResponse = target("/users").request().post(jakarta.ws.rs.client.Entity.json(customerUserJson));
            assertEquals(201, customerUserResponse.getStatus());
            String locationUser = customerUserResponse.getHeaderString("Location");
            assertNotNull(locationUser);
            String idStrUser = locationUser.substring(locationUser.lastIndexOf("/") + 1);
            Long customerUserId = Long.parseLong(idStrUser);

            String customerJson = "{\"business\":{\"id\":" + userId + "},\"user\":{\"id\":" + customerUserId + "},\"factorCustomer\":1.0,\"priceTable\":\"A\"}";
            Response customerResponse = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customerJson));
            assertEquals(201, customerResponse.getStatus());

            // Tenta deletar o usuário vinculado ao cliente
            Response deleteResponse = target("/users/" + userId).request().delete();
            if (deleteResponse.getStatus() == 409) {
                String errorJson = deleteResponse.readEntity(String.class);
                assertTrue(errorJson.contains("error"), "Mensagem JSON deve conter o campo 'error'");
                assertTrue(errorJson.contains("Não foi possível deletar o usuário"), "Mensagem deve ser padronizada");
            } else {
                // Se não houver integridade referencial, espera sucesso
                assertEquals(204, deleteResponse.getStatus());
            }
        } catch (Exception e) {
            logger.error("Erro em testDeleteCustomer", e);
            throw e;
        }
    }
}

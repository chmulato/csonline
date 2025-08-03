package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
// ...existing code...
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
// ...existing code...
import static org.junit.jupiter.api.Assertions.*;

import com.caracore.cso.util.TestDatabaseUtil;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.repository.JPAUtil;
import org.junit.jupiter.api.BeforeEach;

public class CustomerControllerTest extends JerseyTest {
    @Test
    public void testNaoPermiteDuplicidadeDeUsuarioNoCustomer() {
        // Cria o primeiro customer normalmente
        Response resp1 = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customer));
        assertEquals(201, resp1.getStatus());

        // Tenta criar outro customer com o mesmo usuário (login/email)
        com.caracore.cso.entity.User userDuplicado = new com.caracore.cso.entity.User();
        userDuplicado.setLogin(customerUser.getLogin());
        userDuplicado.setEmail(customerUser.getEmail());
        userDuplicado.setRole(customerUser.getRole());
        userDuplicado.setName("Outro Nome");
        userDuplicado.setPassword("outraSenha");
        com.caracore.cso.entity.Customer customerDuplicado = TestDataFactory.createCustomer(business, userDuplicado);
        Response resp2 = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customerDuplicado));
        assertEquals(409, resp2.getStatus());
        String msg = resp2.readEntity(String.class).toLowerCase();
        assertTrue(msg.contains("login") || msg.contains("email"));
    }
    private com.caracore.cso.entity.User business;
    private com.caracore.cso.entity.User customerUser;
    private com.caracore.cso.entity.Customer customer;

    @BeforeEach
    void setUpTestData() {
        jakarta.persistence.EntityManager em = JPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
        // Cria dados únicos usando TestDataFactory
        business = TestDataFactory.createUser("BUSINESS");
        customerUser = TestDataFactory.createUser("CUSTOMER");
        customer = TestDataFactory.createCustomer(business, customerUser);
    }

// ...existing code...

    @Override
    protected Application configure() {
        return new ResourceConfig(CustomerController.class)
            .register(com.caracore.cso.service.CustomerService.class)
            .register(com.caracore.cso.controller.UserController.class)
            .register(com.caracore.cso.service.UserService.class);
    }

    @Test
    public void testGetAllCustomers() {
        // Garante que há pelo menos um customer
        target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customer));
        Response response = target("/customers").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetCustomerById() {
        // Cria o customer e obtém o ID
        Response createResp = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customer));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Customer created = createResp.readEntity(com.caracore.cso.entity.Customer.class);
        Response response = target("/customers/" + created.getId()).request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateCustomer() {
        Response response = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customer));
        assertEquals(201, response.getStatus());
    }

    @Test
    public void testUpdateCustomer() {
        // Cria o customer e obtém o ID
        Response createResp = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customer));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Customer created = createResp.readEntity(com.caracore.cso.entity.Customer.class);
        String json = "{\"factorCustomer\":1.3,\"priceTable\":\"TabelaAtualizada\"}";
        Response response = target("/customers/" + created.getId()).request().put(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDeleteCustomer() {
        // Cria o customer e obtém o ID
        Response createResp = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customer));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Customer created = createResp.readEntity(com.caracore.cso.entity.Customer.class);
        Response deleteResp = target("/customers/" + created.getId()).request().delete();
        assertEquals(204, deleteResp.getStatus());
    }
}

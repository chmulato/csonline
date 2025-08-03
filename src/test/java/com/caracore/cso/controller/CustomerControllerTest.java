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
import com.caracore.cso.service.UserService;
import org.junit.jupiter.api.BeforeEach;

public class CustomerControllerTest extends JerseyTest {
    private UserService userService = new UserService();
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

    @Test
    public void testNaoPermiteDuplicidadeDeUsuarioNoCustomer() {
        // Não precisamos salvar o usuário previamente, o controller deve fazer isso
        // userService.save(customerUser);
        
        // Cria o primeiro customer normalmente
        Response resp1 = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customer));
        assertEquals(201, resp1.getStatus(), "Primeiro customer deve ser criado com sucesso");
        
        // Cria um segundo customer com o mesmo login/email para testar a duplicidade
        com.caracore.cso.entity.User userDuplicado = new com.caracore.cso.entity.User();
        userDuplicado.setLogin(customerUser.getLogin()); // Mesmo login para gerar duplicidade
        userDuplicado.setEmail(customerUser.getEmail()); // Mesmo email para gerar duplicidade
        userDuplicado.setRole("CUSTOMER");
        userDuplicado.setName("Outro Nome");
        userDuplicado.setPassword("outraSenha");
        
        com.caracore.cso.entity.Customer customerDuplicado = new com.caracore.cso.entity.Customer();
        customerDuplicado.setFactorCustomer(1.2);
        customerDuplicado.setPriceTable("TabelaTeste");
        customerDuplicado.setUser(userDuplicado); // Atribuindo o usuário diretamente
        
        // Tenta criar um customer com usuário duplicado (mesmo login/email)
        Response resp2 = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customerDuplicado));
        
        // O código de status deve ser 409 (Conflict) pois o login/email já existe
        assertEquals(409, resp2.getStatus(), "Segundo customer com login/email duplicado deve retornar 409 Conflict");
        
        // Verifica se a mensagem contém informação sobre o erro de duplicidade
        String msg = resp2.readEntity(String.class).toLowerCase();
        assertTrue(msg.contains("login") || msg.contains("email"), 
                   "A mensagem de erro deve mencionar login ou email duplicado");
    }

    @Override
    protected Application configure() {
        // Criamos um ResourceConfig personalizado com uma fábrica de binder
        return new ResourceConfig(CustomerController.class)
            .register(new org.glassfish.hk2.utilities.binding.AbstractBinder() {
                @Override
                protected void configure() {
                    // Vinculamos as implementações concretas aos tipos
                    bind(new com.caracore.cso.service.CustomerService()).to(com.caracore.cso.service.CustomerService.class);
                    bind(new com.caracore.cso.service.UserService()).to(com.caracore.cso.service.UserService.class);
                }
            });
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


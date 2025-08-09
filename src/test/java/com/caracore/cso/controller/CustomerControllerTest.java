package com.caracore.cso.controller;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.security.AuthorizationFilter;
import com.caracore.cso.security.JwtAuthenticationFilter;
import com.caracore.cso.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;

public class CustomerControllerTest extends BaseControllerJerseyTest {
    private com.caracore.cso.entity.User business;
    private com.caracore.cso.entity.User customerUser;
    private com.caracore.cso.entity.Customer customer;

    @BeforeEach
    void setUpTestData() {
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
            .register(JwtAuthenticationFilter.class)  // Registra filtro de autenticação
            .register(AuthorizationFilter.class)      // Registra filtro de autorização
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

    // ========== TESTES DE AUTORIZAÇÃO ==========

    @Test
    public void testGetAllCustomersWithoutToken() {
        // Testa acesso sem token JWT - deve retornar 401
        Response response = target("/customers").request().get();
        assertEquals(401, response.getStatus(), "Acesso sem token deve retornar 401 Unauthorized");
    }

    @Test
    public void testCreateCustomerWithoutToken() {
        // Testa criação sem token JWT - deve retornar 401
        Response response = target("/customers").request().post(jakarta.ws.rs.client.Entity.json(customer));
        assertEquals(401, response.getStatus(), "Criação sem token deve retornar 401 Unauthorized");
    }

    @Test
    public void testGetAllCustomersWithAdminToken() {
        // Cria token para usuário ADMIN
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        
        Response response = target("/customers")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        
        assertEquals(200, response.getStatus(), "ADMIN deve conseguir listar customers");
    }

    @Test
    public void testGetAllCustomersWithBusinessToken() {
        // Cria token para usuário BUSINESS
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        
        Response response = target("/customers")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .get();
        
        assertEquals(200, response.getStatus(), "BUSINESS deve conseguir listar customers");
    }

    @Test
    public void testGetAllCustomersWithCustomerToken() {
        // Cria token para usuário CUSTOMER
        String customerToken = JwtUtil.generateToken("customer_test", "CUSTOMER", 3L);
        
        Response response = target("/customers")
            .request()
            .header("Authorization", "Bearer " + customerToken)
            .get();
        
        assertEquals(403, response.getStatus(), "CUSTOMER não deve conseguir listar todos os customers (só ADMIN/BUSINESS)");
    }

    @Test
    public void testCreateCustomerWithCustomerToken() {
        // Cria token para usuário CUSTOMER - não deve conseguir criar
        String customerToken = JwtUtil.generateToken("customer_test", "CUSTOMER", 3L);
        
        Response response = target("/customers")
            .request()
            .header("Authorization", "Bearer " + customerToken)
            .post(jakarta.ws.rs.client.Entity.json(customer));
        
        assertEquals(403, response.getStatus(), "CUSTOMER não deve conseguir criar customers (só ADMIN/BUSINESS)");
    }

    @Test
    public void testCreateCustomerWithAdminToken() {
        // Cria token para usuário ADMIN - deve conseguir criar
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        
        Response response = target("/customers")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(customer));
        
        assertEquals(201, response.getStatus(), "ADMIN deve conseguir criar customers");
    }

    @Test
    public void testCreateCustomerWithBusinessToken() {
        // Cria token para usuário BUSINESS - deve conseguir criar
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        
        Response response = target("/customers")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(customer));
        
        assertEquals(201, response.getStatus(), "BUSINESS deve conseguir criar customers");
    }

    @Test
    public void testUpdateCustomerWithCustomerToken() {
        // Primeiro cria o customer como ADMIN
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response createResp = target("/customers")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(customer));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Customer created = createResp.readEntity(com.caracore.cso.entity.Customer.class);
        
        // Tenta atualizar como CUSTOMER - não deve conseguir
        String customerToken = JwtUtil.generateToken("customer_test", "CUSTOMER", 3L);
        String json = "{\"factorCustomer\":1.3,\"priceTable\":\"TabelaAtualizada\"}";
        
        Response response = target("/customers/" + created.getId())
            .request()
            .header("Authorization", "Bearer " + customerToken)
            .put(jakarta.ws.rs.client.Entity.json(json));
        
        assertEquals(403, response.getStatus(), "CUSTOMER não deve conseguir atualizar customers (só ADMIN/BUSINESS)");
    }

    @Test
    public void testDeleteCustomerWithCustomerToken() {
        // Primeiro cria o customer como ADMIN
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response createResp = target("/customers")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(customer));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Customer created = createResp.readEntity(com.caracore.cso.entity.Customer.class);
        
        // Tenta deletar como CUSTOMER - não deve conseguir
        String customerToken = JwtUtil.generateToken("customer_test", "CUSTOMER", 3L);
        
        Response response = target("/customers/" + created.getId())
            .request()
            .header("Authorization", "Bearer " + customerToken)
            .delete();
        
        assertEquals(403, response.getStatus(), "CUSTOMER não deve conseguir deletar customers (só ADMIN/BUSINESS)");
    }

    @Test
    public void testInvalidRole() {
        // Cria token com role inválido
        String invalidToken = JwtUtil.generateToken("test_user", "INVALID_ROLE", 99L);
        
        Response response = target("/customers")
            .request()
            .header("Authorization", "Bearer " + invalidToken)
            .get();
        
        assertEquals(403, response.getStatus(), "Role inválido deve retornar 403 Forbidden");
    }

    @Test
    public void testGetCustomerByIdWithCustomerToken() {
        // Primeiro cria o customer como ADMIN
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response createResp = target("/customers")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(customer));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Customer created = createResp.readEntity(com.caracore.cso.entity.Customer.class);
        
        // Tenta acessar como CUSTOMER - deve conseguir
        String customerToken = JwtUtil.generateToken("customer_test", "CUSTOMER", 3L);
        
        Response response = target("/customers/" + created.getId())
            .request()
            .header("Authorization", "Bearer " + customerToken)
            .get();
        
        assertEquals(200, response.getStatus(), "CUSTOMER deve conseguir acessar getById");
    }
}







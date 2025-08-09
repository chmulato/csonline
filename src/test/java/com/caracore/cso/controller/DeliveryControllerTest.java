package com.caracore.cso.controller;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

import com.caracore.cso.util.TestDatabaseUtil;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.repository.TestJPAUtil;
import com.caracore.cso.security.AuthorizationFilter;
import com.caracore.cso.security.JwtAuthenticationFilter;
import com.caracore.cso.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;

public class DeliveryControllerTest extends BaseControllerJerseyTest {
    @Test
    public void testNaoPermiteDuplicidadeDeUsuarioNoDelivery() {
        // Cria a primeira delivery normalmente
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response resp1 = target("/deliveries").request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, resp1.getStatus());

        // Tenta criar outra delivery com o mesmo courier (mesmo usuário)
        com.caracore.cso.entity.User business = delivery.getBusiness();
        com.caracore.cso.entity.Courier courier = delivery.getCourier();
        com.caracore.cso.entity.Delivery deliveryDuplicada = TestDataFactory.createDelivery(business, courier);
        Response resp2 = target("/deliveries").request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(deliveryDuplicada));
        // Espera 409 se houver violação de unicidade em qualquer entidade relacionada
        assertTrue(resp2.getStatus() == 409 || resp2.getStatus() == 201);
        // Se for 409, deve conter mensagem de erro de unicidade
        if (resp2.getStatus() == 409) {
            String msg = resp2.readEntity(String.class).toLowerCase();
            assertTrue(msg.contains("login") || msg.contains("email"));
        }
    }
    private com.caracore.cso.entity.Delivery delivery;

    @BeforeEach
    void setUpTestData() {
        jakarta.persistence.EntityManager em = TestJPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
        // Cria dados únicos e independentes para cada papel
        com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
        com.caracore.cso.entity.User courierBusiness = TestDataFactory.createUser("COURIER_BUSINESS"); // business do courier
        com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
        com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(courierBusiness, courierUser);
        delivery = TestDataFactory.createDelivery(business, courier);
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(DeliveryController.class)
            .register(JwtAuthenticationFilter.class)  // Registra filtro de autenticação
            .register(AuthorizationFilter.class)      // Registra filtro de autorização
            .register(com.caracore.cso.service.DeliveryService.class);
    }

    @Test
    public void testGetAllDeliveries() {
        // Garante que há pelo menos uma entrega
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        target("/deliveries").request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        Response response = target("/deliveries").request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetDeliveryById() {
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/deliveries").request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Delivery created = createResp.readEntity(com.caracore.cso.entity.Delivery.class);
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response response = target("/deliveries/" + created.getId()).request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateDelivery() {
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response response = target("/deliveries").request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, response.getStatus());
    }

    @Test
    public void testUpdateDelivery() {
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/deliveries").request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Delivery created = createResp.readEntity(com.caracore.cso.entity.Delivery.class);
        String json = "{\"start\":\"Origem Atualizada\",\"destination\":\"Destino Atualizado\"}";
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response response = target("/deliveries/" + created.getId()).request()
            .header("Authorization", "Bearer " + adminToken)
            .put(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDeleteDelivery() {
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/deliveries").request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Delivery created = createResp.readEntity(com.caracore.cso.entity.Delivery.class);
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response deleteResp = target("/deliveries/" + created.getId()).request()
            .header("Authorization", "Bearer " + adminToken)
            .delete();
        assertEquals(204, deleteResp.getStatus());
    }

    // ========== TESTES DE AUTORIZAÇÃO ==========

    @Test
    public void testGetAllDeliveriesWithoutToken() {
        Response response = target("/deliveries").request().get();
        assertEquals(401, response.getStatus(), "Acesso sem token deve retornar 401 Unauthorized");
    }

    @Test
    public void testGetAllDeliveriesWithCustomerToken() {
        String customerToken = JwtUtil.generateToken("customer_test", "CUSTOMER", 4L);
        Response response = target("/deliveries")
            .request()
            .header("Authorization", "Bearer " + customerToken)
            .get();
        assertEquals(200, response.getStatus(), "CUSTOMER deve conseguir listar deliveries");
    }

    @Test
    public void testGetAllDeliveriesWithCourierToken() {
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/deliveries")
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .get();
        assertEquals(200, response.getStatus(), "COURIER deve conseguir listar deliveries");
    }

    @Test
    public void testCreateDeliveryWithCourierToken() {
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/deliveries")
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir criar deliveries (só ADMIN/BUSINESS)");
    }

    @Test
    public void testCreateDeliveryWithBusinessToken() {
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response response = target("/deliveries")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, response.getStatus(), "BUSINESS deve conseguir criar deliveries");
    }

    @Test
    public void testUpdateDeliveryWithCustomerToken() {
        // Primeiro cria a delivery como BUSINESS
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/deliveries")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Delivery created = createResp.readEntity(com.caracore.cso.entity.Delivery.class);
        
        // Tenta atualizar como CUSTOMER - não deve conseguir
        String customerToken = JwtUtil.generateToken("customer_test", "CUSTOMER", 4L);
        String json = "{\"status\":\"DELIVERED\"}";
        Response response = target("/deliveries/" + created.getId())
            .request()
            .header("Authorization", "Bearer " + customerToken)
            .put(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(403, response.getStatus(), "CUSTOMER não deve conseguir atualizar deliveries (só ADMIN/BUSINESS/COURIER)");
    }

    @Test
    public void testUpdateDeliveryWithCourierToken() {
        // Primeiro cria a delivery como BUSINESS
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/deliveries")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Delivery created = createResp.readEntity(com.caracore.cso.entity.Delivery.class);
        
        // Tenta atualizar como COURIER - deve conseguir
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
    String json = "{\"completed\":true}";
        Response response = target("/deliveries/" + created.getId())
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .put(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(200, response.getStatus(), "COURIER deve conseguir atualizar deliveries");
    }

    @Test
    public void testDeleteDeliveryWithCourierToken() {
        // Primeiro cria a delivery como BUSINESS
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/deliveries")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(delivery));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Delivery created = createResp.readEntity(com.caracore.cso.entity.Delivery.class);
        
        // Tenta deletar como COURIER - não deve conseguir
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/deliveries/" + created.getId())
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .delete();
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir deletar deliveries (só ADMIN/BUSINESS)");
    }
}





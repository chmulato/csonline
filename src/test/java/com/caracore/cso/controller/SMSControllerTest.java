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

public class SMSControllerTest extends BaseControllerJerseyTest {
    private com.caracore.cso.entity.Delivery delivery;
    private com.caracore.cso.entity.SMS sms;

    @BeforeEach
    void setUpTestData() {
        jakarta.persistence.EntityManager em = TestJPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
        // Cria dados únicos usando TestDataFactory
        com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
        com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
        com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
        // Persiste business e courier
        new com.caracore.cso.service.UserService().save(business);
        new com.caracore.cso.service.UserService().save(courierUser);
        new com.caracore.cso.service.CourierService().save(courier);
        // Persiste delivery
        delivery = TestDataFactory.createDelivery(business, courier);
        new com.caracore.cso.service.DeliveryService().save(delivery);
        // Busca delivery persistida para garantir ID
        java.util.List<com.caracore.cso.entity.Delivery> deliveries = new com.caracore.cso.service.DeliveryService().findAll();
        if (!deliveries.isEmpty()) delivery = deliveries.get(0);
        sms = TestDataFactory.createSMS(delivery);
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(SMSController.class)
            .register(JwtAuthenticationFilter.class)  // Registra filtro de autenticação
            .register(AuthorizationFilter.class)      // Registra filtro de autorização
            .register(com.caracore.cso.service.SMSService.class)
            .register(com.caracore.cso.service.DeliveryService.class);
    }

    @Test
    public void testGetAllSMS() {
        // Garante que há pelo menos um SMS
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        target("/sms").request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(sms));
        Response response = target("/sms").request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetSMSById() {
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response createResp = target("/sms").request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.SMS created = createResp.readEntity(com.caracore.cso.entity.SMS.class);
        Response response = target("/sms/" + created.getId()).request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetSMSByDelivery() {
        // Primeiro verificamos se o delivery tem um ID válido
        assertNotNull(delivery);
        assertNotNull(delivery.getId());
        
        // Configuramos explicitamente o ID no SMS
        sms.setDelivery(delivery);
        
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response createResp = target("/sms").request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(201, createResp.getStatus());
        
        com.caracore.cso.entity.SMS created = createResp.readEntity(com.caracore.cso.entity.SMS.class);
        
        // Verificamos se o SMS criado tem um delivery com ID
        assertNotNull(created);
        assertNotNull(created.getDeliveryId());
        
        String adminToken2 = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response response = target("/sms/delivery/" + delivery.getId()).request()
            .header("Authorization", "Bearer " + adminToken2)
            .get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateSMS() {
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response response = target("/sms").request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(201, response.getStatus());

        // Tenta criar outro SMS igual (mesmo delivery, type e piece)
        com.caracore.cso.entity.SMS smsDuplicado = new com.caracore.cso.entity.SMS();
        smsDuplicado.setDelivery(sms.getDelivery());
        smsDuplicado.setType(sms.getType());
        smsDuplicado.setPiece(sms.getPiece());
        smsDuplicado.setMobileFrom(sms.getMobileFrom());
        smsDuplicado.setMobileTo(sms.getMobileTo());
        smsDuplicado.setMessage("Mensagem duplicada");
        smsDuplicado.setDatetime(sms.getDatetime());
        Response resp2 = target("/sms").request()
            .header("Authorization", "Bearer " + adminToken)
            .post(jakarta.ws.rs.client.Entity.json(smsDuplicado));
        assertEquals(409, resp2.getStatus());
        String msg = resp2.readEntity(String.class).toLowerCase();
        assertTrue(msg.contains("entrega") || msg.contains("piece") || msg.contains("tipo") || msg.contains("existe"));
    }

    // ========== TESTES DE AUTORIZAÇÃO ==========

    @Test
    public void testGetAllSMSWithoutToken() {
        Response response = target("/sms").request().get();
        assertEquals(401, response.getStatus(), "Acesso sem token deve retornar 401 Unauthorized");
    }

    @Test
    public void testGetAllSMSWithAdminToken() {
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response response = target("/sms")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        assertEquals(200, response.getStatus(), "ADMIN deve conseguir listar SMS");
    }

    @Test
    public void testGetAllSMSWithBusinessToken() {
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response response = target("/sms")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .get();
        assertEquals(200, response.getStatus(), "BUSINESS deve conseguir listar SMS");
    }

    @Test
    public void testGetAllSMSWithCourierToken() {
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/sms")
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .get();
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir listar SMS (só ADMIN/BUSINESS)");
    }

    @Test
    public void testCreateSMSWithCourierToken() {
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/sms")
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir criar SMS (só ADMIN/BUSINESS)");
    }

    @Test
    public void testCreateSMSWithBusinessToken() {
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response response = target("/sms")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(201, response.getStatus(), "BUSINESS deve conseguir criar SMS");
    }

    @Test
    public void testSendSMSWithCustomerToken() {
        // Primeiro cria o SMS como BUSINESS
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/sms")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.SMS created = createResp.readEntity(com.caracore.cso.entity.SMS.class);
        
        // Tenta enviar como CUSTOMER - não deve conseguir
        String customerToken = JwtUtil.generateToken("customer_test", "CUSTOMER", 4L);
        Response response = target("/sms/" + created.getId() + "/send")
            .request()
            .header("Authorization", "Bearer " + customerToken)
            .post(jakarta.ws.rs.client.Entity.text(""));
        assertEquals(403, response.getStatus(), "CUSTOMER não deve conseguir enviar SMS (só ADMIN/BUSINESS)");
    }

    @Test
    public void testSendSMSWithBusinessToken() {
        // Primeiro cria o SMS como BUSINESS
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/sms")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.SMS created = createResp.readEntity(com.caracore.cso.entity.SMS.class);
        
        // Tenta enviar como BUSINESS - deve conseguir
        Response response = target("/sms/" + created.getId() + "/send")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.text(""));
        assertEquals(200, response.getStatus(), "BUSINESS deve conseguir enviar SMS");
    }
}



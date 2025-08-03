package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

import com.caracore.cso.util.TestDatabaseUtil;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.repository.JPAUtil;
import org.junit.jupiter.api.BeforeEach;

public class SMSControllerTest extends JerseyTest {
    private com.caracore.cso.entity.Delivery delivery;
    private com.caracore.cso.entity.SMS sms;

    @BeforeEach
    void setUpTestData() {
        jakarta.persistence.EntityManager em = JPAUtil.getEntityManager();
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
            .register(com.caracore.cso.service.SMSService.class)
            .register(com.caracore.cso.service.DeliveryService.class);
    }

    @Test
    public void testGetAllSMS() {
        // Garante que há pelo menos um SMS
        target("/sms").request().post(jakarta.ws.rs.client.Entity.json(sms));
        Response response = target("/sms").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetSMSById() {
        Response createResp = target("/sms").request().post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.SMS created = createResp.readEntity(com.caracore.cso.entity.SMS.class);
        Response response = target("/sms/" + created.getId()).request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetSMSByDelivery() {
        // Primeiro verificamos se o delivery tem um ID válido
        assertNotNull(delivery);
        assertNotNull(delivery.getId());
        
        // Configuramos explicitamente o ID no SMS
        sms.setDelivery(delivery);
        
        Response createResp = target("/sms").request().post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(201, createResp.getStatus());
        
        com.caracore.cso.entity.SMS created = createResp.readEntity(com.caracore.cso.entity.SMS.class);
        
        // Verificamos se o SMS criado tem um delivery com ID
        assertNotNull(created);
        assertNotNull(created.getDeliveryId());
        
        Response response = target("/sms/delivery/" + delivery.getId()).request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateSMS() {
        Response response = target("/sms").request().post(jakarta.ws.rs.client.Entity.json(sms));
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
        Response resp2 = target("/sms").request().post(jakarta.ws.rs.client.Entity.json(smsDuplicado));
        assertEquals(409, resp2.getStatus());
        String msg = resp2.readEntity(String.class).toLowerCase();
        assertTrue(msg.contains("entrega") || msg.contains("piece") || msg.contains("tipo") || msg.contains("existe"));
    }
}

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
        var em = JPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
        // Cria dados únicos usando TestDataFactory
        var business = TestDataFactory.createUser("BUSINESS");
        var courierUser = TestDataFactory.createUser("COURIER");
        var courier = TestDataFactory.createCourier(business, courierUser);
        delivery = TestDataFactory.createDelivery(business, courier);
        sms = TestDataFactory.createSMS(delivery);
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(SMSController.class);
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
        Response createResp = target("/sms").request().post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.SMS created = createResp.readEntity(com.caracore.cso.entity.SMS.class);
        Response response = target("/sms/delivery/" + created.getDelivery().getId()).request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCreateSMS() {
        Response response = target("/sms").request().post(jakarta.ws.rs.client.Entity.json(sms));
        assertEquals(201, response.getStatus());
    }
}

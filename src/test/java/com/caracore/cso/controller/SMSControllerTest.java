package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SMSControllerTest extends JerseyTest {
    @BeforeEach
    void setUpTestData() {
        // Cria dados de SMS para os testes
        var smsService = new com.caracore.cso.service.SMSService();
        var allSms = smsService.findAll();
        for (var s : allSms) smsService.deleteById(s.getId());

        var deliveryService = new com.caracore.cso.service.DeliveryService();
        var deliveries = deliveryService.findAll();
        for (var d : deliveries) deliveryService.delete(d.getId());

        var delivery = new com.caracore.cso.entity.Delivery();
        delivery.setId(1L);
        delivery.setStart("Origem Teste");
        delivery.setDestination("Destino Teste");
        delivery.setContact("Contato Teste");
        delivery.setDescription("Descrição Teste");
        delivery.setVolume("1");
        delivery.setWeight("1");
        delivery.setKm("1");
        delivery.setAdditionalCost(0.0);
        delivery.setCost(0.0);
        delivery.setReceived(true);
        delivery.setCompleted(false);
        delivery.setDatatime(java.time.LocalDateTime.now());
        deliveryService.save(delivery);

        var sms = new com.caracore.cso.entity.SMS();
        sms.setId(1L);
        sms.setDelivery(delivery);
        sms.setPiece(1);
        sms.setType("S");
        sms.setMobileTo("11999999999");
        sms.setMobileFrom("11888888888");
        sms.setMessage("Teste WhatsApp");
        sms.setDatetime("2025-07-27T10:00:00");
        smsService.save(sms);
    }
    private static final Logger logger = LogManager.getLogger(SMSControllerTest.class);

    @Override
    protected Application configure() {
        return new ResourceConfig(SMSController.class);
    }

    @Test
    public void testGetAllSMS() {
        try {
            Response response = target("/sms").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetAllSMS", e);
            throw e;
        }
    }

    @Test
    public void testGetSMSById() {
        try {
            Response response = target("/sms/1").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetSMSById", e);
            throw e;
        }
    }

    @Test
    public void testGetSMSByDelivery() {
        try {
            Response response = target("/sms/delivery/1").request().get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetSMSByDelivery", e);
            throw e;
        }
    }

    @Test
    public void testCreateSMS() {
        try {
            String json = "{\"message\":\"Teste WhatsApp\",\"mobileTo\":\"11999999999\",\"mobileFrom\":\"11888888888\"}";
            Response response = target("/sms").request().post(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(201, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testCreateSMS", e);
            throw e;
        }
    }
}

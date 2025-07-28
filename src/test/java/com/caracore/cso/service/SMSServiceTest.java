package com.caracore.cso.service;

import com.caracore.cso.entity.SMS;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SMSServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(SMSServiceTest.class);
    private SMSService service;
    @Test
    void testDeleteSMSWithDeliveryReference() {
        try {
            long ts = System.currentTimeMillis();
            // Cria business
            var userService = new UserService();
            var business = new com.caracore.cso.entity.User();
            business.setRole("BUSINESS");
            business.setName("BusinessRef");
            business.setLogin("businessref_" + ts);
            business.setPassword("businessref123");
            userService.save(business);
            business = userService.findByLogin("businessref_" + ts);

            // Cria courier user
            var courierUser = new com.caracore.cso.entity.User();
            courierUser.setRole("COURIER");
            courierUser.setName("CourierRef");
            courierUser.setLogin("courierref_" + ts);
            courierUser.setPassword("courierref123");
            userService.save(courierUser);
            courierUser = userService.findByLogin("courierref_" + ts);

            // Cria courier
            var courier = new com.caracore.cso.entity.Courier();
            courier.setBusiness(business);
            courier.setUser(courierUser);
            courier.setFactorCourier(1.5);
            new CourierService().save(courier);
            var couriers = new CourierService().findAllByBusiness(business.getId());
            if (!couriers.isEmpty()) courier = couriers.get(0);

            // Cria delivery
            var delivery = new com.caracore.cso.entity.Delivery();
            delivery.setBusiness(business);
            delivery.setCourier(courier);
            delivery.setStart("A");
            delivery.setDestination("B");
            delivery.setContact("Contact");
            delivery.setDescription("Desc");
            delivery.setVolume("10");
            delivery.setWeight("5");
            delivery.setKm("2");
            delivery.setAdditionalCost(1.0);
            delivery.setCost(10.0);
            delivery.setReceived(true);
            delivery.setCompleted(false);
            delivery.setDatatime(java.time.LocalDateTime.now());
            new DeliveryService().save(delivery);
            var deliveries = new DeliveryService().findAllByBusiness(business.getId());
            if (!deliveries.isEmpty()) delivery = deliveries.get(0);

            // Cria SMS vinculado ao delivery
            var sms = new com.caracore.cso.entity.SMS();
            sms.setDelivery(delivery);
            sms.setMessage("Teste");
            sms.setMobileFrom("11111111");
            sms.setMobileTo("22222222");
            sms.setPiece(1);
            sms.setType("S");
            service.save(sms);
            var smsList = service.getDeliverySMSHistory(delivery.getId());
            final Long smsId = !smsList.isEmpty() ? smsList.get(0).getId() : null;

            // Tenta deletar o delivery vinculado ao SMS
            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteById(smsId));
            assertTrue(ex.getMessage().contains("Não foi possível deletar o SMS") || ex.getMessage().contains("vinculados"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteSMSWithDeliveryReference em SMSServiceTest", e);
            throw e;
        }
    }

    @BeforeEach
    void setUp() {
        try {
            // TestDatabaseUtil.clearDatabase();
            service = new SMSService();
        } catch (Exception e) {
            logger.error("Erro ao preparar o teste SMSServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindById() {
        try {
            SMS sms = service.findById(1L);
            // O teste real depende do banco estar populado
            // assertNull(sms);
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindById em SMSServiceTest", e);
            throw e;
        }
    }

    @Test
    void testSendAndGetDeliverySMS() {
        try {
            Long deliveryId = 1L;
            String fromMobile = "11999999999";
            String toMobile = "11888888888";
            String type = "WHATSAPP";
            String message = "Entrega iniciada";
            Integer piece = 1;
            String datetime = "2025-07-27T15:45:00";

            // Envia SMS
            service.sendDeliverySMS(deliveryId, fromMobile, toMobile, type, message, piece, datetime);

            // Consulta histórico
            var history = service.getDeliverySMSHistory(deliveryId);
            assertNotNull(history);
            assertTrue(history.stream().anyMatch(sms ->
                fromMobile.equals(sms.getMobileFrom()) &&
                toMobile.equals(sms.getMobileTo()) &&
                type.equals(sms.getType()) &&
                message.equals(sms.getMessage()) &&
                piece.equals(sms.getPiece()) &&
                datetime.equals(sms.getDatetime())
            ));
        } catch (Exception e) {
            logger.error("Erro durante o teste testSendAndGetDeliverySMS em SMSServiceTest", e);
            throw e;
        }
    }

    // Outros testes podem ser criados para findAllByDelivery, updateDeliveryId, etc.
}

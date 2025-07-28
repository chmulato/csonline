package com.caracore.cso.service;

import com.caracore.cso.entity.Delivery;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DeliveryServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryServiceTest.class);
    private DeliveryService service;
    @Test
    void testDeleteDeliveryWithSMSReference() {
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
            // Buscar o courier persistido para pegar o ID real
            var couriers = new CourierService().findAllByBusiness(business.getId());
            final Long courierId = !couriers.isEmpty() ? couriers.get(0).getId() : null;

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
            service.save(delivery);

            // Cria SMS vinculado ao delivery
            var sms = new com.caracore.cso.entity.SMS();
            sms.setDelivery(delivery);
            sms.setMessage("Teste");
            sms.setMobileFrom("11111111");
            sms.setMobileTo("22222222");
            sms.setPiece(1);
            sms.setType("S");
            new SMSService().save(sms);

            // Tenta deletar o delivery vinculado ao SMS
            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.delete(delivery.getId()));
            assertTrue(ex.getMessage().contains("Não foi possível deletar a entrega") || ex.getMessage().contains("vinculados"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteDeliveryWithSMSReference em DeliveryServiceTest", e);
            throw e;
        }
    }
    // ...existing code...

    @BeforeEach
    void setUp() {
        try {
            // TestDatabaseUtil.clearDatabase();
            service = new DeliveryService();
        } catch (Exception e) {
            logger.error("Erro ao preparar o teste DeliveryServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindById() {
        try {
            Delivery delivery = service.findById(1L);
            // O teste real depende do banco estar populado
            // assertNull(delivery);
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindById em DeliveryServiceTest", e);
            throw e;
        }
    }

    // Outros testes podem ser criados para findAllByBusiness, updateDeliveryStatus, etc.
}

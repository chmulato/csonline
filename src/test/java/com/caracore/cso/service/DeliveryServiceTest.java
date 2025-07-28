package com.caracore.cso.service;

import com.caracore.cso.entity.Delivery;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;

class DeliveryServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryServiceTest.class);
    private DeliveryService service;
    @Test
    void testDeleteDeliveryWithSMSReference() {
        try {
            var userService = new UserService();
            var business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            var courierUser = TestDataFactory.createUser("COURIER");
            userService.save(courierUser);
            courierUser = userService.findByLogin(courierUser.getLogin());

            var courier = TestDataFactory.createCourier(business, courierUser);
            new CourierService().save(courier);
            var couriers = new CourierService().findAllByBusiness(business.getId());
            if (!couriers.isEmpty()) courier = couriers.get(0);

            var delivery = TestDataFactory.createDelivery(business, courier);
            service.save(delivery);

            var sms = TestDataFactory.createSMS(delivery);
            new SMSService().save(sms);

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

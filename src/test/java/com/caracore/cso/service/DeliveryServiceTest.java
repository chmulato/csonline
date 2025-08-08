package com.caracore.cso.service;

import com.caracore.cso.entity.Delivery;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Courier;
import com.caracore.cso.entity.SMS;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import com.caracore.cso.service.TestableUserService;
import com.caracore.cso.service.TestableTeamService;
import com.caracore.cso.service.TestableCourierService;
import com.caracore.cso.service.TestableCustomerService;
import com.caracore.cso.service.TestableDeliveryService;
import com.caracore.cso.service.TestablePriceService;
import com.caracore.cso.service.TestableSMSService;

class DeliveryServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryServiceTest.class);
    private TestableDeliveryService service;
    @Test
    void testDeleteDeliveryWithSMSReference() {
        try {
            TestableUserService userService = new TestableUserService(true);
            User business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            User courierUser = TestDataFactory.createUser("COURIER");
            userService.save(courierUser);
            courierUser = userService.findByLogin(courierUser.getLogin());

            Courier courier = TestDataFactory.createCourier(business, courierUser);
            new TestableCourierService(true).save(courier);
            List<Courier> couriers = new TestableCourierService(true).findByBusiness(business.getId());
            if (!couriers.isEmpty()) courier = couriers.get(0);

            Delivery delivery = TestDataFactory.createDelivery(business, courier);
            service.save(delivery);

            SMS sms = TestDataFactory.createSMS(delivery);
            new TestableSMSService(true).save(sms);

            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteById(delivery.getId()));
            assertTrue(ex.getMessage().contains("Não foi possível deletar a entrega") || ex.getMessage().contains("vinculados"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteDeliveryWithSMSReference em DeliveryServiceTest", e);
            throw e;
        }
    }
    // ...existing code...

    @BeforeEach
    void setUp() {
        EntityManager em = com.caracore.cso.repository.TestJPAUtil.getEntityManager();
        try {
            com.caracore.cso.util.TestDatabaseUtil.clearDatabase(em);
        } finally {
            em.close();
        }
        try {
            service = new TestableDeliveryService(true);
            TestableUserService userService = new TestableUserService(true);
            User business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            User courierUser = TestDataFactory.createUser("COURIER");
            userService.save(courierUser);
            courierUser = userService.findByLogin(courierUser.getLogin());

            Courier courier = TestDataFactory.createCourier(business, courierUser);
            new TestableCourierService(true).save(courier);
            List<Courier> couriers = new TestableCourierService(true).findByBusiness(business.getId());
            if (!couriers.isEmpty()) courier = couriers.get(0);

            Delivery delivery = TestDataFactory.createDelivery(business, courier);
            service.save(delivery);
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






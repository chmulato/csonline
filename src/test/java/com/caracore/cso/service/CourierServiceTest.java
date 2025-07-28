
package com.caracore.cso.service;

import com.caracore.cso.entity.Courier;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDatabaseUtil;
import com.caracore.cso.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import com.caracore.cso.util.TestDataFactory;

class CourierServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(CourierServiceTest.class);
    private CourierService service;

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TestDatabaseUtil.clearDatabase(em);
        } finally {
            em.close();
        }
    }
    @Test
    void testDeleteCourierWithDeliveryReference() {
        try {
            var userService = new UserService();
            var business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            var courierUser = TestDataFactory.createUser("COURIER");
            userService.save(courierUser);
            courierUser = userService.findByLogin(courierUser.getLogin());

            var courier = TestDataFactory.createCourier(business, courierUser);
            service.save(courier);
            var couriers = service.findAllByBusiness(business.getId());
            final Long courierId = !couriers.isEmpty() ? couriers.get(0).getId() : null;

            var delivery = TestDataFactory.createDelivery(business, courier);
            new DeliveryService().save(delivery);

            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.delete(courierId));
            assertTrue(ex.getMessage().contains("Não foi possível deletar o entregador") || ex.getMessage().contains("vínculos"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteCourierWithDeliveryReference", e);
            throw e;
        }
    }
    // ...existing code...

    @BeforeEach
    void setUp() {
        try {
            service = new CourierService();
            var userService = new UserService();

            var business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            var courierUser = TestDataFactory.createUser("COURIER");
            userService.save(courierUser);
            courierUser = userService.findByLogin(courierUser.getLogin());

            var courier = TestDataFactory.createCourier(business, courierUser);
            service.save(courier);
            var couriers = service.findAllByBusiness(business.getId());
            if (!couriers.isEmpty()) courier = couriers.get(0);

            var deliveryService = new DeliveryService();
            var delivery = TestDataFactory.createDelivery(business, courier);
            deliveryService.save(delivery);
            // Store IDs for use in tests
            System.setProperty("test.courier.id", courier.getId().toString());
            System.setProperty("test.business.id", business.getId().toString());
        } catch (Exception e) {
            logger.error("Erro ao preparar o teste CourierServiceTest", e);
            throw e;
        }
    }


    @Test
    void debugEntities() {
        Long courierId = Long.valueOf(System.getProperty("test.courier.id", "1"));
        var courier = service.findById(courierId);
        System.out.println("Courier id=" + courierId + ": " + courier);
        if (courier != null) {
            System.out.println("Courier.user: " + courier.getUser());
        }
        var deliveryService = new DeliveryService();
        var delivery = deliveryService.findAll().stream().findFirst().orElse(null);
        System.out.println("Delivery: " + delivery);
        if (delivery != null) {
            System.out.println("Delivery.courier: " + delivery.getCourier());
            if (delivery.getCourier() != null) {
                System.out.println("Delivery.courier.user: " + delivery.getCourier().getUser());
            }
        }
        // Não faz assert, apenas imprime para depuração
    }

    @Test
    void testFindById() {
        Long courierId = Long.valueOf(System.getProperty("test.courier.id", "1"));
        Courier courier = service.findById(courierId);
        assertNotNull(courier);
        assertEquals(courierId, courier.getId());
        assertEquals(1.2, courier.getFactorCourier());
    }

    @Test
    void testFindAllByBusiness() {
        Long businessId = Long.valueOf(System.getProperty("test.business.id", "1"));
        var couriers = service.findAllByBusiness(businessId);
        assertNotNull(couriers);
        assertTrue(couriers.size() >= 1);
    }

    @Test
    void testUpdateFactor() {
        Long courierId = Long.valueOf(System.getProperty("test.courier.id", "1"));
        service.updateFactor(courierId, 2.5);
        Courier courier = service.findById(courierId);
        assertEquals(2.5, courier.getFactorCourier());
        // Restaura valor original
        service.updateFactor(courierId, 1.2);
    }

    @Test
    void testCanAccessDelivery() {
        var deliveryService = new DeliveryService();
        var delivery = deliveryService.findAll().stream().findFirst().orElse(null);
        assertNotNull(delivery);
        var courier = delivery.getCourier();
        assertNotNull(courier);
        var courierUser = courier.getUser();
        assertNotNull(courierUser);
        boolean canAccess = service.canAccessDelivery(courierUser, delivery);
        assertTrue(canAccess);
    }
}

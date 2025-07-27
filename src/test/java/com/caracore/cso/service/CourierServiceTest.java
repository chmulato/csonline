
package com.caracore.cso.service;

import com.caracore.cso.entity.Courier;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CourierServiceTest {
    private CourierService service;

    @BeforeEach
    void setUp() {
        service = new CourierService();
    }


    @Test
    void debugEntities() {
        var courier = service.findById(1L);
        System.out.println("Courier id=1: " + courier);
        if (courier != null) {
            System.out.println("Courier.user: " + courier.getUser());
        }
        var deliveryService = new DeliveryService();
        var delivery = deliveryService.findById(1L);
        System.out.println("Delivery id=1: " + delivery);
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
        Courier courier = service.findById(1L);
        assertNotNull(courier);
        assertEquals(1L, courier.getId());
        assertEquals(1.2, courier.getFactorCourier());
    }

    @Test
    void testFindAllByBusiness() {
        var couriers = service.findAllByBusiness(2L);
        assertNotNull(couriers);
        assertTrue(couriers.size() >= 2);
    }

    @Test
    void testUpdateFactor() {
        service.updateFactor(1L, 2.5);
        Courier courier = service.findById(1L);
        assertEquals(2.5, courier.getFactorCourier());
        // Restaura valor original
        service.updateFactor(1L, 1.2);
    }

    @Test
    void testCanAccessDelivery() {
        var deliveryService = new DeliveryService();
        var delivery = deliveryService.findById(1L);
        assertNotNull(delivery);
        var courier = delivery.getCourier();
        assertNotNull(courier);
        var courierUser = courier.getUser();
        assertNotNull(courierUser);
        boolean canAccess = service.canAccessDelivery(courierUser, delivery);
        assertTrue(canAccess);
    }
}

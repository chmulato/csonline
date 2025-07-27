
package com.caracore.cso.service;

import com.caracore.cso.entity.Courier;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CourierServiceTest {
    private CourierService service;

    @BeforeEach
    void setUp() {
        service = new CourierService();

        // Limpa e insere dados necessários para os testes
        // Cria business
        var userService = new UserService();
        java.util.List<com.caracore.cso.entity.User> users = userService.findAll();
        for (com.caracore.cso.entity.User u : users) userService.delete(u.getId());

        var business = new com.caracore.cso.entity.User();
        business.setId(2L);
        business.setRole("BUSINESS");
        business.setName("Business");
        business.setLogin("business");
        business.setPassword("business123");
        userService.save(business);

        var courierUser = new com.caracore.cso.entity.User();
        courierUser.setId(1L);
        courierUser.setRole("COURIER");
        courierUser.setName("Courier");
        courierUser.setLogin("courier");
        courierUser.setPassword("courier123");
        userService.save(courierUser);

        var courier = new Courier();
        courier.setId(1L);
        courier.setBusiness(business);
        courier.setUser(courierUser);
        courier.setFactorCourier(1.2);
        service.save(courier);

        // Cria Delivery para teste de canAccessDelivery
        var deliveryService = new DeliveryService();
        java.util.List<com.caracore.cso.entity.Delivery> deliveries = deliveryService.findAll();
        for (com.caracore.cso.entity.Delivery d : deliveries) deliveryService.delete(d.getId());

        var delivery = new com.caracore.cso.entity.Delivery();
        delivery.setId(1L);
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
        deliveryService.save(delivery);
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

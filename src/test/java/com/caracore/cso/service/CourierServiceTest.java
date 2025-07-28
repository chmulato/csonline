
package com.caracore.cso.service;

import com.caracore.cso.entity.Courier;
import com.caracore.cso.service.CourierService;
import com.caracore.cso.service.UserService;
import com.caracore.cso.service.DeliveryService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CourierServiceTest {
    @Test
    void testDeleteCourierWithDeliveryReference() {
        // Cria business
        var userService = new UserService();
        var business = new com.caracore.cso.entity.User();
        business.setRole("BUSINESS");
        business.setName("BusinessRef");
        business.setLogin("businessref");
        business.setPassword("businessref123");
        userService.save(business);
        business = userService.findByLogin("businessref");

        // Cria courier user
        var courierUser = new com.caracore.cso.entity.User();
        courierUser.setRole("COURIER");
        courierUser.setName("CourierRef");
        courierUser.setLogin("courierref");
        courierUser.setPassword("courierref123");
        userService.save(courierUser);
        courierUser = userService.findByLogin("courierref");

        // Cria courier
        var courier = new com.caracore.cso.entity.Courier();
        courier.setBusiness(business);
        courier.setUser(courierUser);
        courier.setFactorCourier(1.5);
        service.save(courier);
        // Buscar o courier persistido para pegar o ID real
        var couriers = service.findAllByBusiness(business.getId());
        final Long courierId = !couriers.isEmpty() ? couriers.get(0).getId() : null;

        // Cria delivery vinculado ao courier
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

        // Tenta deletar o courier vinculado ao delivery
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.delete(courierId));
        assertTrue(ex.getMessage().contains("Não foi possível deletar o entregador") || ex.getMessage().contains("vínculos"));
    }
    private CourierService service;

    @BeforeEach
    void setUp() {
        service = new CourierService();

        // Limpa tabelas manualmente para evitar conflitos de chave primária
        var em = com.caracore.cso.repository.JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM sms").executeUpdate();
        em.createNativeQuery("DELETE FROM delivery").executeUpdate();
        em.createNativeQuery("DELETE FROM team").executeUpdate();
        em.createNativeQuery("DELETE FROM customer").executeUpdate();
        em.createNativeQuery("DELETE FROM price").executeUpdate();
        em.createNativeQuery("DELETE FROM app_user").executeUpdate();
        // Reset sequence for app_user.id (H2 specific)
        em.createNativeQuery("ALTER TABLE app_user ALTER COLUMN id RESTART WITH 100").executeUpdate();
        em.getTransaction().commit();
        em.close();

        // Cria business com login único para evitar conflito com import.sql
        var userService = new UserService();

        var business = new com.caracore.cso.entity.User();
        business.setRole("BUSINESS");
        business.setName("Test Business");
        business.setLogin("test_business");
        business.setPassword("test_business123");
        userService.save(business);
        business = userService.findByLogin("test_business");

        var courierUser = new com.caracore.cso.entity.User();
        courierUser.setRole("COURIER");
        courierUser.setName("Test Courier");
        courierUser.setLogin("test_courier");
        courierUser.setPassword("test_courier123");
        userService.save(courierUser);
        courierUser = userService.findByLogin("test_courier");

        var courier = new Courier();
        courier.setBusiness(business);
        courier.setUser(courierUser);
        courier.setFactorCourier(1.2);
        service.save(courier);
        // Buscar o courier persistido para pegar o ID real
        var couriers = service.findAllByBusiness(business.getId());
        if (!couriers.isEmpty()) courier = couriers.get(0);

        // Cria Delivery para teste de canAccessDelivery
        var deliveryService = new DeliveryService();
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

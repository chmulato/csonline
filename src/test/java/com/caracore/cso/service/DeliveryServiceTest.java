package com.caracore.cso.service;

import com.caracore.cso.entity.Delivery;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryServiceTest {
    @Test
    void testDeleteDeliveryWithSMSReference() {
        // Cria business
        var userService = new UserService();
        var business = new com.caracore.cso.entity.User();
        business.setId(20L);
        business.setRole("BUSINESS");
        business.setName("BusinessRef");
        business.setLogin("businessref");
        business.setPassword("businessref123");
        userService.save(business);

        // Cria courier user
        var courierUser = new com.caracore.cso.entity.User();
        courierUser.setId(21L);
        courierUser.setRole("COURIER");
        courierUser.setName("CourierRef");
        courierUser.setLogin("courierref");
        courierUser.setPassword("courierref123");
        userService.save(courierUser);

        // Cria courier
        var courier = new com.caracore.cso.entity.Courier();
        courier.setId(22L);
        courier.setBusiness(business);
        courier.setUser(courierUser);
        courier.setFactorCourier(1.5);
        new CourierService().save(courier);

        // Cria delivery
        var delivery = new com.caracore.cso.entity.Delivery();
        delivery.setId(23L);
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
        sms.setId(24L);
        sms.setDelivery(delivery);
        sms.setMessage("Teste");
        sms.setMobileFrom("11111111");
        sms.setMobileTo("22222222");
        sms.setPiece(1);
        sms.setType("S");
        new SMSService().save(sms);

        // Tenta deletar o delivery vinculado ao SMS
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.delete(23L));
        assertTrue(ex.getMessage().contains("Não foi possível deletar a entrega") || ex.getMessage().contains("vinculados"));
    }
    private DeliveryService service;

    @BeforeEach
    void setUp() {
        service = new DeliveryService();
    }

    @Test
    void testFindById() {
        Delivery delivery = service.findById(1L);
        // O teste real depende do banco estar populado
        // assertNull(delivery);
    }

    // Outros testes podem ser criados para findAllByBusiness, updateDeliveryStatus, etc.
}

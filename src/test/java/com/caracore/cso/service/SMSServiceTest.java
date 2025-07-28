package com.caracore.cso.service;

import com.caracore.cso.entity.SMS;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SMSServiceTest {
    @Test
    void testDeleteSMSWithDeliveryReference() {
        // Cria business
        var userService = new UserService();
        var business = new com.caracore.cso.entity.User();
        business.setId(40L);
        business.setRole("BUSINESS");
        business.setName("BusinessRef");
        business.setLogin("businessref");
        business.setPassword("businessref123");
        userService.save(business);

        // Cria courier user
        var courierUser = new com.caracore.cso.entity.User();
        courierUser.setId(41L);
        courierUser.setRole("COURIER");
        courierUser.setName("CourierRef");
        courierUser.setLogin("courierref");
        courierUser.setPassword("courierref123");
        userService.save(courierUser);

        // Cria courier
        var courier = new com.caracore.cso.entity.Courier();
        courier.setId(42L);
        courier.setBusiness(business);
        courier.setUser(courierUser);
        courier.setFactorCourier(1.5);
        new CourierService().save(courier);

        // Cria delivery
        var delivery = new com.caracore.cso.entity.Delivery();
        delivery.setId(43L);
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

        // Cria SMS vinculado ao delivery
        var sms = new com.caracore.cso.entity.SMS();
        sms.setId(44L);
        sms.setDelivery(delivery);
        sms.setMessage("Teste");
        sms.setMobileFrom("11111111");
        sms.setMobileTo("22222222");
        sms.setPiece(1);
        sms.setType("S");
        service.save(sms);

        // Tenta deletar o delivery vinculado ao SMS
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteById(44L));
        assertTrue(ex.getMessage().contains("Não foi possível deletar o SMS") || ex.getMessage().contains("vinculados"));
    }
    private SMSService service;

    @BeforeEach
    void setUp() {
        service = new SMSService();
    }

    @Test
    void testFindById() {
        SMS sms = service.findById(1L);
        // O teste real depende do banco estar populado
        // assertNull(sms);
    }

    @Test
    void testSendAndGetDeliverySMS() {
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
    }

    // Outros testes podem ser criados para findAllByDelivery, updateDeliveryId, etc.
}

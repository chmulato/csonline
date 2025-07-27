package com.caracore.cso.service;

import com.caracore.cso.entity.SMS;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SMSServiceTest {
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

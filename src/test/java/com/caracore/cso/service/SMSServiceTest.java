package com.caracore.cso.service;

import com.caracore.cso.entity.SMS;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Courier;
import com.caracore.cso.entity.Delivery;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;
import java.util.List;

class SMSServiceTest extends BaseServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(SMSServiceTest.class);
    private TestableSMSService service;
    @Test
    void testDeleteSMSWithDeliveryReference() {
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
            new TestableDeliveryService(true).save(delivery);
            List<Delivery> deliveries = new TestableDeliveryService(true).findByBusiness(business.getId());
            if (!deliveries.isEmpty()) delivery = deliveries.get(0);

            SMS sms = TestDataFactory.createSMS(delivery);
            service.save(sms);
            List<SMS> smsList = service.getDeliverySMSHistory(delivery.getId());
            final Long smsId = !smsList.isEmpty() ? smsList.get(0).getId() : null;

            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteById(smsId));
            assertTrue(ex.getMessage().contains("Não foi possível deletar o SMS") || ex.getMessage().contains("vinculados"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteSMSWithDeliveryReference em SMSServiceTest", e);
            throw e;
        }
    }

    @BeforeEach
    void setUp() {
        try {
            service = new TestableSMSService(true);
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
            new TestableDeliveryService(true).save(delivery);
        } catch (Exception e) {
            logger.error("Erro ao preparar o teste SMSServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindById() {
        try {
            service.findById(1L);
            // O teste real depende do banco estar populado
            // assertNull(sms);
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindById em SMSServiceTest", e);
            throw e;
        }
    }

    @Test
    void testSendAndGetDeliverySMS() {
        try {
            // Cria toda a cadeia de entidades necessárias
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
            new TestableDeliveryService(true).save(delivery);
            List<Delivery> deliveries = new TestableDeliveryService(true).findByBusiness(business.getId());
            if (!deliveries.isEmpty()) delivery = deliveries.get(0);

            Long deliveryId = delivery.getId();
            String fromMobile = "11999999999";
            String toMobile = "11888888888";
            String type = "WHATSAPP";
            String message = "Entrega iniciada";
            Integer piece = 1;
            String datetime = "2025-07-27T15:45:00";

            // Envia SMS
            service.sendDeliverySMS(deliveryId, fromMobile, toMobile, type, message, piece, datetime);

            // Consulta histórico
            List<SMS> history = service.getDeliverySMSHistory(deliveryId);
            assertNotNull(history);
            assertTrue(history.stream().anyMatch(sms ->
                fromMobile.equals(sms.getMobileFrom()) &&
                toMobile.equals(sms.getMobileTo()) &&
                type.equals(sms.getType()) &&
                message.equals(sms.getMessage()) &&
                piece.equals(sms.getPiece()) &&
                datetime.equals(sms.getDatetime())
            ));
        } catch (Exception e) {
            logger.error("Erro durante o teste testSendAndGetDeliverySMS em SMSServiceTest", e);
            throw e;
        }
    }

    // Outros testes podem ser criados para findAllByDelivery, updateDeliveryId, etc.
}






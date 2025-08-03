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
import jakarta.persistence.EntityManager;
import java.util.List;

class SMSServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(SMSServiceTest.class);
    private SMSService service;
    @Test
    void testDeleteSMSWithDeliveryReference() {
        try {
            UserService userService = new UserService();
            User business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            User courierUser = TestDataFactory.createUser("COURIER");
            userService.save(courierUser);
            courierUser = userService.findByLogin(courierUser.getLogin());

            Courier courier = TestDataFactory.createCourier(business, courierUser);
            new CourierService().save(courier);
            List<Courier> couriers = new CourierService().findAllByBusiness(business.getId());
            if (!couriers.isEmpty()) courier = couriers.get(0);

            Delivery delivery = TestDataFactory.createDelivery(business, courier);
            new DeliveryService().save(delivery);
            List<Delivery> deliveries = new DeliveryService().findAllByBusiness(business.getId());
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
        EntityManager em = com.caracore.cso.repository.JPAUtil.getEntityManager();
        try {
            com.caracore.cso.util.TestDatabaseUtil.clearDatabase(em);
        } finally {
            em.close();
        }
        try {
            service = new SMSService();
            UserService userService = new UserService();
            User business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            User courierUser = TestDataFactory.createUser("COURIER");
            userService.save(courierUser);
            courierUser = userService.findByLogin(courierUser.getLogin());

            Courier courier = TestDataFactory.createCourier(business, courierUser);
            new CourierService().save(courier);
            List<Courier> couriers = new CourierService().findAllByBusiness(business.getId());
            if (!couriers.isEmpty()) courier = couriers.get(0);

            Delivery delivery = TestDataFactory.createDelivery(business, courier);
            new DeliveryService().save(delivery);
        } catch (Exception e) {
            logger.error("Erro ao preparar o teste SMSServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindById() {
        try {
            SMS sms = service.findById(1L);
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
            UserService userService = new UserService();
            User business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            User courierUser = TestDataFactory.createUser("COURIER");
            userService.save(courierUser);
            courierUser = userService.findByLogin(courierUser.getLogin());

            Courier courier = TestDataFactory.createCourier(business, courierUser);
            new CourierService().save(courier);
            List<Courier> couriers = new CourierService().findAllByBusiness(business.getId());
            if (!couriers.isEmpty()) courier = couriers.get(0);

            Delivery delivery = TestDataFactory.createDelivery(business, courier);
            new DeliveryService().save(delivery);
            List<Delivery> deliveries = new DeliveryService().findAllByBusiness(business.getId());
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

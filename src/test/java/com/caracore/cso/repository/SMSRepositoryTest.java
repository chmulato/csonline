package com.caracore.cso.repository;

import com.caracore.cso.entity.SMS;
import com.caracore.cso.entity.Delivery;
import org.junit.jupiter.api.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import static org.junit.jupiter.api.Assertions.*;

class SMSRepositoryTest {
    private Session session;
    private Transaction tx;

    @BeforeEach
    void setUp() {
        session = HibernateUtil.getSessionFactory().openSession();
        tx = session.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        tx.rollback();
        session.close();
    }

    @Test
    void testCRUD() {
        Delivery delivery = new Delivery();
        delivery.setId(2L);
        delivery.setStart("X");
        delivery.setDestination("Y");
        delivery.setContact("Contato");
        delivery.setDescription("Mensagem");
        delivery.setVolume("1");
        delivery.setWeight("1");
        delivery.setKm("1");
        delivery.setAdditionalCost(0.0);
        delivery.setCost(0.0);
        delivery.setReceived(true);
        delivery.setCompleted(false);
        session.save(delivery);

        SMS sms = new SMS();
        sms.setId(1L);
        sms.setDelivery(delivery);
        sms.setPiece(1);
        sms.setType("S");
        sms.setMobileTo("99999999");
        sms.setMobileFrom("88888888");
        sms.setMessage("Teste");
        sms.setDatetime("2025-07-27T10:00:00");
        session.save(sms);
        session.flush();

        SMS found = session.get(SMS.class, 1L);
        assertNotNull(found);
        assertEquals("Teste", found.getMessage());

        found.setMessage("Alterado");
        session.update(found);
        session.flush();
        SMS updated = session.get(SMS.class, 1L);
        assertEquals("Alterado", updated.getMessage());

        session.delete(updated);
        session.flush();
        assertNull(session.get(SMS.class, 1L));
    }
}

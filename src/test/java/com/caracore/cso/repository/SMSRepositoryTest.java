package com.caracore.cso.repository;

import com.caracore.cso.entity.SMS;
import com.caracore.cso.entity.Delivery;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

class SMSRepositoryTest {
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeEach
    void setUp() {
        em = Persistence.createEntityManagerFactory("csonlinePU").createEntityManager();
        tx = em.getTransaction();
        tx.begin();
    }

    @AfterEach
    void tearDown() {
        if (tx.isActive()) {
            tx.rollback();
        }
        em.close();
    }

    @Test
    void testCRUD() {
        Delivery delivery = new Delivery();
        delivery.setId(140L);
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
        em.persist(delivery);

        SMS sms = new SMS();
        sms.setId(141L);
        sms.setDelivery(delivery);
        sms.setPiece(1);
        sms.setType("S");
        sms.setMobileTo("99999999");
        sms.setMobileFrom("88888888");
        sms.setMessage("Teste");
        sms.setDatetime("2025-07-27T10:00:00");
        em.persist(sms);
        em.flush();

        SMS found = em.find(SMS.class, 141L);
        assertNotNull(found);
        assertEquals("Teste", found.getMessage());

        found.setMessage("Alterado");
        em.merge(found);
        em.flush();
        SMS updated = em.find(SMS.class, 141L);
        assertEquals("Alterado", updated.getMessage());

        em.remove(updated);
        em.flush();
        assertNull(em.find(SMS.class, 141L));
    }
}

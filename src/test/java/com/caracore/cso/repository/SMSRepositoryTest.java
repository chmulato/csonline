
package com.caracore.cso.repository;
import com.caracore.cso.factory.DeliveryFactory;
import com.caracore.cso.factory.SMSFactory;

import com.caracore.cso.entity.SMS;
import com.caracore.cso.entity.Delivery;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SMSRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(SMSRepositoryTest.class);
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeEach
    void setUp() {
        try {
            em = Persistence.createEntityManagerFactory("csonlinePU").createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            // Limpa tabelas relevantes para garantir isolamento
            em.createQuery("DELETE FROM SMS").executeUpdate();
            em.createQuery("DELETE FROM Delivery").executeUpdate();
            em.createQuery("DELETE FROM Team").executeUpdate();
            em.createQuery("DELETE FROM Customer").executeUpdate();
            em.createQuery("DELETE FROM Courier").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            tx.commit();
            tx.begin();
        } catch (Exception e) {
            logger.error("Erro ao iniciar EntityManager ou transação", e);
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        } catch (Exception e) {
            logger.error("Erro ao finalizar transação ou fechar EntityManager", e);
        }
    }

    @Test
    void testCRUD() {
        try {
            Delivery delivery = DeliveryFactory.createUniqueDelivery();
            em.persist(delivery);
            SMS sms = SMSFactory.createUniqueSMS(delivery);
            em.persist(sms);
            em.flush();

            SMS found = em.find(SMS.class, sms.getId());
            assertNotNull(found);
            assertEquals(sms.getMessage(), found.getMessage());

            found.setMessage("Alterado");
            em.merge(found);
            em.flush();
            SMS updated = em.find(SMS.class, sms.getId());
            assertEquals("Alterado", updated.getMessage());

            em.remove(updated);
            em.flush();
            assertNull(em.find(SMS.class, sms.getId()));
        } catch (Exception e) {
            logger.error("Erro durante o teste CRUD do SMSRepository", e);
            throw e;
        }
    }
}



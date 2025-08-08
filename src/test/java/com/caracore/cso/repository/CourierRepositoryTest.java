
package com.caracore.cso.repository;
import com.caracore.cso.factory.UserFactory;
import com.caracore.cso.factory.CourierFactory;

import com.caracore.cso.entity.Courier;
import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CourierRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(CourierRepositoryTest.class);
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeEach
    void setUp() {
        try {
            em = TestJPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();
            em.createQuery("DELETE FROM Team").executeUpdate();
            em.createQuery("DELETE FROM Courier").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            tx.commit();
            tx.begin(); // deixa transação ativa para o teste como no padrão original
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
            User business = UserFactory.createUniqueUser();
            business.setRole("BUSINESS");
            em.persist(business);
            User user = UserFactory.createUniqueUser();
            user.setRole("COURIER");
            em.persist(user);
            Courier courier = CourierFactory.createUniqueCourier(business, user);
            em.persist(courier);
            em.flush();

            Courier found = em.find(Courier.class, courier.getId());
            assertNotNull(found);
            assertEquals(courier.getFactorCourier(), found.getFactorCourier());

            found.setFactorCourier(3.0);
            em.merge(found);
            em.flush();
            Courier updated = em.find(Courier.class, courier.getId());
            assertEquals(3.0, updated.getFactorCourier());

            em.remove(updated);
            em.flush();
            assertNull(em.find(Courier.class, courier.getId()));
        } catch (Exception e) {
            logger.error("Erro durante o teste CRUD do CourierRepository", e);
            throw e;
        }
    }
}



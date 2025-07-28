package com.caracore.cso.repository;

import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UserRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class);
    private EntityManager em;

    @BeforeEach
    void setUp() {
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
        } catch (Exception e) {
            logger.error("Erro ao iniciar EntityManager ou transação", e);
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        } catch (Exception e) {
            logger.error("Erro ao finalizar transação ou fechar EntityManager", e);
        }
    }

    @Test
    void testCRUD() {
        try {
            long ts = System.currentTimeMillis();
            User user = new User();
            user.setRole("ADMIN");
            user.setName("Test User");
            user.setLogin("test_" + ts);
            user.setPassword("123");
            em.persist(user);
            em.flush();

            User found = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", "test_" + ts)
                .getSingleResult();
            assertNotNull(found);
            assertEquals("Test User", found.getName());

            found.setName("Updated");
            em.merge(found);
            em.flush();
            User updated = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", "test_" + ts)
                .getSingleResult();
            assertEquals("Updated", updated.getName());

            em.remove(updated);
            em.flush();
            assertTrue(em.createQuery("SELECT COUNT(u) FROM User u WHERE u.login = :login", Long.class)
                .setParameter("login", "test_" + ts).getSingleResult() == 0);
        } catch (Exception e) {
            logger.error("Erro durante o teste CRUD do UserRepository", e);
            throw e;
        }
    }
}

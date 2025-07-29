
package com.caracore.cso.repository;
import com.caracore.cso.factory.UserFactory;

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
            // Limpa a tabela antes de cada teste para garantir isolamento
            em.createQuery("DELETE FROM User").executeUpdate();
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
            User user = UserFactory.createUniqueUser();
            em.persist(user);
            em.flush();

            User found = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", user.getLogin())
                .getSingleResult();
            assertNotNull(found);
            assertEquals(user.getName(), found.getName());

            found.setName("Updated");
            em.merge(found);
            em.flush();
            User updated = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", user.getLogin())
                .getSingleResult();
            assertEquals("Updated", updated.getName());

            em.remove(updated);
            em.flush();
            assertEquals(0L, em.createQuery("SELECT COUNT(u) FROM User u WHERE u.login = :login", Long.class)
                .setParameter("login", user.getLogin()).getSingleResult());
        } catch (Exception e) {
            logger.error("Erro durante o teste CRUD do UserRepository", e);
            throw e;
        }
    }
}

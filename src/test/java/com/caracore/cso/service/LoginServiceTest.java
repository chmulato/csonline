package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;

class LoginServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceTest.class);
    private EntityManager em;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            loginService = new LoginService();
            User user = TestDataFactory.createUser("ADMIN");
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao preparar o teste LoginServiceTest", e);
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            logger.error("Erro ao finalizar o teste LoginServiceTest", e);
        }
    }

    @Test
    void testLoginSuccess() {
        try {
            // Recupera o usuário criado no setUp
            User user = (User) em.createQuery("SELECT u FROM User u WHERE u.role = 'ADMIN'").getSingleResult();
            User authUser = loginService.authenticate(user.getLogin(), user.getPassword());
            assertNotNull(authUser);
            assertEquals(user.getName(), authUser.getName());
        } catch (Exception e) {
            logger.error("Erro durante o teste testLoginSuccess em LoginServiceTest", e);
            throw e;
        }
    }

    @Test
    void testLoginFail() {
        try {
            // Recupera o usuário criado no setUp
            User user = (User) em.createQuery("SELECT u FROM User u WHERE u.role = 'ADMIN'").getSingleResult();
            assertThrows(SecurityException.class, () -> {
                loginService.authenticate(user.getLogin(), "wrongpass");
            });
            assertThrows(SecurityException.class, () -> {
                loginService.authenticate("wronguser", user.getPassword());
            });
        } catch (Exception e) {
            logger.error("Erro durante o teste testLoginFail em LoginServiceTest", e);
            throw e;
        }
    }
}

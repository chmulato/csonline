package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            // Cria usuário de teste com login único
            long ts = System.currentTimeMillis();
            User user = new User();
            user.setRole("ADMIN");
            user.setName("Login Test");
            user.setLogin("loginuser_" + ts);
            user.setPassword("senha123");
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
            em.createQuery("DELETE FROM User WHERE id = 160").executeUpdate();
            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            logger.error("Erro ao finalizar o teste LoginServiceTest", e);
        }
    }

    @Test
    void testLoginSuccess() {
        try {
            User user = loginService.authenticate("loginuser", "senha123");
            assertNotNull(user);
            assertEquals("Login Test", user.getName());
        } catch (Exception e) {
            logger.error("Erro durante o teste testLoginSuccess em LoginServiceTest", e);
            throw e;
        }
    }

    @Test
    void testLoginFail() {
        try {
            assertThrows(SecurityException.class, () -> {
                loginService.authenticate("loginuser", "wrongpass");
            });
            assertThrows(SecurityException.class, () -> {
                loginService.authenticate("wronguser", "senha123");
            });
        } catch (Exception e) {
            logger.error("Erro durante o teste testLoginFail em LoginServiceTest", e);
            throw e;
        }
    }
}

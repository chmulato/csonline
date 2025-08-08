package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.TestJPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;

class LoginServiceTest extends BaseServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceTest.class);
    private EntityManager em;
    private TestableLoginService loginService;

    @BeforeEach
    void setUp() {
        em = TestJPAUtil.getEntityManager();
        em.getTransaction().begin();
        loginService = new TestableLoginService(true);
        User user = TestDataFactory.createUser("ADMIN");
        em.persist(user);
        em.getTransaction().commit();
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



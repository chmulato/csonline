package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {
    private EntityManager em;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        loginService = new LoginService();
        // Cria usuário de teste
        User user = new User();
        user.setId(160L);
        user.setRole("ADMIN");
        user.setName("Login Test");
        user.setLogin("loginuser");
        user.setPassword("senha123");
        em.persist(user);
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM User WHERE id = 160").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    void testLoginSuccess() {
        User user = loginService.authenticate("loginuser", "senha123");
        assertNotNull(user);
        assertEquals("Login Test", user.getName());
    }

    @Test
    void testLoginFail() {
        assertThrows(SecurityException.class, () -> {
            loginService.authenticate("loginuser", "wrongpass");
        });
        assertThrows(SecurityException.class, () -> {
            loginService.authenticate("wronguser", "senha123");
        });
    }
}

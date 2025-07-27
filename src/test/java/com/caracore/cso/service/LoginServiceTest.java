package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.HibernateUtil;
import org.hibernate.Session;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {
    private Session session;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        loginService = new LoginService();
        // Cria usuário de teste
        User user = new User();
        user.setId(160L);
        user.setRole("ADMIN");
        user.setName("Login Test");
        user.setLogin("loginuser");
        user.setPassword("senha123");
        session.save(user);
        session.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        session.beginTransaction();
        session.createQuery("DELETE FROM User WHERE id = 160").executeUpdate();
        session.getTransaction().commit();
        session.close();
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

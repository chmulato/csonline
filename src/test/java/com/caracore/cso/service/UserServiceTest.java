package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService();
    }

    @Test
    void testFindByLoginAndPassword() {
        User found = service.findByLoginAndPassword("admin", "admin123");
        assertNotNull(found);
        assertEquals("ADMIN", found.getRole());
        assertEquals("Administrador", found.getName());
    }

    @Test
    void testFindById() {
        User user = service.findById(1L);
        assertNotNull(user);
        assertEquals("admin", user.getLogin());
    }

    @Test
    void testFindByLogin() {
        User user = service.findByLogin("courier");
        assertNotNull(user);
        assertEquals("COURIER", user.getRole());
    }

    @Test
    void testIsAdmin() {
        User admin = service.findByLoginAndPassword("admin", "admin123");
        assertTrue(service.isAdmin(admin));
        User courier = service.findByLoginAndPassword("courier", "courier123");
        assertFalse(service.isAdmin(courier));
    }

    @Test
    void testFindAll() {
        var users = service.findAll();
        assertNotNull(users);
        assertTrue(users.size() >= 6);
    }
}

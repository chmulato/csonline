package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService();

        // Limpa e insere dados necessários para os testes
        // Remove todos os usuários existentes
        var users = service.findAll();
        for (User u : users) {
            service.delete(u.getId());
        }

        // Cria usuários de teste
        User admin = new User();
        admin.setId(1L);
        admin.setRole("ADMIN");
        admin.setName("Administrador");
        admin.setLogin("admin");
        admin.setPassword("admin123");
        service.save(admin);

        User courier = new User();
        courier.setId(2L);
        courier.setRole("COURIER");
        courier.setName("Courier");
        courier.setLogin("courier");
        courier.setPassword("courier123");
        service.save(courier);

        User business = new User();
        business.setId(3L);
        business.setRole("BUSINESS");
        business.setName("Business");
        business.setLogin("business");
        business.setPassword("business123");
        service.save(business);

        // Adiciona mais usuários se necessário para o teste de findAll
        for (long i = 4; i <= 6; i++) {
            User u = new User();
            u.setId(i);
            u.setRole("USER");
            u.setName("User" + i);
            u.setLogin("user" + i);
            u.setPassword("pass" + i);
            service.save(u);
        }
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

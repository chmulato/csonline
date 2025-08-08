package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.test.DatabaseTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste simplificado para verificar se a configuração de banco funciona
 */
class SimpleUserServiceTest extends DatabaseTestBase {
    
    private TestableUserService service;
    
    @BeforeEach
    void setUp() {
        service = new TestableUserService(true); // Modo teste
    }
    
    @Test
    void testBasicUserOperations() {
        // Criar usuário
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("password123");
        user.setName("Test User");
        user.setRole("ADMIN");
        user.setEmail("test@example.com");
        
        // Salvar
        assertDoesNotThrow(() -> service.save(user));
        assertNotNull(user.getId());
        
        // Buscar por ID
        User found = service.findById(user.getId());
        assertNotNull(found);
        assertEquals("testuser", found.getLogin());
        assertEquals("Test User", found.getName());
        
        // Buscar por login
        User foundByLogin = service.findByLogin("testuser");
        assertNotNull(foundByLogin);
        assertEquals(user.getId(), foundByLogin.getId());
        
        // Verificar admin
        assertTrue(service.isAdmin(found));
        
        // Deletar
        assertDoesNotThrow(() -> service.deleteById(user.getId()));
        
        // Verificar se foi deletado
        User deleted = service.findById(user.getId());
        assertNull(deleted);
    }
    
    @Test
    void testUniqueConstraints() {
        // Criar primeiro usuário
        User user1 = new User();
        user1.setLogin("unique");
        user1.setPassword("password");
        user1.setName("User 1");
        user1.setRole("ADMIN");
        user1.setEmail("unique@test.com");
        
        service.save(user1);
        
        // Tentar criar usuário com mesmo login
        User user2 = new User();
        user2.setLogin("unique"); // Mesmo login
        user2.setPassword("password");
        user2.setName("User 2");
        user2.setRole("BUSINESS");
        user2.setEmail("different@test.com");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.save(user2);
        });
        
        assertTrue(exception.getMessage().contains("Login já existe"));
    }
}



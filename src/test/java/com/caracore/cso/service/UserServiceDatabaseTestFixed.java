package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.TestJPAUtil;
import com.caracore.cso.test.DatabaseTestBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração para UserService com banco de dados real
 */
public class UserServiceDatabaseTestFixed extends DatabaseTestBase {
    
    @Test
    @DisplayName("Deve criar e recuperar usuário do banco de dados")
    public void testCreateAndFindUser() {
        EntityManager em = TestJPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            // Criar usuário
            User user = new User();
            user.setName("Test User");
            user.setLogin("testuser");
            user.setPassword("password123");
            user.setRole("ADMIN");
            user.setEmail("test@example.com");
            
            em.persist(user);
            transaction.commit();
            
            // Verificar se foi salvo
            assertNotNull(user.getId());
            
            // Buscar usuário salvo
            User foundUser = em.find(User.class, user.getId());
            assertNotNull(foundUser);
            assertEquals("Test User", foundUser.getName());
            assertEquals("testuser", foundUser.getLogin());
            assertEquals("password123", foundUser.getPassword());
            assertEquals("ADMIN", foundUser.getRole());
            assertEquals("test@example.com", foundUser.getEmail());
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            fail("Erro no teste: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Test
    @DisplayName("Deve buscar usuário por login")
    public void testFindByLogin() {
        EntityManager em = TestJPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            // Criar usuário
            User user = new User();
            user.setName("Search User");
            user.setLogin("searchuser");
            user.setPassword("secret");
            user.setRole("BUSINESS");
            user.setEmail("search@example.com");
            
            em.persist(user);
            transaction.commit();
            
            // Buscar por query
            User found = em.createQuery(
                "SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", "searchuser")
                .getSingleResult();
            
            assertNotNull(found);
            assertEquals("searchuser", found.getLogin());
            assertEquals("BUSINESS", found.getRole());
            assertEquals("Search User", found.getName());
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            fail("Erro no teste: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Test
    @DisplayName("Deve validar constraint unique de login")
    public void testLoginUniqueConstraint() {
        EntityManager em = TestJPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            // Criar primeiro usuário
            User user1 = new User();
            user1.setName("User One");
            user1.setLogin("uniqueuser");
            user1.setPassword("pass1");
            user1.setRole("COURIER");
            user1.setEmail("user1@example.com");
            
            em.persist(user1);
            em.flush(); // Forçar execução no banco
            
            // Tentar criar segundo usuário com mesmo login
            User user2 = new User();
            user2.setName("User Two");
            user2.setLogin("uniqueuser"); // Login duplicado
            user2.setPassword("pass2");
            user2.setRole("CUSTOMER");
            user2.setEmail("user2@example.com");
            
            em.persist(user2);
            
            // Deve falhar por causa da constraint unique
            assertThrows(Exception.class, () -> {
                em.flush();
            });
            
        } catch (Exception e) {
            // Esperado devido à constraint
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            em.close();
        }
    }
}



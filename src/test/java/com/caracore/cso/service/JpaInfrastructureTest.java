package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.TestJPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste básico para verificar se a infraestrutura JPA está funcionando
 */
public class JpaInfrastructureTest {
    
    @Test
    @DisplayName("Deve criar EntityManager sem erro")
    public void testEntityManagerCreation() {
        EntityManager em = TestJPAUtil.getEntityManager();
        assertNotNull(em);
        assertTrue(em.isOpen());
        em.close();
    }
    
    @Test
    @DisplayName("Deve conseguir fazer transação básica")
    public void testBasicTransaction() {
        EntityManager em = TestJPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            // Criar usuário simples
            User user = new User();
            user.setName("Teste JPA");
            user.setLogin("testjpa" + System.currentTimeMillis()); // Login único
            user.setPassword("password");
            user.setRole("ADMIN");
            
            em.persist(user);
            transaction.commit();
            
            // Verificar se foi salvo
            assertNotNull(user.getId());
            System.out.println("✅ Usuário criado com ID: " + user.getId());
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            fail("Erro na transação: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Test
    @DisplayName("Deve conseguir buscar dados")
    public void testDataRetrieval() {
        EntityManager em = TestJPAUtil.getEntityManager();
        
        try {
            // Tentar fazer uma query simples
            long count = em.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                          .getSingleResult();
            
            System.out.println("✅ Número de usuários no banco: " + count);
            assertTrue(count >= 0); // Qualquer valor >= 0 é válido
            
        } catch (Exception e) {
            fail("Erro na busca: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}




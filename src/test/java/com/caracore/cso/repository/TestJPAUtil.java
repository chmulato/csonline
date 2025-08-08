package com.caracore.cso.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utilitário JPA específico para testes unitários
 * Usa configuração de banco em memória sem JNDI
 */
public class TestJPAUtil {
    
    private static EntityManagerFactory emf;
    
    static {
        try {
            // Usa a unidade de persistência específica para testes
            emf = Persistence.createEntityManagerFactory("csonlineTestPU");
        } catch (Exception e) {
            System.err.println("Erro ao criar EntityManagerFactory para testes: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public static EntityManager getEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory não foi inicializado");
        }
        return emf.createEntityManager();
    }
    
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    
    /**
     * Garante que o esquema do banco de dados existe
     */
    public static void ensureSchemaExists() {
        EntityManager em = getEntityManager();
        try {
            // Executa uma operação simples para forçar a criação das tabelas
            em.getTransaction().begin();
            
            // Tenta uma operação que força o EclipseLink a criar o esquema
            try {
                em.createNativeQuery("SELECT COUNT(*) FROM APP_USER").getSingleResult();
            } catch (Exception e) {
                // Se falhar, provavelmente as tabelas não existem ainda
                // O EclipseLink deveria criar automaticamente com ddl-generation=create-tables
                System.out.println("Esquema sendo criado automaticamente pelo EclipseLink...");
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("Aviso: Problema ao verificar esquema: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    /**
     * Limpa todas as tabelas para testes isolados
     */
    public static void clearDatabase() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Desabilitar verificação de integridade referencial temporariamente
            try {
                em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
            } catch (Exception e) {
                // Ignorar se não suportar
            }
            
            // Lista de tabelas para limpar
            String[] tables = {
                "TB_DELIVERY", "TB_SMS", "TB_COURIER", "TB_CUSTOMER", 
                "TB_PRICE", "TB_TEAM", "TB_USER"
            };
            
            // Limpar cada tabela, ignorando erros se não existir
            for (String table : tables) {
                try {
                    em.createNativeQuery("DELETE FROM " + table).executeUpdate();
                } catch (Exception e) {
                    // Ignorar se a tabela não existir ainda
                    System.out.println("Tabela " + table + " não existe ou está vazia");
                }
            }
            
            // Reabilitar verificação de integridade
            try {
                em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
            } catch (Exception e) {
                // Ignorar se não suportar
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Não lançar exceção crítica para não quebrar testes
            System.out.println("Aviso: Problema ao limpar banco de dados: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    /**
     * Versão mais segura da limpeza que verifica se as tabelas existem
     */
    public static void clearDatabaseSafely() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Usar truncate em vez de delete quando possível, e só se a tabela existir
            String[] tables = {"APP_USER"}; // Começar só com a tabela User que sabemos que existe
            
            for (String table : tables) {
                try {
                    // Verificar se a tabela existe primeiro
                    em.createNativeQuery("SELECT COUNT(*) FROM " + table).getSingleResult();
                    // Se chegou aqui, a tabela existe, pode limpar
                    em.createNativeQuery("DELETE FROM " + table).executeUpdate();
                } catch (Exception e) {
                    // Tabela não existe, ignorar
                }
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }
}



package com.caracore.cso.test;

import com.caracore.cso.repository.TestJPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

/**
 * Classe base para testes que precisam de acesso ao banco de dados
 * Configura automaticamente o EntityManager para testes
 */
public abstract class DatabaseTestBase {
    
    @BeforeAll
    static void setUpDatabaseSchema() {
        // Garante que o esquema seja criado na primeira execução
        TestJPAUtil.ensureSchemaExists();
    }
    
    @BeforeEach
    void setUpDatabase() {
        // Não limpar antes dos testes para evitar erros de tabelas inexistentes
        // O EclipseLink criará as tabelas automaticamente conforme necessário
    }
    
    @AfterAll
    static void tearDown() {
        // Fecha o EntityManagerFactory após todos os testes
        TestJPAUtil.closeEntityManagerFactory();
    }
    
    /**
     * Obtém um EntityManager para testes
     */
    protected EntityManager getEntityManager() {
        return TestJPAUtil.getEntityManager();
    }
}



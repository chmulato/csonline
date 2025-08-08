package com.caracore.cso.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import jakarta.persistence.EntityManager;
import com.caracore.cso.repository.TestJPAUtil;
import com.caracore.cso.util.TestDatabaseUtil;

/**
 * Base para testes de service: ativa modo de teste e limpa o banco antes de cada método.
 */
public abstract class BaseServiceTest {
    @BeforeAll
    public static void enableTestMode() {
        System.setProperty("test.mode", "true");
    }

    @BeforeEach
    public void clearDatabase() {
        EntityManager em = TestJPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
    }
}

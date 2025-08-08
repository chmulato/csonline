package com.caracore.cso.controller;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import jakarta.persistence.EntityManager;
import com.caracore.cso.repository.TestJPAUtil;
import com.caracore.cso.util.TestDatabaseUtil;

/**
 * Base centraliza ativação do modo de teste (JPAUtil -> TestJPAUtil) e limpeza do banco.
 */
public abstract class BaseControllerJerseyTest extends JerseyTest {
    @BeforeAll
    public static void enableTestModeBase() {
        System.setProperty("test.mode", "true");
    }

    @BeforeEach
    public void clearDatabaseBase() {
        EntityManager em = TestJPAUtil.getEntityManager();
        TestDatabaseUtil.clearDatabase(em);
        em.close();
    }
}

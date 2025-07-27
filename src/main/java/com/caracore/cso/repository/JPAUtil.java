package com.caracore.cso.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JPAUtil {
    private static final Logger logger = LogManager.getLogger(JPAUtil.class);
    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            return Persistence.createEntityManagerFactory("csonlinePU");
        } catch (Throwable ex) {
            logger.error("Erro ao inicializar EntityManagerFactory do JPA", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void shutdown() {
        try {
            emf.close();
        } catch (Exception e) {
            logger.error("Erro ao fechar EntityManagerFactory do JPA", e);
        }
    }
}

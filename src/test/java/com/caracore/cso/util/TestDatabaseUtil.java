package com.caracore.cso.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseUtil.class);

    public static void clearDatabase(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Delete in order of dependencies to avoid referential integrity errors
            em.createQuery("DELETE FROM SMS").executeUpdate();
            em.createQuery("DELETE FROM Delivery").executeUpdate();
            em.createQuery("DELETE FROM Price").executeUpdate();
            em.createQuery("DELETE FROM Team").executeUpdate();
            em.createQuery("DELETE FROM Customer").executeUpdate();
            em.createQuery("DELETE FROM Courier").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            tx.commit();
            // Truncate tables and reset auto-increment (H2 specific)
            tx.begin();
            em.createNativeQuery("TRUNCATE TABLE sms RESTART IDENTITY").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE delivery RESTART IDENTITY").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE price RESTART IDENTITY").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE team RESTART IDENTITY").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE customer RESTART IDENTITY").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE courier RESTART IDENTITY").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE app_user RESTART IDENTITY").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            logger.error("Erro ao limpar o banco de dados de teste", e);
            if (tx.isActive()) tx.rollback();
        }
    }
}

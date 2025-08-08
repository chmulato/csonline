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
            // Tentar desabilitar integridade (H2 / HSQL variantes)
            try { em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate(); } catch (Exception ignored) { }
            try { em.createNativeQuery("SET DATABASE REFERENTIAL INTEGRITY FALSE").executeUpdate(); } catch (Exception ignored) { }

            // Delete em ordem (filhos -> pais)
            em.createQuery("DELETE FROM SMS").executeUpdate();
            em.createQuery("DELETE FROM Delivery").executeUpdate();
            em.createQuery("DELETE FROM Price").executeUpdate();
            em.createQuery("DELETE FROM Team").executeUpdate();
            em.createQuery("DELETE FROM Customer").executeUpdate();
            em.createQuery("DELETE FROM Courier").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();

            // Reabilitar integridade
            try { em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate(); } catch (Exception ignored) { }
            try { em.createNativeQuery("SET DATABASE REFERENTIAL INTEGRITY TRUE").executeUpdate(); } catch (Exception ignored) { }

            tx.commit();
        } catch (Exception e) {
            logger.error("Erro ao limpar o banco de dados de teste", e);
            if (tx.isActive()) tx.rollback();
        }
    }
}



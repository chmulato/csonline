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
            // Limpeza estritamente ordenada sem comandos de integridade (HSQL não suporta SET REFERENTIAL_INTEGRITY)
            logger.debug("[DB-CLEAN] Iniciando limpeza ordenada das tabelas de teste");
            em.createQuery("DELETE FROM SMS").executeUpdate();
            em.createQuery("DELETE FROM Delivery").executeUpdate();
            em.createQuery("DELETE FROM Price").executeUpdate();
            em.createQuery("DELETE FROM Team").executeUpdate();
            em.createQuery("DELETE FROM Customer").executeUpdate();
            em.createQuery("DELETE FROM Courier").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.flush(); // garante aplicação antes de commit
            tx.commit();
            logger.debug("[DB-CLEAN] Limpeza concluída");
        } catch (Exception e) {
            logger.error("Erro ao limpar o banco de dados de teste", e);
            if (tx.isActive()) tx.rollback();
        }
    }
}



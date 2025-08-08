package com.caracore.cso.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// Import usado somente em modo de teste (classe está em test scope)
// Usamos reflexão defensiva para evitar erro em compilação de produção? Aqui precisamos do símbolo em test compile.
// Como o build principal compila main antes de test, referenciar diretamente classe em test causaria erro.
// Ajuste: usar reflexão ao invés de referência direta.
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JPAUtil {
    private static final Logger logger = LogManager.getLogger(JPAUtil.class);
    private static EntityManagerFactory emf = null;
    private static volatile boolean initialized = false;

    private static synchronized void initIfNeeded() {
        if (initialized) return;
        initialized = true;
        boolean testMode = Boolean.parseBoolean(System.getProperty("test.mode", "false"));
        if (testMode) {
            logger.info("[JPAUtil] Modo de teste ativo - delegando para TestJPAUtil");
            return; // Não inicializa EMF real; TestJPAUtil será usado diretamente
        }
        try {
            emf = Persistence.createEntityManagerFactory("csonlinePU");
        } catch (Throwable ex) {
            logger.error("Erro ao inicializar EntityManagerFactory do JPA", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        boolean testMode = Boolean.parseBoolean(System.getProperty("test.mode", "false"));
        if (testMode) {
            try {
                Class<?> testUtil = Class.forName("com.caracore.cso.repository.TestJPAUtil");
                return (EntityManager) testUtil.getMethod("getEntityManager").invoke(null);
            } catch (Exception e) {
                throw new IllegalStateException("Falha ao obter EntityManager em modo de teste", e);
            }
        }
        initIfNeeded();
        return emf.createEntityManager();
    }

    public static void shutdown() {
        try {
            if (emf != null && emf.isOpen()) emf.close();
        } catch (Exception e) {
            logger.error("Erro ao fechar EntityManagerFactory do JPA", e);
        }
    }
}

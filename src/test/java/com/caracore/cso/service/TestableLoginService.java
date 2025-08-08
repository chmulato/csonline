package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.repository.TestJPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Versão testável do LoginService usando TestJPAUtil quando em modo de teste.
 */
public class TestableLoginService {
    private static final Logger logger = LogManager.getLogger(TestableLoginService.class);
    private final boolean isTestMode;

    public TestableLoginService() { this(false); }
    public TestableLoginService(boolean testMode) { this.isTestMode = testMode; }

    private EntityManager getEntityManager() {
        return isTestMode ? TestJPAUtil.getEntityManager() : JPAUtil.getEntityManager();
    }

    public User authenticate(String login, String password) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class);
            query.setParameter("login", login);
            User user = query.getResultStream().findFirst().orElse(null);
            if (user == null || !user.getPassword().equals(password)) {
                logger.warn("Tentativa de login inválido para usuário: {}", login);
                throw new SecurityException("Login ou senha inválidos");
            }
            return user;
        } catch (SecurityException se) {
            throw se;
        } catch (Exception e) {
            logger.error("Erro ao autenticar usuário: " + login, e);
            throw new SecurityException("Login ou senha inválidos", e);
        } finally {
            em.close();
        }
    }
}

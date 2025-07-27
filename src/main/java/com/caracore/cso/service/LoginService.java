package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.JPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class LoginService {
    private static final Logger logger = LogManager.getLogger(LoginService.class);

    /**
     * Autentica o usuário pelo login e senha.
     * @param login login do usuário
     * @param password senha do usuário
     * @return User autenticado
     * @throws SecurityException se não encontrar ou senha inválida
     */
    public User authenticate(String login, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("FROM User WHERE login = :login", User.class);
            query.setParameter("login", login);
            User user = query.getResultStream().findFirst().orElse(null);
            if (user == null || !user.getPassword().equals(password)) {
                logger.warn("Tentativa de login inválido para usuário: {}", login);
                throw new SecurityException("Login ou senha inválidos");
            }
            return user;
        } catch (Exception e) {
            logger.error("Erro ao autenticar usuário: " + login, e);
            throw e;
        } finally {
            em.close();
        }
    }
}

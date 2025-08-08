package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.repository.TestJPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class TestableUserService {
    private static final Logger logger = LogManager.getLogger(TestableUserService.class);
    private final boolean isTestMode;
    
    public TestableUserService() {
        this.isTestMode = false; // Modo produção
    }
    
    public TestableUserService(boolean testMode) {
        this.isTestMode = testMode;
    }
    
    private EntityManager getEntityManager() {
        return isTestMode ? TestJPAUtil.getEntityManager() : JPAUtil.getEntityManager();
    }

    public void save(User user) {
        EntityManager em = getEntityManager();
        try {
            logger.info("[DEBUG] Salvando usuário com ID: {}", user.getId());
            em.getTransaction().begin();
            // Validação de unicidade de login
            if (user.getId() == null) {
                TypedQuery<User> loginQuery = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class);
                loginQuery.setParameter("login", user.getLogin());
                if (!loginQuery.getResultList().isEmpty()) {
                    throw new IllegalArgumentException("Login já existe: " + user.getLogin());
                }
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    TypedQuery<User> emailQuery = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
                    emailQuery.setParameter("email", user.getEmail());
                    if (!emailQuery.getResultList().isEmpty()) {
                        throw new IllegalArgumentException("Email já existe: " + user.getEmail());
                    }
                }
                em.persist(user);
                logger.info("[DEBUG] Usuário criado com ID: {}", user.getId());
            } else {
                em.merge(user);
                logger.info("[DEBUG] Usuário atualizado com ID: {}", user.getId());
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao salvar usuário", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public User findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public User findByLogin(String login) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class);
            query.setParameter("login", login);
            List<User> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public User findByLoginAndPassword(String login, String password) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.login = :login AND u.password = :password", User.class);
            query.setParameter("login", login);
            query.setParameter("password", password);
            List<User> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

    public List<User> findAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
                logger.info("[DEBUG] Usuário removido com ID: {}", id);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao deletar usuário", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        deleteById(id);
    }
}



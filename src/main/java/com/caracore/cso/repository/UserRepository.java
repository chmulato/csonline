package com.caracore.cso.repository;

import com.caracore.cso.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(User user) {
        try {
            em.persist(user);
        } catch (Exception e) {
            logger.error("Erro ao salvar User", e);
            throw e;
        }
    }

    public User findById(Long id) {
        try {
            return em.find(User.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar User por id", e);
            throw e;
        }
    }

    public List<User> findAll() {
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os Users", e);
            throw e;
        }
    }

    @Transactional
    public void update(User user) {
        try {
            em.merge(user);
        } catch (Exception e) {
            logger.error("Erro ao atualizar User", e);
            throw e;
        }
    }

    @Transactional
    public void delete(User user) {
        try {
            em.remove(em.contains(user) ? user : em.merge(user));
        } catch (Exception e) {
            logger.error("Erro ao deletar User", e);
            throw e;
        }
    }
}

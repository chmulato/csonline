package com.caracore.cso.repository;

import com.caracore.cso.entity.Team;
import jakarta.persistence.EntityManager;
import java.util.List;

public class TeamRepository {
    private EntityManager entityManager;

    public TeamRepository() {
        this.entityManager = null;
    }

    public TeamRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        if (entityManager != null) return entityManager;
        return JPAUtil.getEntityManager();
    }
    public void save(Team team) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (team.getId() == null) {
                Long nextId = ((Number) em.createQuery("SELECT COALESCE(MAX(t.id), 0) + 1 FROM Team t").getSingleResult()).longValue();
                team.setId(nextId);
            }
            em.merge(team);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            if (entityManager == null) em.close();
        }
    }

    public Team findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Team.class, id);
        } finally {
            if (entityManager == null) em.close();
        }
    }

    public List<Team> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT t FROM Team t", Team.class).getResultList();
        } finally {
            if (entityManager == null) em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Team team = em.find(Team.class, id);
            if (team != null) {
                em.remove(team);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            if (entityManager == null) em.close();
        }
    }
}

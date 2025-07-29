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
            // Garante que business e courier estão gerenciados
            if (team.getBusiness() != null && team.getBusiness().getId() != null) {
                team.setBusiness(em.find(com.caracore.cso.entity.User.class, team.getBusiness().getId()));
            }
            if (team.getCourier() != null && team.getCourier().getId() != null) {
                team.setCourier(em.find(com.caracore.cso.entity.User.class, team.getCourier().getId()));
            }
            // Validação de unicidade: não pode haver outro Team com mesmo business e courier
            if (team.getBusiness() != null && team.getCourier() != null) {
                Long count = em.createQuery(
                    "SELECT COUNT(t) FROM Team t WHERE t.business.id = :businessId AND t.courier.id = :courierId" +
                    (team.getId() != null ? " AND t.id <> :id" : ""), Long.class)
                    .setParameter("businessId", team.getBusiness().getId())
                    .setParameter("courierId", team.getCourier().getId())
                    .setParameter(team.getId() != null ? "id" : "dummy", team.getId())
                    .getSingleResult();
                if (count > 0) {
                    throw new IllegalArgumentException("Já existe um Team para este business e courier.");
                }
            }
            if (team.getId() == null) {
                em.persist(team);
            } else {
                em.merge(team);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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

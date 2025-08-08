package com.caracore.cso.service.testable;

import com.caracore.cso.entity.Team;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.repository.TestJPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Versão testável do TeamService que pode usar TestJPAUtil
 */
public class TestableTeamService {
    private final boolean testMode;

    public TestableTeamService() {
        this(false);
    }
    
    public TestableTeamService(boolean testMode) {
        this.testMode = testMode;
    }

    private EntityManager getEntityManager() {
        return testMode ? TestJPAUtil.getEntityManager() : JPAUtil.getEntityManager();
    }

    public void save(Team team) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            if (team.getId() == null) {
                em.persist(team);
            } else {
                em.merge(team);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Team findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Team.class, id);
        } finally {
            em.close();
        }
    }

    public List<Team> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT t FROM Team t", Team.class).getResultList();
        } finally {
            em.close();
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Team> findByBusiness(Long businessId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Team> query = em.createQuery(
                "SELECT t FROM Team t WHERE t.business.id = :businessId", Team.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}



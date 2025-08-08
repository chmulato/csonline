package com.caracore.cso.service;

import com.caracore.cso.entity.Team;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.repository.TestJPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Versão testável do TeamService
 */
public class TestableTeamService {
    private static final Logger logger = LogManager.getLogger(TestableTeamService.class);
    private final boolean isTestMode;
    
    public TestableTeamService() {
        this.isTestMode = false;
    }
    
    public TestableTeamService(boolean testMode) {
        this.isTestMode = testMode;
    }
    
    private EntityManager getEntityManager() {
        return isTestMode ? TestJPAUtil.getEntityManager() : JPAUtil.getEntityManager();
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
            logger.info("[DEBUG] Team salvo com sucesso. ID: {}", team.getId());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao salvar team", e);
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

    public List<Team> findByBusiness(Long businessId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Team> query = em.createQuery("SELECT t FROM Team t WHERE t.business.id = :businessId", Team.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteById(Long id) {
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
}



package com.caracore.cso.service.testable;

import com.caracore.cso.entity.Courier;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.repository.TestJPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Versão testável do CourierService que pode usar TestJPAUtil
 */
public class TestableCourierService {
    private final boolean testMode;

    public TestableCourierService() {
        this(false);
    }
    
    public TestableCourierService(boolean testMode) {
        this.testMode = testMode;
    }

    private EntityManager getEntityManager() {
        return testMode ? TestJPAUtil.getEntityManager() : JPAUtil.getEntityManager();
    }

    public void save(Courier courier) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            if (courier.getId() == null) {
                em.persist(courier);
            } else {
                em.merge(courier);
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

    public Courier findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Courier.class, id);
        } finally {
            em.close();
        }
    }

    public List<Courier> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Courier c", Courier.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Courier courier = em.find(Courier.class, id);
            if (courier != null) {
                em.remove(courier);
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

    public List<Courier> findByBusiness(Long businessId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Courier> query = em.createQuery(
                "SELECT c FROM Courier c WHERE c.business.id = :businessId", Courier.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}



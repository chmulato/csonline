package com.caracore.cso.service;

import com.caracore.cso.entity.Price;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.repository.TestJPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Versão testável do PriceService
 */
public class TestablePriceService {
    private static final Logger logger = LogManager.getLogger(TestablePriceService.class);
    private final boolean isTestMode;
    
    public TestablePriceService() {
        this.isTestMode = false;
    }
    
    public TestablePriceService(boolean testMode) {
        this.isTestMode = testMode;
    }
    
    private EntityManager getEntityManager() {
        return isTestMode ? TestJPAUtil.getEntityManager() : JPAUtil.getEntityManager();
    }

    public void save(Price price) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            if (price.getId() == null) {
                em.persist(price);
            } else {
                em.merge(price);
            }
            
            em.getTransaction().commit();
            logger.info("[DEBUG] Price salvo com sucesso. ID: {}", price.getId());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao salvar price", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public Price findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Price.class, id);
        } finally {
            em.close();
        }
    }

    public List<Price> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Price p", Price.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Price> findByBusiness(Long businessId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Price> query = em.createQuery("SELECT p FROM Price p WHERE p.business.id = :businessId", Price.class);
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
            Price price = em.find(Price.class, id);
            if (price != null) {
                em.remove(price);
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



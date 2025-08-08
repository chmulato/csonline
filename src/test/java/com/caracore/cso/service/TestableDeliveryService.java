package com.caracore.cso.service;

import com.caracore.cso.entity.Delivery;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.repository.TestJPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Versão testável do DeliveryService
 */
public class TestableDeliveryService {
    private static final Logger logger = LogManager.getLogger(TestableDeliveryService.class);
    private final boolean isTestMode;
    
    public TestableDeliveryService() {
        this.isTestMode = false;
    }
    
    public TestableDeliveryService(boolean testMode) {
        this.isTestMode = testMode;
    }
    
    private EntityManager getEntityManager() {
        return isTestMode ? TestJPAUtil.getEntityManager() : JPAUtil.getEntityManager();
    }

    public void save(Delivery delivery) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            if (delivery.getId() == null) {
                em.persist(delivery);
            } else {
                em.merge(delivery);
            }
            
            em.getTransaction().commit();
            logger.info("[DEBUG] Delivery salvo com sucesso. ID: {}", delivery.getId());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao salvar delivery", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public Delivery findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Delivery.class, id);
        } finally {
            em.close();
        }
    }

    public List<Delivery> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT d FROM Delivery d", Delivery.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Delivery> findByBusiness(Long businessId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Delivery> query = em.createQuery("SELECT d FROM Delivery d WHERE d.business.id = :businessId", Delivery.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Delivery> findAllByBusiness(Long businessId) {
        return findByBusiness(businessId);
    }

    public List<Delivery> findByCourier(Long courierId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Delivery> query = em.createQuery("SELECT d FROM Delivery d WHERE d.courier.id = :courierId", Delivery.class);
            query.setParameter("courierId", courierId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Delivery> findByCustomer(Long customerId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Delivery> query = em.createQuery("SELECT d FROM Delivery d WHERE d.customer.id = :customerId", Delivery.class);
            query.setParameter("customerId", customerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Delivery delivery = em.find(Delivery.class, id);
            if (delivery != null) {
                em.remove(delivery);
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



package com.caracore.cso.service;

import com.caracore.cso.entity.Courier;
import com.caracore.cso.entity.User;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.repository.TestJPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Versão testável do CourierService
 */
public class TestableCourierService {
    private static final Logger logger = LogManager.getLogger(TestableCourierService.class);
    private final boolean isTestMode;
    
    public TestableCourierService() {
        this.isTestMode = false;
    }
    
    public TestableCourierService(boolean testMode) {
        this.isTestMode = testMode;
    }
    
    private EntityManager getEntityManager() {
        return isTestMode ? TestJPAUtil.getEntityManager() : JPAUtil.getEntityManager();
    }

    public void save(Courier courier) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (courier.getBusiness() != null) {
                courier.setBusiness(em.merge(courier.getBusiness()));
            }
            if (courier.getUser() != null) {
                courier.setUser(em.merge(courier.getUser()));
            }
            
            if (courier.getId() == null) {
                em.persist(courier);
            } else {
                em.merge(courier);
            }
            
            em.getTransaction().commit();
            logger.info("[DEBUG] Courier salvo com sucesso. ID: {}", courier.getId());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao salvar courier", e);
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

    public List<Courier> findByBusiness(Long businessId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Courier> query = em.createQuery("SELECT c FROM Courier c WHERE c.business.id = :businessId", Courier.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Courier> findAllByBusiness(Long businessId) {
        return findByBusiness(businessId);
    }

    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Courier courier = em.find(Courier.class, id);
            if (courier != null) {
                try {
                    em.remove(courier);
                } catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg != null && msg.toLowerCase().contains("constraint")) {
                        throw new RuntimeException("Não foi possível deletar o entregador. Existem registros vinculados.");
                    }
                    throw e;
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            String full = (e.getMessage() + " " + (e.getCause()!=null? e.getCause().getMessage():""));
            if (full != null && full.toLowerCase().contains("constraint")) {
                throw new RuntimeException("Não foi possível deletar o entregador. Existem registros vinculados.", e);
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateFactor(Long courierId, double factor) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Courier courier = em.find(Courier.class, courierId);
            if (courier != null) {
                courier.setFactorCourier(factor);
                em.merge(courier);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean canAccessDelivery(User courierUser, com.caracore.cso.entity.Delivery delivery) {
        if (courierUser == null || delivery == null) {
            return false;
        }
        
        // Buscar o courier associado ao usuário
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Courier> query = em.createQuery("SELECT c FROM Courier c WHERE c.user.id = :userId", Courier.class);
            query.setParameter("userId", courierUser.getId());
            List<Courier> couriers = query.getResultList();
            
            if (couriers.isEmpty()) {
                return false;
            }
            
            Courier courier = couriers.get(0);
            // Verificar se o courier pertence ao mesmo business da entrega
            return courier.getBusiness().getId().equals(delivery.getBusiness().getId());
        } finally {
            em.close();
        }
    }
}



package com.caracore.cso.service;



import com.caracore.cso.entity.Courier;
import com.caracore.cso.repository.JPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;

import java.util.List;

public class CourierService {
    private static final Logger logger = LogManager.getLogger(CourierService.class);

    public java.util.List<Courier> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Courier> query = em.createQuery("SELECT c FROM Courier c", Courier.class);
            return new java.util.ArrayList<>(query.getResultList());
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os couriers", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void save(Courier courier) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(courier);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar courier", e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Courier courier) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(courier);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar courier", e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Long courierId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Courier courier = em.find(Courier.class, courierId);
            if (courier != null) {
                em.remove(courier);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao deletar courier id: " + courierId, e);
            em.getTransaction().rollback();
            String msg = e.getMessage();
            if (msg != null && msg.contains("integrity constraint violation")) {
                throw new RuntimeException("Não foi possível deletar o entregador. Existem vínculos que impedem a exclusão.");
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Courier findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Courier.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar courier por id: " + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Courier> findAllByBusiness(Long businessId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Courier> query = em.createQuery("SELECT c FROM Courier c WHERE c.business.id = :businessId", Courier.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar couriers por businessId: " + businessId, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateFactor(Long courierId, double factor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE Courier SET factorCourier = :factor WHERE id = :courierId");
            query.setParameter("factor", factor);
            query.setParameter("courierId", courierId);
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar fator do courier id: " + courierId, e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteById(Long courierId) {
        delete(courierId);
    }

    public boolean canAccessDelivery(com.caracore.cso.entity.User courier, com.caracore.cso.entity.Delivery delivery) {
        try {
            if (courier == null || delivery == null) return false;
            return "COURIER".equalsIgnoreCase(courier.getRole()) &&
                   delivery.getCourier() != null &&
                   delivery.getCourier().getUser() != null &&
                   courier.getId().equals(delivery.getCourier().getUser().getId());
        } catch (Exception e) {
            logger.error("Erro ao verificar acesso do courier à delivery", e);
            return false;
        }
    }
}

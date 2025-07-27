package com.caracore.cso.service;


import com.caracore.cso.entity.Delivery;
import com.caracore.cso.repository.JPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;

import java.util.List;

public class DeliveryService {
    private static final Logger logger = LogManager.getLogger(DeliveryService.class);

    public java.util.List<Delivery> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Delivery> query = em.createQuery("SELECT d FROM Delivery d", Delivery.class);
            return new java.util.ArrayList<>(query.getResultList());
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as deliveries", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void save(Delivery delivery) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(delivery);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar delivery", e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Delivery delivery) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(delivery);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar delivery", e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Long deliveryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Delivery delivery = em.find(Delivery.class, deliveryId);
            if (delivery != null) {
                em.remove(delivery);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao deletar delivery id: " + deliveryId, e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Delivery findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Delivery.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar delivery por id: " + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Delivery> findAllByBusiness(Long businessId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Delivery> query = em.createQuery("SELECT d FROM Delivery d WHERE d.business.id = :businessId", Delivery.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar deliveries por businessId: " + businessId, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Delivery> findAllNotCompletedByBusiness(Long businessId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Delivery> query = em.createQuery("SELECT d FROM Delivery d WHERE d.business.id = :businessId AND d.completed = false", Delivery.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar deliveries não concluídas por businessId: " + businessId, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateDeliveryStatus(Long deliveryId, boolean received, boolean completed) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE Delivery SET received = :received, completed = :completed WHERE id = :deliveryId");
            query.setParameter("received", received);
            query.setParameter("completed", completed);
            query.setParameter("deliveryId", deliveryId);
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar status da delivery id: " + deliveryId, e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteById(Long deliveryId) {
        delete(deliveryId);
    }
}

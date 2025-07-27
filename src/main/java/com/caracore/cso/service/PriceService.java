package com.caracore.cso.service;

import com.caracore.cso.entity.Price;
import com.caracore.cso.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PriceService {
    private static final Logger logger = LogManager.getLogger(PriceService.class);

    public Price findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Price.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar price por id: " + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Price> findAllByBusiness(Long businessId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Price> query = em.createQuery("FROM Price WHERE business.id = :businessId", Price.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar prices por businessId: " + businessId, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void updatePrice(Long priceId, double price) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE Price SET price = :price WHERE id = :priceId");
            query.setParameter("price", price);
            query.setParameter("priceId", priceId);
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar price id: " + priceId, e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteById(Long priceId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Price price = em.find(Price.class, priceId);
            if (price != null) {
                em.remove(price);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao deletar price por id: " + priceId, e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

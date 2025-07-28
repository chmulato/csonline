
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

    public void save(Price price) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (price.getId() == null) {
                Long nextId = ((Number) em.createQuery("SELECT COALESCE(MAX(p.id), 0) + 1 FROM Price p").getSingleResult()).longValue();
                price.setId(nextId);
            }
            em.merge(price);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar price", e);
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

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
            TypedQuery<Price> query = em.createQuery("SELECT p FROM Price p WHERE p.business.id = :businessId", Price.class);
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
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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
            if (em.getTransaction().isActive()) try { em.getTransaction().rollback(); } catch (Exception ex) { /* ignora */ }
            String msg = e.getMessage();
            if ((msg != null && msg.contains("integrity constraint violation")) ||
                (e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().contains("integrity constraint violation"))) {
                throw new com.caracore.cso.exception.ReferentialIntegrityException("Não foi possível deletar o preço. Existem registros vinculados a este preço.", e);
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Price> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Price p", Price.class).getResultList();
        } finally {
            em.close();
        }
    }
}

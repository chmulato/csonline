package com.caracore.cso.service;

import com.caracore.cso.entity.Price;
import com.caracore.cso.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PriceService {
    private static final Logger logger = LogManager.getLogger(PriceService.class);

    public Price findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Price.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar price por id: " + id, e);
            throw e;
        }
    }

    public List<Price> findAllByBusiness(Long businessId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Price> query = session.createQuery("FROM Price WHERE business.id = :businessId", Price.class);
            query.setParameter("businessId", businessId);
            return query.list();
        } catch (Exception e) {
            logger.error("Erro ao buscar prices por businessId: " + businessId, e);
            throw e;
        }
    }

    public void updatePrice(Long priceId, double price) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("UPDATE Price SET price = :price WHERE id = :priceId");
            query.setParameter("price", price);
            query.setParameter("priceId", priceId);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar price id: " + priceId, e);
            throw e;
        }
    }

    public void deleteById(Long priceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Price price = session.get(Price.class, priceId);
            if (price != null) {
                session.delete(price);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao deletar price por id: " + priceId, e);
            throw e;
        }
    }
}

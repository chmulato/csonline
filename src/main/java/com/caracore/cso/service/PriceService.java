package com.caracore.cso.service;

import com.caracore.cso.entity.Price;
import com.caracore.cso.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class PriceService {
    public Price findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Price.class, id);
        }
    }

    public List<Price> findAllByBusiness(Long businessId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Price> query = session.createQuery("FROM Price WHERE business.id = :businessId", Price.class);
            query.setParameter("businessId", businessId);
            return query.list();
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
        }
    }
}

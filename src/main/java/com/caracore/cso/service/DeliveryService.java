package com.caracore.cso.service;


import com.caracore.cso.entity.Delivery;
import com.caracore.cso.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class DeliveryService {
    public List<Delivery> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Delivery", Delivery.class).list();
        }
    }

    public void save(Delivery delivery) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(delivery);
            session.getTransaction().commit();
        }
    }

    public void update(Delivery delivery) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(delivery);
            session.getTransaction().commit();
        }
    }

    public void delete(Long deliveryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Delivery delivery = session.get(Delivery.class, deliveryId);
            if (delivery != null) {
                session.delete(delivery);
            }
            session.getTransaction().commit();
        }
    }
    public Delivery findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Delivery.class, id);
        }
    }

    public List<Delivery> findAllByBusiness(Long businessId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Delivery> query = session.createQuery("FROM Delivery WHERE business.id = :businessId", Delivery.class);
            query.setParameter("businessId", businessId);
            return query.list();
        }
    }

    public List<Delivery> findAllNotCompletedByBusiness(Long businessId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Delivery> query = session.createQuery("FROM Delivery WHERE business.id = :businessId AND completed = false", Delivery.class);
            query.setParameter("businessId", businessId);
            return query.list();
        }
    }

    public void updateDeliveryStatus(Long deliveryId, boolean received, boolean completed) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("UPDATE Delivery SET received = :received, completed = :completed WHERE id = :deliveryId");
            query.setParameter("received", received);
            query.setParameter("completed", completed);
            query.setParameter("deliveryId", deliveryId);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    public void deleteById(Long deliveryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Delivery delivery = session.get(Delivery.class, deliveryId);
            if (delivery != null) {
                session.delete(delivery);
            }
            session.getTransaction().commit();
        }
    }
}


package com.caracore.cso.service;



import com.caracore.cso.entity.Courier;
import com.caracore.cso.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CourierService {
    public List<Courier> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Courier", Courier.class).list();
        }
    }

    public void save(Courier courier) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(courier);
            session.getTransaction().commit();
        }
    }

    public void update(Courier courier) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(courier);
            session.getTransaction().commit();
        }
    }

    public void delete(Long courierId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Courier courier = session.get(Courier.class, courierId);
            if (courier != null) {
                session.delete(courier);
            }
            session.getTransaction().commit();
        }
    }
    public Courier findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Courier.class, id);
        }
    }

    public List<Courier> findAllByBusiness(Long businessId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Courier> query = session.createQuery("FROM Courier WHERE business.id = :businessId", Courier.class);
            query.setParameter("businessId", businessId);
            return query.list();
        }
    }

    public void updateFactor(Long courierId, double factor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("UPDATE Courier SET factorCourier = :factor WHERE id = :courierId");
            query.setParameter("factor", factor);
            query.setParameter("courierId", courierId);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    public void deleteById(Long courierId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Courier courier = session.get(Courier.class, courierId);
            if (courier != null) {
                session.delete(courier);
            }
            session.getTransaction().commit();
        }
    }
    public boolean canAccessDelivery(com.caracore.cso.entity.User courier, com.caracore.cso.entity.Delivery delivery) {
        if (courier == null || delivery == null) return false;
        // O entregador pode acessar se for o usuário vinculado ao courier da entrega
        return "COURIER".equalsIgnoreCase(courier.getRole()) &&
               delivery.getCourier() != null &&
               delivery.getCourier().getUser() != null &&
               courier.getId().equals(delivery.getCourier().getUser().getId());
    }
}

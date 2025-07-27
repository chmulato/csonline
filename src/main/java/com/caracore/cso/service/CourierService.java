package com.caracore.cso.service;



import com.caracore.cso.entity.Courier;
import com.caracore.cso.repository.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CourierService {
    private static final Logger logger = LogManager.getLogger(CourierService.class);

    public List<Courier> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Courier", Courier.class).list();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os couriers", e);
            throw e;
        }
    }

    public void save(Courier courier) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(courier);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar courier", e);
            throw e;
        }
    }

    public void update(Courier courier) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(courier);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar courier", e);
            throw e;
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
        } catch (Exception e) {
            logger.error("Erro ao deletar courier id: " + courierId, e);
            throw e;
        }
    }

    public Courier findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Courier.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar courier por id: " + id, e);
            throw e;
        }
    }

    public List<Courier> findAllByBusiness(Long businessId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Courier> query = session.createQuery("FROM Courier WHERE business.id = :businessId", Courier.class);
            query.setParameter("businessId", businessId);
            return query.list();
        } catch (Exception e) {
            logger.error("Erro ao buscar couriers por businessId: " + businessId, e);
            throw e;
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
        } catch (Exception e) {
            logger.error("Erro ao atualizar fator do courier id: " + courierId, e);
            throw e;
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
        } catch (Exception e) {
            logger.error("Erro ao deletar courier por id: " + courierId, e);
            throw e;
        }
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

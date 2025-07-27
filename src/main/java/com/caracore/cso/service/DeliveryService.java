package com.caracore.cso.service;


import com.caracore.cso.entity.Delivery;
import com.caracore.cso.repository.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class DeliveryService {
    private static final Logger logger = LogManager.getLogger(DeliveryService.class);

    public List<Delivery> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Delivery", Delivery.class).list();
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as deliveries", e);
            throw e;
        }
    }

    public void save(Delivery delivery) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(delivery);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar delivery", e);
            throw e;
        }
    }

    public void update(Delivery delivery) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(delivery);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar delivery", e);
            throw e;
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
        } catch (Exception e) {
            logger.error("Erro ao deletar delivery id: " + deliveryId, e);
            throw e;
        }
    }

    public Delivery findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Delivery.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar delivery por id: " + id, e);
            throw e;
        }
    }

    public List<Delivery> findAllByBusiness(Long businessId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Delivery> query = session.createQuery("FROM Delivery WHERE business.id = :businessId", Delivery.class);
            query.setParameter("businessId", businessId);
            return query.list();
        } catch (Exception e) {
            logger.error("Erro ao buscar deliveries por businessId: " + businessId, e);
            throw e;
        }
    }

    public List<Delivery> findAllNotCompletedByBusiness(Long businessId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Delivery> query = session.createQuery("FROM Delivery WHERE business.id = :businessId AND completed = false", Delivery.class);
            query.setParameter("businessId", businessId);
            return query.list();
        } catch (Exception e) {
            logger.error("Erro ao buscar deliveries não concluídas por businessId: " + businessId, e);
            throw e;
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
        } catch (Exception e) {
            logger.error("Erro ao atualizar status da delivery id: " + deliveryId, e);
            throw e;
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
        } catch (Exception e) {
            logger.error("Erro ao deletar delivery por id: " + deliveryId, e);
            throw e;
        }
    }
}

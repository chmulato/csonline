package com.caracore.cso.service;

import com.caracore.cso.entity.SMS;
import com.caracore.cso.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class SMSService {

    public List<SMS> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SMS> query = session.createQuery("FROM SMS ORDER BY datetime ASC", SMS.class);
            return query.list();
        }
    }

    public void save(SMS sms) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(sms);
            session.getTransaction().commit();
        }
    }
    public SMS findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(SMS.class, id);
        }
    }

    public List<SMS> findAllByDelivery(Long deliveryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SMS> query = session.createQuery("FROM SMS WHERE delivery.id = :deliveryId ORDER BY type, piece", SMS.class);
            query.setParameter("deliveryId", deliveryId);
            return query.list();
        }
    }

    // Registra uma mensagem SMS entre courier e customer para uma entrega
    public void sendDeliverySMS(Long deliveryId, String fromMobile, String toMobile, String type, String message, Integer piece, String datetime) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            SMS sms = new SMS();
            sms.setDelivery(session.get(com.caracore.cso.entity.Delivery.class, deliveryId));
            sms.setMobileFrom(fromMobile);
            sms.setMobileTo(toMobile);
            sms.setType(type);
            sms.setMessage(message);
            sms.setPiece(piece);
            sms.setDatetime(datetime);
            session.persist(sms);
            session.getTransaction().commit();
        }
    }

    // Consulta o histórico de mensagens SMS de uma entrega
    public List<SMS> getDeliverySMSHistory(Long deliveryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SMS> query = session.createQuery("FROM SMS WHERE delivery.id = :deliveryId ORDER BY datetime ASC", SMS.class);
            query.setParameter("deliveryId", deliveryId);
            return query.list();
        }
    }

    public void updateDeliveryId(Long smsId, Long deliveryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("UPDATE SMS SET delivery.id = :deliveryId WHERE id = :smsId");
            query.setParameter("deliveryId", deliveryId);
            query.setParameter("smsId", smsId);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    public void deleteById(Long smsId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            SMS sms = session.get(SMS.class, smsId);
            if (sms != null) {
                session.delete(sms);
            }
            session.getTransaction().commit();
        }
    }
}
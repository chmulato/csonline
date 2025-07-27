package com.caracore.cso.repository;

import com.caracore.cso.entity.SMS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

public class SMSRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(SMS sms) {
        em.persist(sms);
    }

    public SMS findById(Long id) {
        return em.find(SMS.class, id);
    }

    public List<SMS> findAll() {
        return em.createQuery("SELECT s FROM SMS s", SMS.class).getResultList();
    }

    @Transactional
    public void update(SMS sms) {
        em.merge(sms);
    }

    @Transactional
    public void delete(SMS sms) {
        em.remove(em.contains(sms) ? sms : em.merge(sms));
    }
}

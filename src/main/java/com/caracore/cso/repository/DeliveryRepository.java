package com.caracore.cso.repository;

import com.caracore.cso.entity.Delivery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

public class DeliveryRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Delivery delivery) {
        em.persist(delivery);
    }

    public Delivery findById(Long id) {
        return em.find(Delivery.class, id);
    }

    public List<Delivery> findAll() {
        return em.createQuery("SELECT d FROM Delivery d", Delivery.class).getResultList();
    }

    @Transactional
    public void update(Delivery delivery) {
        em.merge(delivery);
    }

    @Transactional
    public void delete(Delivery delivery) {
        em.remove(em.contains(delivery) ? delivery : em.merge(delivery));
    }
}

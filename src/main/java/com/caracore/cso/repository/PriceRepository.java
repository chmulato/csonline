package com.caracore.cso.repository;

import com.caracore.cso.entity.Price;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

public class PriceRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Price price) {
        em.persist(price);
    }

    public Price findById(Long id) {
        return em.find(Price.class, id);
    }

    public List<Price> findAll() {
        return em.createQuery("SELECT p FROM Price p", Price.class).getResultList();
    }

    @Transactional
    public void update(Price price) {
        em.merge(price);
    }

    @Transactional
    public void delete(Price price) {
        em.remove(em.contains(price) ? price : em.merge(price));
    }
}

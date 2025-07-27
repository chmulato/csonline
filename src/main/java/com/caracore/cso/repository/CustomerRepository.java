package com.caracore.cso.repository;

import com.caracore.cso.entity.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

public class CustomerRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Customer customer) {
        em.persist(customer);
    }

    public Customer findById(Long id) {
        return em.find(Customer.class, id);
    }

    public List<Customer> findAll() {
        return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
    }

    @Transactional
    public void update(Customer customer) {
        em.merge(customer);
    }

    @Transactional
    public void delete(Customer customer) {
        em.remove(em.contains(customer) ? customer : em.merge(customer));
    }
}

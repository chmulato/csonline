package com.caracore.cso.repository;

import com.caracore.cso.entity.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomerRepository {
    private static final Logger logger = LogManager.getLogger(CustomerRepository.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Customer customer) {
        try {
            em.persist(customer);
        } catch (Exception e) {
            logger.error("Erro ao salvar Customer", e);
            throw e;
        }
    }

    public Customer findById(Long id) {
        try {
            return em.find(Customer.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar Customer por id", e);
            throw e;
        }
    }

    public List<Customer> findAll() {
        try {
            return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os Customers", e);
            throw e;
        }
    }

    @Transactional
    public void update(Customer customer) {
        try {
            em.merge(customer);
        } catch (Exception e) {
            logger.error("Erro ao atualizar Customer", e);
            throw e;
        }
    }

    @Transactional
    public void delete(Customer customer) {
        try {
            em.remove(em.contains(customer) ? customer : em.merge(customer));
        } catch (Exception e) {
            logger.error("Erro ao deletar Customer", e);
            throw e;
        }
    }
}

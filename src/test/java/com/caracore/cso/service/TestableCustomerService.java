package com.caracore.cso.service;

import com.caracore.cso.entity.Customer;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.repository.TestJPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Versão testável do CustomerService
 */
public class TestableCustomerService {
    private static final Logger logger = LogManager.getLogger(TestableCustomerService.class);
    private final boolean isTestMode;
    
    public TestableCustomerService() {
        this.isTestMode = false;
    }
    
    public TestableCustomerService(boolean testMode) {
        this.isTestMode = testMode;
    }
    
    private EntityManager getEntityManager() {
        return isTestMode ? TestJPAUtil.getEntityManager() : JPAUtil.getEntityManager();
    }

    public void save(Customer customer) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            if (customer.getId() == null) {
                em.persist(customer);
            } else {
                em.merge(customer);
            }
            
            em.getTransaction().commit();
            logger.info("[DEBUG] Customer salvo com sucesso. ID: {}", customer.getId());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao salvar customer", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public Customer findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Customer.class, id);
        } finally {
            em.close();
        }
    }

    public List<Customer> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Customer> findByBusiness(Long businessId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c WHERE c.business.id = :businessId", Customer.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Customer customer = em.find(Customer.class, id);
            if (customer != null) {
                em.remove(customer);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Customer findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class);
            query.setParameter("email", email);
            List<Customer> customers = query.getResultList();
            return customers.isEmpty() ? null : customers.get(0);
        } finally {
            em.close();
        }
    }
}



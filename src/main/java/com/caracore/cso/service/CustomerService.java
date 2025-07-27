package com.caracore.cso.service;


import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.User;
import com.caracore.cso.repository.JPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;

import java.util.List;

public class CustomerService {
    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    public List<Customer> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c", Customer.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os customers", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void save(Customer customer) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(customer);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar customer", e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Customer customer) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(customer);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar customer", e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Long customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Customer customer = em.find(Customer.class, customerId);
            if (customer != null) {
                em.remove(customer);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao deletar customer id: " + customerId, e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Customer findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Customer.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar customer por id: " + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Customer> findAllByBusiness(Long businessId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c WHERE c.business.id = :businessId", Customer.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar customers por businessId: " + businessId, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateFactorAndPriceTable(Long customerId, double factor, String priceTable) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE Customer SET factorCustomer = :factor, priceTable = :priceTable WHERE id = :customerId");
            query.setParameter("factor", factor);
            query.setParameter("priceTable", priceTable);
            query.setParameter("customerId", customerId);
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar fator e tabela de preço do customer id: " + customerId, e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteById(Long customerId) {
        delete(customerId);
    }

    public boolean canAccessDelivery(com.caracore.cso.entity.User customer, com.caracore.cso.entity.Delivery delivery) {
        try {
            if (customer == null || delivery == null) return false;
            return "CUSTOMER".equalsIgnoreCase(customer.getRole()) &&
                   delivery.getCustomer() != null &&
                   delivery.getCustomer().getUser() != null &&
                   customer.getId().equals(delivery.getCustomer().getUser().getId());
        } catch (Exception e) {
            logger.error("Erro ao verificar acesso do customer à delivery", e);
            return false;
        }
    }
}

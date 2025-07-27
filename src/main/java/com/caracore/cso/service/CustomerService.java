package com.caracore.cso.service;


import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.User;
import com.caracore.cso.repository.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CustomerService {
    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    public List<Customer> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Customer", Customer.class).list();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os customers", e);
            throw e;
        }
    }

    public void save(Customer customer) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(customer);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar customer", e);
            throw e;
        }
    }

    public void update(Customer customer) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(customer);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar customer", e);
            throw e;
        }
    }

    public void delete(Long customerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Customer customer = session.get(Customer.class, customerId);
            if (customer != null) {
                session.delete(customer);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao deletar customer id: " + customerId, e);
            throw e;
        }
    }

    public Customer findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Customer.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar customer por id: " + id, e);
            throw e;
        }
    }

    public List<Customer> findAllByBusiness(Long businessId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Customer> query = session.createQuery("FROM Customer WHERE business.id = :businessId", Customer.class);
            query.setParameter("businessId", businessId);
            return query.list();
        } catch (Exception e) {
            logger.error("Erro ao buscar customers por businessId: " + businessId, e);
            throw e;
        }
    }

    public void updateFactorAndPriceTable(Long customerId, double factor, String priceTable) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("UPDATE Customer SET factorCustomer = :factor, priceTable = :priceTable WHERE id = :customerId");
            query.setParameter("factor", factor);
            query.setParameter("priceTable", priceTable);
            query.setParameter("customerId", customerId);
            query.executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar fator e tabela de preço do customer id: " + customerId, e);
            throw e;
        }
    }

    public void deleteById(Long customerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Customer customer = session.get(Customer.class, customerId);
            if (customer != null) {
                session.delete(customer);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao deletar customer por id: " + customerId, e);
            throw e;
        }
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

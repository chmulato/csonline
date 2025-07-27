package com.caracore.cso.service;

import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.User;
import com.caracore.cso.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class CustomerService {
    public Customer findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Customer.class, id);
        }
    }

    public List<Customer> findAllByBusiness(Long businessId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Customer> query = session.createQuery("FROM Customer WHERE business.id = :businessId", Customer.class);
            query.setParameter("businessId", businessId);
            return query.list();
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
        }
    }
    public boolean canAccessDelivery(com.caracore.cso.entity.User customer, com.caracore.cso.entity.Delivery delivery) {
        if (customer == null || delivery == null) return false;
        // O cliente pode acessar se for o usuário vinculado ao customer da entrega
        return "CUSTOMER".equalsIgnoreCase(customer.getRole()) &&
               delivery.getCustomer() != null &&
               delivery.getCustomer().getUser() != null &&
               customer.getId().equals(delivery.getCustomer().getUser().getId());
    }
}

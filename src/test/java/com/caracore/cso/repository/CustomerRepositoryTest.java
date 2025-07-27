package com.caracore.cso.repository;

import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest {
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeEach
    void setUp() {
        em = Persistence.createEntityManagerFactory("csonlinePU").createEntityManager();
        tx = em.getTransaction();
        tx.begin();
    }

    @AfterEach
    void tearDown() {
        if (tx.isActive()) {
            tx.rollback();
        }
        em.close();
    }

    @Test
    void testCRUD() {
        User business = new User();
        business.setId(110L);
        business.setRole("BUSINESS");
        business.setName("Biz");
        business.setLogin("biz");
        business.setPassword("pass");
        em.persist(business);

        User user = new User();
        user.setId(111L);
        user.setRole("CUSTOMER");
        user.setName("Customer");
        user.setLogin("cust");
        user.setPassword("pass");
        em.persist(user);

        Customer customer = new Customer();
        customer.setId(112L);
        customer.setBusiness(business);
        customer.setUser(user);
        customer.setFactorCustomer(1.5);
        customer.setPriceTable("A");
        em.persist(customer);
        em.flush();

        Customer found = em.find(Customer.class, 112L);
        assertNotNull(found);
        assertEquals("A", found.getPriceTable());

        found.setPriceTable("B");
        em.merge(found);
        em.flush();
        Customer updated = em.find(Customer.class, 112L);
        assertEquals("B", updated.getPriceTable());

        em.remove(updated);
        em.flush();
        assertNull(em.find(Customer.class, 112L));
    }
}

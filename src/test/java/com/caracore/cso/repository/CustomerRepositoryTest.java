package com.caracore.cso.repository;

import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest {
    private Session session;
    private Transaction tx;

    @BeforeEach
    void setUp() {
        session = HibernateUtil.getSessionFactory().openSession();
        tx = session.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        tx.rollback();
        session.close();
    }

    @Test
    void testCRUD() {
        User business = new User();
        business.setId(110L);
        business.setRole("BUSINESS");
        business.setName("Biz");
        business.setLogin("biz");
        business.setPassword("pass");
        session.save(business);

        User user = new User();
        user.setId(111L);
        user.setRole("CUSTOMER");
        user.setName("Customer");
        user.setLogin("cust");
        user.setPassword("pass");
        session.save(user);

        Customer customer = new Customer();
        customer.setId(112L);
        customer.setBusiness(business);
        customer.setUser(user);
        customer.setFactorCustomer(1.5);
        customer.setPriceTable("A");
        session.save(customer);
        session.flush();

        Customer found = session.get(Customer.class, 112L);
        assertNotNull(found);
        assertEquals("A", found.getPriceTable());

        found.setPriceTable("B");
        session.update(found);
        session.flush();
        Customer updated = session.get(Customer.class, 112L);
        assertEquals("B", updated.getPriceTable());

        session.delete(updated);
        session.flush();
        assertNull(session.get(Customer.class, 112L));
    }
}

package com.caracore.cso.repository;

import com.caracore.cso.entity.Courier;
import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import static org.junit.jupiter.api.Assertions.*;

class CourierRepositoryTest {
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
        business.setId(100L);
        business.setRole("BUSINESS");
        business.setName("Biz2");
        business.setLogin("biz2");
        business.setPassword("pass");
        session.save(business);

        User user = new User();
        user.setId(101L);
        user.setRole("COURIER");
        user.setName("Courier");
        user.setLogin("cour");
        user.setPassword("pass");
        session.save(user);

        Courier courier = new Courier();
        courier.setId(200L);
        courier.setBusiness(business);
        courier.setUser(user);
        courier.setFactorCourier(2.0);
        session.save(courier);
        session.flush();

        Courier found = session.get(Courier.class, 200L);
        assertNotNull(found);
        assertEquals(2.0, found.getFactorCourier());

        found.setFactorCourier(3.0);
        session.update(found);
        session.flush();
        Courier updated = session.get(Courier.class, 200L);
        assertEquals(3.0, updated.getFactorCourier());

        session.delete(updated);
        session.flush();
        assertNull(session.get(Courier.class, 200L));
    }
}

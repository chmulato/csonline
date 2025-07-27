package com.caracore.cso.repository;

import com.caracore.cso.entity.Courier;
import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

class CourierRepositoryTest {
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
        business.setId(100L);
        business.setRole("BUSINESS");
        business.setName("Biz2");
        business.setLogin("biz2");
        business.setPassword("pass");
        em.persist(business);

        User user = new User();
        user.setId(101L);
        user.setRole("COURIER");
        user.setName("Courier");
        user.setLogin("cour");
        user.setPassword("pass");
        em.persist(user);

        Courier courier = new Courier();
        courier.setId(200L);
        courier.setBusiness(business);
        courier.setUser(user);
        courier.setFactorCourier(2.0);
        em.persist(courier);
        em.flush();

        Courier found = em.find(Courier.class, 200L);
        assertNotNull(found);
        assertEquals(2.0, found.getFactorCourier());

        found.setFactorCourier(3.0);
        em.merge(found);
        em.flush();
        Courier updated = em.find(Courier.class, 200L);
        assertEquals(3.0, updated.getFactorCourier());

        em.remove(updated);
        em.flush();
        assertNull(em.find(Courier.class, 200L));
    }
}

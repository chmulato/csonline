package com.caracore.cso.repository;

import com.caracore.cso.entity.Delivery;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.Courier;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryRepositoryTest {
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
        business.setId(120L);
        business.setRole("BUSINESS");
        business.setName("Biz3");
        business.setLogin("biz3");
        business.setPassword("pass");
        em.persist(business);

        User user = new User();
        user.setId(121L);
        user.setRole("CUSTOMER");
        user.setName("Customer2");
        user.setLogin("cust2");
        user.setPassword("pass");
        em.persist(user);

        Customer customer = new Customer();
        customer.setId(122L);
        customer.setBusiness(business);
        customer.setUser(user);
        customer.setFactorCustomer(1.2);
        customer.setPriceTable("C");
        em.persist(customer);

        User courierUser = new User();
        courierUser.setId(123L);
        courierUser.setRole("COURIER");
        courierUser.setName("Courier2");
        courierUser.setLogin("cour2");
        courierUser.setPassword("pass");
        em.persist(courierUser);

        Courier courier = new Courier();
        courier.setId(124L);
        courier.setBusiness(business);
        courier.setUser(courierUser);
        courier.setFactorCourier(2.5);
        em.persist(courier);

        Delivery delivery = new Delivery();
        delivery.setId(125L);
        delivery.setBusiness(business);
        delivery.setCustomer(customer);
        delivery.setCourier(courier);
        delivery.setStart("A");
        delivery.setDestination("B");
        delivery.setContact("Contact");
        delivery.setDescription("Desc");
        delivery.setVolume("10");
        delivery.setWeight("5");
        delivery.setKm("2");
        delivery.setAdditionalCost(1.0);
        delivery.setCost(10.0);
        delivery.setReceived(true);
        delivery.setCompleted(false);
        delivery.setDatatime(LocalDateTime.now());
        em.persist(delivery);
        em.flush();

        Delivery found = em.find(Delivery.class, 125L);
        assertNotNull(found);
        assertEquals("A", found.getStart());

        found.setStart("C");
        em.merge(found);
        em.flush();
        Delivery updated = em.find(Delivery.class, 125L);
        assertEquals("C", updated.getStart());

        em.remove(updated);
        em.flush();
        assertNull(em.find(Delivery.class, 125L));
    }
}

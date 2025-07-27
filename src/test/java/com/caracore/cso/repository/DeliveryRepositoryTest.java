package com.caracore.cso.repository;

import com.caracore.cso.entity.Delivery;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.Courier;
import org.junit.jupiter.api.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryRepositoryTest {
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
        business.setId(120L);
        business.setRole("BUSINESS");
        business.setName("Biz3");
        business.setLogin("biz3");
        business.setPassword("pass");
        session.save(business);

        User user = new User();
        user.setId(121L);
        user.setRole("CUSTOMER");
        user.setName("Customer2");
        user.setLogin("cust2");
        user.setPassword("pass");
        session.save(user);

        Customer customer = new Customer();
        customer.setId(122L);
        customer.setBusiness(business);
        customer.setUser(user);
        customer.setFactorCustomer(1.2);
        customer.setPriceTable("C");
        session.save(customer);

        User courierUser = new User();
        courierUser.setId(123L);
        courierUser.setRole("COURIER");
        courierUser.setName("Courier2");
        courierUser.setLogin("cour2");
        courierUser.setPassword("pass");
        session.save(courierUser);

        Courier courier = new Courier();
        courier.setId(124L);
        courier.setBusiness(business);
        courier.setUser(courierUser);
        courier.setFactorCourier(2.5);
        session.save(courier);

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
        session.save(delivery);
        session.flush();

        Delivery found = session.get(Delivery.class, 125L);
        assertNotNull(found);
        assertEquals("A", found.getStart());

        found.setStart("C");
        session.update(found);
        session.flush();
        Delivery updated = session.get(Delivery.class, 125L);
        assertEquals("C", updated.getStart());

        session.delete(updated);
        session.flush();
        assertNull(session.get(Delivery.class, 125L));
    }
}

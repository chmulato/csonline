package com.caracore.cso.repository;

import com.caracore.cso.entity.Price;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

class PriceRepositoryTest {
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
        business.setId(130L);
        business.setRole("BUSINESS");
        business.setName("Biz4");
        business.setLogin("biz4");
        business.setPassword("pass");
        em.persist(business);

        Customer customer = new Customer();
        customer.setId(131L);
        customer.setBusiness(business);
        customer.setFactorCustomer(1.1);
        customer.setPriceTable("D");
        em.persist(customer);

        Price price = new Price();
        price.setId(132L);
        price.setBusiness(business);
        price.setCustomer(customer);
        price.setTableName("Tabela1");
        price.setVehicle("Carro");
        price.setLocal("Local1");
        price.setPrice(100.0);
        em.persist(price);
        em.flush();

        Price found = em.find(Price.class, 132L);
        assertNotNull(found);
        assertEquals("Tabela1", found.getTableName());

        found.setTableName("Tabela2");
        em.merge(found);
        em.flush();
        Price updated = em.find(Price.class, 132L);
        assertEquals("Tabela2", updated.getTableName());

        em.remove(updated);
        em.flush();
        assertNull(em.find(Price.class, 132L));
    }
}

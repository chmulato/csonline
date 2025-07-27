package com.caracore.cso.repository;

import com.caracore.cso.entity.Price;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import org.junit.jupiter.api.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import static org.junit.jupiter.api.Assertions.*;

class PriceRepositoryTest {
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
        business.setId(130L);
        business.setRole("BUSINESS");
        business.setName("Biz4");
        business.setLogin("biz4");
        business.setPassword("pass");
        session.save(business);

        Customer customer = new Customer();
        customer.setId(131L);
        customer.setBusiness(business);
        customer.setFactorCustomer(1.1);
        customer.setPriceTable("D");
        session.save(customer);

        Price price = new Price();
        price.setId(132L);
        price.setBusiness(business);
        price.setCustomer(customer);
        price.setTableName("Tabela1");
        price.setVehicle("Carro");
        price.setLocal("Local1");
        price.setPrice(100.0);
        session.save(price);
        session.flush();

        Price found = session.get(Price.class, 132L);
        assertNotNull(found);
        assertEquals("Tabela1", found.getTableName());

        found.setTableName("Tabela2");
        session.update(found);
        session.flush();
        Price updated = session.get(Price.class, 132L);
        assertEquals("Tabela2", updated.getTableName());

        session.delete(updated);
        session.flush();
        assertNull(session.get(Price.class, 132L));
    }
}

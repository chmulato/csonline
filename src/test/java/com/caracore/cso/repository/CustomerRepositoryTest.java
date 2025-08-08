
package com.caracore.cso.repository;
import com.caracore.cso.factory.UserFactory;
import com.caracore.cso.factory.CustomerFactory;

import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CustomerRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(CustomerRepositoryTest.class);
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeEach
    void setUp() {
        try {
            em = Persistence.createEntityManagerFactory("csonlinePU").createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            // Limpa tabelas relevantes para garantir isolamento
            em.createQuery("DELETE FROM Team").executeUpdate();
            em.createQuery("DELETE FROM Customer").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            tx.commit();
            tx.begin();
        } catch (Exception e) {
            logger.error("Erro ao iniciar EntityManager ou transação", e);
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        } catch (Exception e) {
            logger.error("Erro ao finalizar transação ou fechar EntityManager", e);
        }
    }

    @Test
    void testCRUD() {
        try {
            User business = UserFactory.createUniqueUser();
            business.setRole("BUSINESS");
            em.persist(business);
            User user = UserFactory.createUniqueUser();
            user.setRole("CUSTOMER");
            em.persist(user);
            Customer customer = CustomerFactory.createUniqueCustomer(business);
            customer.setUser(user);
            em.persist(customer);
            em.flush();

            Customer found = em.find(Customer.class, customer.getId());
            assertNotNull(found);
            assertEquals(customer.getPriceTable(), found.getPriceTable());

            found.setPriceTable("B");
            em.merge(found);
            em.flush();
            Customer updated = em.find(Customer.class, customer.getId());
            assertEquals("B", updated.getPriceTable());

            em.remove(updated);
            em.flush();
            assertNull(em.find(Customer.class, customer.getId()));
        } catch (Exception e) {
            logger.error("Erro durante o teste CRUD do CustomerRepository", e);
            throw e;
        }
    }
}



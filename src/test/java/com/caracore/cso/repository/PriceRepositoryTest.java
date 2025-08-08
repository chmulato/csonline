
package com.caracore.cso.repository;
import com.caracore.cso.factory.UserFactory;
import com.caracore.cso.factory.CustomerFactory;
import com.caracore.cso.factory.PriceFactory;

import com.caracore.cso.entity.Price;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PriceRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(PriceRepositoryTest.class);
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeEach
    void setUp() {
        try {
            em = Persistence.createEntityManagerFactory("csonlinePU").createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            // Limpa tabelas relevantes para garantir isolamento
            em.createQuery("DELETE FROM Price").executeUpdate();
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
            Customer customer = CustomerFactory.createUniqueCustomer(business);
            em.persist(customer);
            Price price = PriceFactory.createUniquePrice(business, customer);
            em.persist(price);
            em.flush();

            Price found = em.find(Price.class, price.getId());
            assertNotNull(found);
            assertEquals(price.getTableName(), found.getTableName());

            found.setTableName("Tabela2");
            em.merge(found);
            em.flush();
            Price updated = em.find(Price.class, price.getId());
            assertEquals("Tabela2", updated.getTableName());

            em.remove(updated);
            em.flush();
            assertNull(em.find(Price.class, price.getId()));
        } catch (Exception e) {
            logger.error("Erro durante o teste CRUD do PriceRepository", e);
            throw e;
        }
    }
}



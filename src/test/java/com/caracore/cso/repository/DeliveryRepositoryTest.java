
package com.caracore.cso.repository;
import com.caracore.cso.factory.UserFactory;
import com.caracore.cso.factory.CustomerFactory;
import com.caracore.cso.factory.CourierFactory;
import com.caracore.cso.factory.DeliveryFullFactory;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DeliveryRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryRepositoryTest.class);
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeEach
    void setUp() {
        try {
            em = Persistence.createEntityManagerFactory("csonlinePU").createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            // Limpa tabelas relevantes para garantir isolamento
            em.createQuery("DELETE FROM Delivery").executeUpdate();
            em.createQuery("DELETE FROM Team").executeUpdate();
            em.createQuery("DELETE FROM Courier").executeUpdate();
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
            User courierUser = UserFactory.createUniqueUser();
            courierUser.setRole("COURIER");
            em.persist(courierUser);
            Courier courier = CourierFactory.createUniqueCourier(business, courierUser);
            em.persist(courier);
            Delivery delivery = DeliveryFullFactory.createUniqueDelivery(business, customer, courier);
            em.persist(delivery);
            em.flush();

            Delivery found = em.find(Delivery.class, delivery.getId());
            assertNotNull(found);
            assertEquals(delivery.getStart(), found.getStart());

            found.setStart("C");
            em.merge(found);
            em.flush();
            Delivery updated = em.find(Delivery.class, delivery.getId());
            assertEquals("C", updated.getStart());

            em.remove(updated);
            em.flush();
            assertNull(em.find(Delivery.class, delivery.getId()));
        } catch (Exception e) {
            logger.error("Erro durante o teste CRUD do DeliveryRepository", e);
            throw e;
        }
    }
}

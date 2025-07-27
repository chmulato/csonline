package com.caracore.cso.repository;

import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import com.caracore.cso.repository.JPAUtil;
import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        em.close();
    }

    @Test
    void testCRUD() {
        User user = new User();
        user.setId(150L);
        user.setRole("ADMIN");
        user.setName("Test User");
        user.setLogin("test");
        user.setPassword("123");
        em.persist(user);
        em.flush();

        User found = em.find(User.class, 150L);
        assertNotNull(found);
        assertEquals("Test User", found.getName());

        found.setName("Updated");
        em.merge(found);
        em.flush();
        User updated = em.find(User.class, 150L);
        assertEquals("Updated", updated.getName());

        em.remove(updated);
        em.flush();
        assertNull(em.find(User.class, 150L));
    }
}

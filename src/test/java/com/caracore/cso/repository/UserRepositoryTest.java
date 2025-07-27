package com.caracore.cso.repository;

import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
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
        User user = new User();
        user.setId(150L);
        user.setRole("ADMIN");
        user.setName("Test User");
        user.setLogin("test");
        user.setPassword("123");
        session.save(user);
        session.flush();

        User found = session.get(User.class, 150L);
        assertNotNull(found);
        assertEquals("Test User", found.getName());

        found.setName("Updated");
        session.update(found);
        session.flush();
        User updated = session.get(User.class, 150L);
        assertEquals("Updated", updated.getName());

        session.delete(updated);
        session.flush();
        assertNull(session.get(User.class, 150L));
    }
}

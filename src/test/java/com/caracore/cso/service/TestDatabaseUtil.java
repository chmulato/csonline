package com.caracore.cso.service;

import com.caracore.cso.repository.JPAUtil;
import jakarta.persistence.EntityManager;

public class TestDatabaseUtil {
    public static void clearDatabase() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM SMS").executeUpdate();
            em.createQuery("DELETE FROM Delivery").executeUpdate();
            em.createQuery("DELETE FROM Price").executeUpdate();
            em.createQuery("DELETE FROM Customer").executeUpdate();
            em.createQuery("DELETE FROM Courier").executeUpdate();
            em.createQuery("DELETE FROM Team").executeUpdate();
            em.createNativeQuery("DELETE FROM app_user").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

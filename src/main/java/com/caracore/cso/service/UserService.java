package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class UserService {
    public User findByLoginAndPassword(String login, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE login = :login AND password = :password", User.class);
            query.setParameter("login", login);
            query.setParameter("password", password);
            return query.uniqueResult();
        }
    }

    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    public User findByLogin(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE login = :login", User.class);
            query.setParameter("login", login);
            return query.uniqueResult();
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User ORDER BY role, name", User.class).list();
        }
    }

    public List<User> findAllBusiness() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE role = 'BUSINESS' ORDER BY name", User.class).list();
        }
    }

    public void updateLoginPassword(String login, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<?> query = session.createQuery("UPDATE User SET password = :password WHERE login = :login");
            query.setParameter("password", password);
            query.setParameter("login", login);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    // Outros métodos conforme as queries do InterfaceSQL podem ser adicionados aqui

    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}

package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);

    public void save(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar usuário", e);
            throw e;
        }
    }

    public void update(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário", e);
            throw e;
        }
    }

    public void delete(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                session.delete(user);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao deletar usuário id: " + userId, e);
            throw e;
        }
    }

    public User findByLoginAndPassword(String login, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE login = :login AND password = :password", User.class);
            query.setParameter("login", login);
            query.setParameter("password", password);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário por login e senha: " + login, e);
            throw e;
        }
    }

    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário por id: " + id, e);
            throw e;
        }
    }

    public User findByLogin(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE login = :login", User.class);
            query.setParameter("login", login);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário por login: " + login, e);
            throw e;
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User ORDER BY role, name", User.class).list();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os usuários", e);
            throw e;
        }
    }

    public List<User> findAllBusiness() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE role = 'BUSINESS' ORDER BY name", User.class).list();
        } catch (Exception e) {
            logger.error("Erro ao buscar usuários BUSINESS", e);
            throw e;
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
        } catch (Exception e) {
            logger.error("Erro ao atualizar senha do usuário: " + login, e);
            throw e;
        }
    }

    // Outros métodos conforme as queries do InterfaceSQL podem ser adicionados aqui

    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}


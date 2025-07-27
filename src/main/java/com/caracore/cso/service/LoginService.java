package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class LoginService {
    /**
     * Autentica o usuário pelo login e senha.
     * @param login login do usuário
     * @param password senha do usuário
     * @return User autenticado
     * @throws SecurityException se não encontrar ou senha inválida
     */
    public User authenticate(String login, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                "FROM User WHERE login = :login", User.class);
            query.setParameter("login", login);
            User user = query.uniqueResult();
            if (user == null || !user.getPassword().equals(password)) {
                throw new SecurityException("Login ou senha inválidos");
            }
            return user;
        }
    }
}

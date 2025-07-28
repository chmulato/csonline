
package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Courier;
import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.Delivery;
import com.caracore.cso.repository.JPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);

    public void save(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (user.getId() == null) {
                Long nextId = ((Number) em.createQuery("SELECT COALESCE(MAX(u.id), 0) + 1 FROM User u").getSingleResult()).longValue();
                user.setId(nextId);
            }
            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar usuário", e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(User user) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário", e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                // Remove registros da tabela team relacionados ao usuário (query nativa)
                jakarta.persistence.Query teamDelete = em.createNativeQuery("DELETE FROM team WHERE idbusiness = ?");
                teamDelete.setParameter(1, userId);
                teamDelete.executeUpdate();

                // Remove explicitamente os filhos para garantir deleção em cascata
                if (user.getCouriers() != null) {
                    for (var courier : user.getCouriers()) {
                        em.remove(courier);
                    }
                    user.getCouriers().clear();
                }
                if (user.getCustomers() != null) {
                    for (var customer : user.getCustomers()) {
                        em.remove(customer);
                    }
                    user.getCustomers().clear();
                }
                if (user.getDeliveries() != null) {
                    for (var delivery : user.getDeliveries()) {
                        em.remove(delivery);
                    }
                    user.getDeliveries().clear();
                }
                em.remove(user);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            try { em.getTransaction().rollback(); } catch (Exception ex) { /* ignora */ }
            String msg = e.getMessage();
            // Garante mensagem amigável mesmo após erro de transação
            if (msg != null && msg.contains("integrity constraint violation")) {
                logger.warn("Não foi possível deletar o usuário id: " + userId + ". Existem clientes ou registros vinculados a este usuário.");
                throw new RuntimeException("Não foi possível deletar o usuário. Existem clientes ou registros vinculados a este usuário.");
            } else if (e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().contains("integrity constraint violation")) {
                logger.warn("Não foi possível deletar o usuário id: " + userId + ". Existem clientes ou registros vinculados a este usuário.");
                throw new RuntimeException("Não foi possível deletar o usuário. Existem clientes ou registros vinculados a este usuário.");
            } else {
                logger.error("Erro ao deletar usuário id: " + userId, e);
                throw e;
            }
        } finally {
            em.close();
        }
    }

    public User findByLoginAndPassword(String login, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.login = :login AND u.password = :password", User.class);
            query.setParameter("login", login);
            query.setParameter("password", password);
            return query.getResultStream().findFirst().orElse(null);
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário por login e senha: " + login, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public User findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(User.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário por id: " + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public User findByLogin(String login) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class);
            query.setParameter("login", login);
            return query.getResultStream().findFirst().orElse(null);
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário por login: " + login, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public java.util.List<User> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u ORDER BY u.role, u.name", User.class);
            return new java.util.ArrayList<>(query.getResultList());
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os usuários", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<User> findAllBusiness() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.role = 'BUSINESS' ORDER BY u.name", User.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar usuários BUSINESS", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateLoginPassword(String login, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            jakarta.persistence.Query query = em.createQuery("UPDATE User SET password = :password WHERE login = :login");
            query.setParameter("password", password);
            query.setParameter("login", login);
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar senha do usuário: " + login, e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Outros métodos conforme as queries do InterfaceSQL podem ser adicionados aqui

    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}


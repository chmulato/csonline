package com.caracore.cso.repository;

import com.caracore.cso.entity.Courier;
import com.caracore.cso.exception.DAOException;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CourierRepository {
    private static final Logger logger = LogManager.getLogger(CourierRepository.class);

    public void save(Courier courier) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(courier);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.error("Erro ao salvar Courier", e);
            throw new DAOException("Erro ao salvar Courier", e);
        } finally {
            em.close();
        }
    }

    public Courier findById(Long id) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Courier.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar Courier por id", e);
            throw new DAOException("Erro ao buscar Courier por id", e);
        } finally {
            em.close();
        }
    }

    public List<Courier> findAll() throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("FROM Courier", Courier.class).getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os Couriers", e);
            throw new DAOException("Erro ao buscar todos os Couriers", e);
        } finally {
            em.close();
        }
    }

    public void update(Courier courier) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(courier);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.error("Erro ao atualizar Courier", e);
            throw new DAOException("Erro ao atualizar Courier", e);
        } finally {
            em.close();
        }
    }

    public void delete(Courier courier) throws DAOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.contains(courier) ? courier : em.merge(courier));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.error("Erro ao deletar Courier", e);
            throw new DAOException("Erro ao deletar Courier", e);
        } finally {
            em.close();
        }
    }
}

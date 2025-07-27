package com.caracore.cso.repository;

import com.caracore.cso.entity.Courier;
import com.caracore.cso.exception.DAOException;
import com.caracore.cso.repository.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CourierRepository {
    private static final Logger logger = LogManager.getLogger(CourierRepository.class);

    public void save(Courier courier) throws DAOException {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(courier);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Erro ao salvar Courier", e);
            throw new DAOException("Erro ao salvar Courier", e);
        }
    }

    public Courier findById(Long id) throws DAOException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Courier.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar Courier por id", e);
            throw new DAOException("Erro ao buscar Courier por id", e);
        }
    }

    public List<Courier> findAll() throws DAOException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Courier", Courier.class).list();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os Couriers", e);
            throw new DAOException("Erro ao buscar todos os Couriers", e);
        }
    }

    public void update(Courier courier) throws DAOException {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(courier);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Erro ao atualizar Courier", e);
            throw new DAOException("Erro ao atualizar Courier", e);
        }
    }

    public void delete(Courier courier) throws DAOException {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(courier);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Erro ao deletar Courier", e);
            throw new DAOException("Erro ao deletar Courier", e);
        }
    }
}

package com.caracore.cso.repository;

import com.caracore.cso.entity.Price;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PriceRepository {
    private static final Logger logger = LogManager.getLogger(PriceRepository.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Price price) {
        try {
            em.persist(price);
        } catch (Exception e) {
            logger.error("Erro ao salvar Price", e);
            throw e;
        }
    }

    public Price findById(Long id) {
        try {
            return em.find(Price.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar Price por id", e);
            throw e;
        }
    }

    public List<Price> findAll() {
        try {
            return em.createQuery("SELECT p FROM Price p", Price.class).getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os Prices", e);
            throw e;
        }
    }

    @Transactional
    public void update(Price price) {
        try {
            em.merge(price);
        } catch (Exception e) {
            logger.error("Erro ao atualizar Price", e);
            throw e;
        }
    }

    @Transactional
    public void delete(Price price) {
        try {
            em.remove(em.contains(price) ? price : em.merge(price));
        } catch (Exception e) {
            logger.error("Erro ao deletar Price", e);
            throw e;
        }
    }
}

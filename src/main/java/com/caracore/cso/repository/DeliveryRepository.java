package com.caracore.cso.repository;

import com.caracore.cso.entity.Delivery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeliveryRepository {
    private static final Logger logger = LogManager.getLogger(DeliveryRepository.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Delivery delivery) {
        try {
            em.persist(delivery);
        } catch (Exception e) {
            logger.error("Erro ao salvar Delivery", e);
            throw e;
        }
    }

    public Delivery findById(Long id) {
        try {
            return em.find(Delivery.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar Delivery por id", e);
            throw e;
        }
    }

    public List<Delivery> findAll() {
        try {
            return em.createQuery("SELECT d FROM Delivery d", Delivery.class).getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as Deliveries", e);
            throw e;
        }
    }

    @Transactional
    public void update(Delivery delivery) {
        try {
            em.merge(delivery);
        } catch (Exception e) {
            logger.error("Erro ao atualizar Delivery", e);
            throw e;
        }
    }

    @Transactional
    public void delete(Delivery delivery) {
        try {
            em.remove(em.contains(delivery) ? delivery : em.merge(delivery));
        } catch (Exception e) {
            logger.error("Erro ao deletar Delivery", e);
            throw e;
        }
    }
}

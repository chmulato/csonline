package com.caracore.cso.repository;

import com.caracore.cso.entity.SMS;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SMSRepository {
    private static final Logger logger = LogManager.getLogger(SMSRepository.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(SMS sms) {
        try {
            em.persist(sms);
        } catch (Exception e) {
            logger.error("Erro ao salvar SMS", e);
            throw e;
        }
    }

    public SMS findById(Long id) {
        try {
            return em.find(SMS.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar SMS por id", e);
            throw e;
        }
    }

    public List<SMS> findAll() {
        try {
            return em.createQuery("SELECT s FROM SMS s", SMS.class).getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as SMS", e);
            throw e;
        }
    }

    @Transactional
    public void update(SMS sms) {
        try {
            em.merge(sms);
        } catch (Exception e) {
            logger.error("Erro ao atualizar SMS", e);
            throw e;
        }
    }

    @Transactional
    public void delete(SMS sms) {
        try {
            em.remove(em.contains(sms) ? sms : em.merge(sms));
        } catch (Exception e) {
            logger.error("Erro ao deletar SMS", e);
            throw e;
        }
    }
}

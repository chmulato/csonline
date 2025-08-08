package com.caracore.cso.service;

import com.caracore.cso.entity.SMS;
import com.caracore.cso.repository.JPAUtil;
import com.caracore.cso.repository.TestJPAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * Versão testável do SMSService
 */
public class TestableSMSService {
    private static final Logger logger = LogManager.getLogger(TestableSMSService.class);
    private final boolean isTestMode;
    
    public TestableSMSService() {
        this.isTestMode = false;
    }
    
    public TestableSMSService(boolean testMode) {
        this.isTestMode = testMode;
    }
    
    private EntityManager getEntityManager() {
        return isTestMode ? TestJPAUtil.getEntityManager() : JPAUtil.getEntityManager();
    }

    public void save(SMS sms) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            if (sms.getId() == null) {
                em.persist(sms);
            } else {
                em.merge(sms);
            }
            
            em.getTransaction().commit();
            logger.info("[DEBUG] SMS salvo com sucesso. ID: {}", sms.getId());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao salvar SMS", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public SMS findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SMS.class, id);
        } finally {
            em.close();
        }
    }

    public List<SMS> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s FROM SMS s", SMS.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<SMS> findByBusiness(Long businessId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<SMS> query = em.createQuery("SELECT s FROM SMS s WHERE s.business.id = :businessId", SMS.class);
            query.setParameter("businessId", businessId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<SMS> findByPhone(String phone) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<SMS> query = em.createQuery("SELECT s FROM SMS s WHERE s.phone = :phone", SMS.class);
            query.setParameter("phone", phone);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            SMS sms = em.find(SMS.class, id);
            if (sms != null) {
                if (sms.getDelivery() != null) {
                    em.getTransaction().rollback();
                    throw new RuntimeException("Não foi possível deletar o SMS. Existem registros vinculados a este SMS.");
                }
                em.remove(sms);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao deletar SMS por id: " + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void sendDeliverySMS(Long deliveryId, String fromMobile, String toMobile, String type, String message, Integer piece, String datetime) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            SMS sms = new SMS();
            sms.setDelivery(em.find(com.caracore.cso.entity.Delivery.class, deliveryId));
            sms.setMobileFrom(fromMobile);
            sms.setMobileTo(toMobile);
            sms.setType(type);
            sms.setMessage(message);
            sms.setPiece(piece);
            sms.setDatetime(datetime);
            // Garante que o campo ID seja atribuído
            Long nextId = ((Number) em.createQuery("SELECT COALESCE(MAX(s.id), 0) + 1 FROM SMS s").getSingleResult()).longValue();
            sms.setId(nextId);
            em.persist(sms);
            em.getTransaction().commit();
            logger.info("SMS enviado para delivery: {}", deliveryId);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Erro ao enviar SMS de delivery", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<SMS> getDeliverySMSHistory(Long deliveryId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<SMS> query = em.createQuery("SELECT s FROM SMS s WHERE s.delivery.id = :deliveryId ORDER BY s.datetime ASC", SMS.class);
            query.setParameter("deliveryId", deliveryId);
            List<SMS> result = query.getResultList();
            logger.info("getDeliverySMSHistory encontrou {} SMS para delivery: {}", result.size(), deliveryId);
            return result;
        } catch (Exception e) {
            logger.error("Erro ao consultar histórico de SMS da delivery: " + deliveryId, e);
            throw e;
        } finally {
            em.close();
        }
    }
}



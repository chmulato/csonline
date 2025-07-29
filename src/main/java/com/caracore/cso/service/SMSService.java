package com.caracore.cso.service;

import com.caracore.cso.entity.SMS;
import com.caracore.cso.repository.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class SMSService {

    private static final Logger logger = LogManager.getLogger(SMSService.class);

    public List<SMS> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<SMS> query = em.createQuery("SELECT s FROM SMS s ORDER BY s.datetime ASC", SMS.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as SMS", e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void save(SMS sms) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Garante que a Delivery está gerenciada
            if (sms.getDelivery() != null && sms.getDelivery().getId() != null) {
                sms.setDelivery(em.find(com.caracore.cso.entity.Delivery.class, sms.getDelivery().getId()));
            }
            // Validação de unicidade: não pode haver outro SMS com mesmo delivery, type e piece
            if (sms.getDelivery() != null && sms.getType() != null && sms.getPiece() != null) {
                Long count = em.createQuery(
                    "SELECT COUNT(s) FROM SMS s WHERE s.delivery.id = :deliveryId AND s.type = :type AND s.piece = :piece", Long.class)
                    .setParameter("deliveryId", sms.getDelivery().getId())
                    .setParameter("type", sms.getType())
                    .setParameter("piece", sms.getPiece())
                    .getSingleResult();
                if (count > 0) {
                    throw new IllegalArgumentException("Já existe um SMS para esta entrega, tipo e piece.");
                }
            }
            // Garante que o campo ID seja atribuído se não estiver definido
            if (sms.getId() == null) {
                Long nextId = ((Number) em.createQuery("SELECT COALESCE(MAX(s.id), 0) + 1 FROM SMS s").getSingleResult()).longValue();
                sms.setId(nextId);
            }
            em.persist(sms);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar SMS", e);
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public SMS findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(SMS.class, id);
        } catch (Exception e) {
            logger.error("Erro ao buscar SMS por id: " + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<SMS> findAllByDelivery(Long deliveryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<SMS> query = em.createQuery("SELECT s FROM SMS s WHERE s.delivery.id = :deliveryId ORDER BY s.type, s.piece", SMS.class);
            query.setParameter("deliveryId", deliveryId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao buscar SMS por deliveryId: " + deliveryId, e);
            throw e;
        } finally {
            em.close();
        }
    }

    // Registra uma mensagem SMS entre courier e customer para uma entrega
    public void sendDeliverySMS(Long deliveryId, String fromMobile, String toMobile, String type, String message, Integer piece, String datetime) {
        EntityManager em = JPAUtil.getEntityManager();
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
        } catch (Exception e) {
            logger.error("Erro ao enviar SMS de delivery", e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Consulta o histórico de mensagens SMS de uma entrega
    public List<SMS> getDeliverySMSHistory(Long deliveryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<SMS> query = em.createQuery("SELECT s FROM SMS s WHERE s.delivery.id = :deliveryId ORDER BY s.datetime ASC", SMS.class);
            query.setParameter("deliveryId", deliveryId);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Erro ao consultar histórico de SMS da delivery: " + deliveryId, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateDeliveryId(Long smsId, Long deliveryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE SMS SET delivery.id = :deliveryId WHERE id = :smsId");
            query.setParameter("deliveryId", deliveryId);
            query.setParameter("smsId", smsId);
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao atualizar deliveryId da SMS id: " + smsId, e);
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteById(Long smsId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            SMS sms = em.find(SMS.class, smsId);
            if (sms != null) {
                em.remove(sms);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao deletar SMS por id: " + smsId, e);
            em.getTransaction().rollback();
            String msg = e.getMessage();
            if ((msg != null && msg.contains("integrity constraint violation")) ||
                (e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().contains("integrity constraint violation"))) {
                throw new com.caracore.cso.exception.ReferentialIntegrityException("Não foi possível deletar o SMS. Existem registros vinculados a este SMS.", e);
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
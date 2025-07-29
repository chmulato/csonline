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
            em.getTransaction().begin();
            // Garante que business e customer estão gerenciados
            if (price.getBusiness() != null && price.getBusiness().getId() != null) {
                price.setBusiness(em.find(com.caracore.cso.entity.User.class, price.getBusiness().getId()));
            }
            if (price.getCustomer() != null && price.getCustomer().getId() != null) {
                price.setCustomer(em.find(com.caracore.cso.entity.Customer.class, price.getCustomer().getId()));
            }
            // Validação de unicidade: não pode haver outro Price com mesmo business, customer, tableName, vehicle e local
            if (price.getBusiness() != null && price.getCustomer() != null && price.getTableName() != null && price.getVehicle() != null && price.getLocal() != null) {
                String jpql = "SELECT COUNT(p) FROM Price p WHERE p.business.id = :businessId AND p.customer.id = :customerId AND p.tableName = :tableName AND p.vehicle = :vehicle AND p.local = :local" +
                        (price.getId() != null ? " AND p.id <> :id" : "");
                var query = em.createQuery(jpql, Long.class)
                        .setParameter("businessId", price.getBusiness().getId())
                        .setParameter("customerId", price.getCustomer().getId())
                        .setParameter("tableName", price.getTableName())
                        .setParameter("vehicle", price.getVehicle())
                        .setParameter("local", price.getLocal());
                if (price.getId() != null) {
                    query.setParameter("id", price.getId());
                }
                Long count = query.getSingleResult();
                if (count > 0) {
                    throw new IllegalArgumentException("Já existe um Price para esta combinação de business, customer, tableName, vehicle e local.");
                }
            }
            if (price.getId() == null) {
                em.persist(price);
            } else {
                em.merge(price);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao salvar Price", e);
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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

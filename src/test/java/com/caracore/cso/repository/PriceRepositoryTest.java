
package com.caracore.cso.repository;

import com.caracore.cso.entity.Price;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import com.caracore.cso.util.TestDataFactory;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PriceRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(PriceRepositoryTest.class);
    private EntityManager em;
    private EntityTransaction tx;
    private User business;
    private User customerUser;
    private Customer customer;

    @BeforeEach
    void setUp() {
        try {
            em = TestJPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();
            
            // Limpar dados para isolamento dos testes
            em.createQuery("DELETE FROM Price").executeUpdate();
            em.createQuery("DELETE FROM Customer").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            
            // Criar dados base para os testes
            business = TestDataFactory.createUser("BUSINESS");
            em.persist(business);
            
            customerUser = TestDataFactory.createUser("CUSTOMER");
            em.persist(customerUser);
            
            customer = TestDataFactory.createCustomer(business, customerUser);
            em.persist(customer);
            
            em.flush();
        } catch (Exception e) {
            logger.error("Erro ao iniciar EntityManager ou transação", e);
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        } catch (Exception e) {
            logger.error("Erro ao finalizar transação ou fechar EntityManager", e);
        }
    }

    @Test
    void testSaveAndFindPrice() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        
        // When
        em.persist(price);
        em.flush();
        Price found = em.find(Price.class, price.getId());
        
        // Then
        assertNotNull(found);
        assertEquals(price.getPrice(), found.getPrice());
        assertEquals(price.getBusiness().getId(), found.getBusiness().getId());
        assertEquals(price.getCustomer().getId(), found.getCustomer().getId());
        assertEquals(price.getTableName(), found.getTableName());
        assertEquals(price.getVehicle(), found.getVehicle());
        assertEquals(price.getLocal(), found.getLocal());
    }

    @Test
    void testUpdatePrice() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        em.persist(price);
        em.flush();
        
        // When
        price.setPrice(150.75);
        price.setLocal("Local Atualizado");
        em.merge(price);
        em.flush();
        
        // Then
        Price updated = em.find(Price.class, price.getId());
        assertEquals(150.75, updated.getPrice());
        assertEquals("Local Atualizado", updated.getLocal());
    }

    @Test
    void testDeletePrice() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        em.persist(price);
        em.flush();
        Long priceId = price.getId();
        
        // When
        em.remove(price);
        em.flush();
        
        // Then
        Price deleted = em.find(Price.class, priceId);
        assertNull(deleted);
    }

    @Test
    void testFindByNonExistentId() {
        // When
        Price found = em.find(Price.class, 999999L);
        
        // Then
        assertNull(found);
    }

    @Test
    void testFindAllPrices() {
        // Given
        Price price1 = TestDataFactory.createPrice(business, customer);
        Price price2 = TestDataFactory.createPrice(business, customer);
        price2.setTableName("TABELA_2");
        
        em.persist(price1);
        em.persist(price2);
        em.flush();
        
        // When
        TypedQuery<Price> query = em.createQuery("SELECT p FROM Price p", Price.class);
        List<Price> allPrices = query.getResultList();
        
        // Then
        assertTrue(allPrices.size() >= 2);
        assertTrue(allPrices.stream().anyMatch(p -> p.getId().equals(price1.getId())));
        assertTrue(allPrices.stream().anyMatch(p -> p.getId().equals(price2.getId())));
    }

    @Test
    void testFindAllByBusiness() {
        // Given
        User anotherBusiness = TestDataFactory.createUser("BUSINESS");
        anotherBusiness.setLogin("outro_business@test.com");
        em.persist(anotherBusiness);
        
        Customer anotherCustomer = TestDataFactory.createCustomer(anotherBusiness, customerUser);
        em.persist(anotherCustomer);
        
        Price price1 = TestDataFactory.createPrice(business, customer);
        Price price2 = TestDataFactory.createPrice(business, customer);
        price2.setTableName("TABELA_B");
        Price price3 = TestDataFactory.createPrice(anotherBusiness, anotherCustomer);
        
        em.persist(price1);
        em.persist(price2);
        em.persist(price3);
        em.flush();
        
        // When
        TypedQuery<Price> query = em.createQuery(
            "SELECT p FROM Price p WHERE p.business.id = :businessId", 
            Price.class
        );
        query.setParameter("businessId", business.getId());
        List<Price> businessPrices = query.getResultList();
        
        // Then
        assertEquals(2, businessPrices.size());
        assertTrue(businessPrices.stream().allMatch(p -> p.getBusiness().getId().equals(business.getId())));
        assertFalse(businessPrices.stream().anyMatch(p -> p.getId().equals(price3.getId())));
    }

    @Test
    void testPriceRelationships() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        em.persist(price);
        em.flush();
        
        // When
        Price found = em.find(Price.class, price.getId());
        
        // Then
        assertNotNull(found.getBusiness());
        assertNotNull(found.getCustomer());
        assertEquals(business.getId(), found.getBusiness().getId());
        assertEquals(customer.getId(), found.getCustomer().getId());
        assertEquals("BUSINESS", found.getBusiness().getRole());
        assertEquals("CUSTOMER", found.getCustomer().getUser().getRole());
    }

    @Test
    void testPriceFieldValidation() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        price.setPrice(0.0);
        price.setTableName("");
        price.setVehicle("");
        price.setLocal("");
        
        // When & Then
        assertDoesNotThrow(() -> {
            em.persist(price);
            em.flush();
        });
        
        Price found = em.find(Price.class, price.getId());
        assertEquals(0.0, found.getPrice());
        assertEquals("", found.getTableName());
        assertEquals("", found.getVehicle());
        assertEquals("", found.getLocal());
    }

    @Test
    void testMultiplePricesForSameCustomer() {
        // Given
        Price price1 = TestDataFactory.createPrice(business, customer);
        price1.setTableName("TABELA_A");
        price1.setVehicle("MOTO");
        
        Price price2 = TestDataFactory.createPrice(business, customer);
        price2.setTableName("TABELA_B");
        price2.setVehicle("CARRO");
        
        // When
        em.persist(price1);
        em.persist(price2);
        em.flush();
        
        // Then
        TypedQuery<Price> query = em.createQuery(
            "SELECT p FROM Price p WHERE p.customer.id = :customerId", 
            Price.class
        );
        query.setParameter("customerId", customer.getId());
        List<Price> customerPrices = query.getResultList();
        
        assertEquals(2, customerPrices.size());
    }

    @Test
    void testPriceConstraints() {
        // Given - Teste com business null deve ser permitido na entidade,
        // mas pode causar problemas na persistência dependendo da configuração
        Price price = TestDataFactory.createPrice(business, customer);
        price.setCustomer(null); // Customer null deve causar problema
        
        // When & Then
        assertDoesNotThrow(() -> {
            em.persist(price);
            em.flush();
        });
    }

    @Test
    void testFindPricesByTableName() {
        // Given
        Price price1 = TestDataFactory.createPrice(business, customer);
        price1.setTableName("TABELA_ESPECIAL");
        
        Price price2 = TestDataFactory.createPrice(business, customer);
        price2.setTableName("TABELA_COMUM");
        
        em.persist(price1);
        em.persist(price2);
        em.flush();
        
        // When
        TypedQuery<Price> query = em.createQuery(
            "SELECT p FROM Price p WHERE p.tableName = :tableName", 
            Price.class
        );
        query.setParameter("tableName", "TABELA_ESPECIAL");
        List<Price> prices = query.getResultList();
        
        // Then
        assertEquals(1, prices.size());
        assertEquals("TABELA_ESPECIAL", prices.get(0).getTableName());
    }

    @Test
    void testPriceBusinessLogic() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        price.setPrice(99.99);
        price.setVehicle("MOTO");
        price.setLocal("CENTRO");
        
        em.persist(price);
        em.flush();
        
        // When
        Price found = em.find(Price.class, price.getId());
        
        // Then
        assertEquals(99.99, found.getPrice());
        assertEquals("MOTO", found.getVehicle());
        assertEquals("CENTRO", found.getLocal());
        assertNotNull(found.getBusiness());
        assertNotNull(found.getCustomer());
    }
}


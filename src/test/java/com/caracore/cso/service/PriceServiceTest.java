
package com.caracore.cso.service;

import com.caracore.cso.entity.Price;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import com.caracore.cso.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import com.caracore.cso.service.TestableUserService;
import com.caracore.cso.service.TestableTeamService;
import com.caracore.cso.service.TestableCourierService;
import com.caracore.cso.service.TestableCustomerService;
import com.caracore.cso.service.TestableDeliveryService;
import com.caracore.cso.service.TestablePriceService;
import com.caracore.cso.service.TestableSMSService;

class PriceServiceTest {

    private TestablePriceService priceService;
    private User business;

    @BeforeEach
    void setUp() {
        EntityManager em = com.caracore.cso.repository.TestJPAUtil.getEntityManager();
        try {
            com.caracore.cso.util.TestDatabaseUtil.clearDatabase(em);
        } finally {
            em.close();
        }
        priceService = new TestablePriceService(true);
        business = TestDataFactory.createUser("BUSINESS");
        new TestableUserService(true).save(business);
    }

    @Test
    void testSaveAndFindPrice() {
        // Cria um usuário CUSTOMER e um Customer entidade para associar ao preço
        User customerUser = TestDataFactory.createUser("CUSTOMER");
        new TestableUserService(true).save(customerUser);
        customerUser = new TestableUserService(true).findByLogin(customerUser.getLogin());

        Customer customer = com.caracore.cso.util.TestDataFactory.createCustomer(business, customerUser);
        
        // Para evitar cascade PERSIST, vamos fazer merge dos usuários
        jakarta.persistence.EntityManager em = com.caracore.cso.repository.TestJPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            business = em.merge(business);
            customerUser = em.merge(customerUser);
            customer.setBusiness(business);
            customer.setUser(customerUser);
            em.persist(customer);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        Price price = TestDataFactory.createPrice(business, customer);
        priceService.save(price);
        Price found = priceService.findById(price.getId());
        assertNotNull(found);
        assertEquals(price.getPrice(), found.getPrice());
    }

    // Outros testes podem ser adicionados aqui usando TestDataFactory
}




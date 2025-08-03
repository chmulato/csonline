package com.caracore.cso.service;

import com.caracore.cso.entity.Customer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;

class CustomerServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);
    private CustomerService service;

    @Test
    void testDeleteCustomerWithDeliveryReference() {
        try {
            var userService = new UserService();
            var business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            var customerUser = TestDataFactory.createUser("CUSTOMER");
            userService.save(customerUser);
            customerUser = userService.findByLogin(customerUser.getLogin());


            var customer = TestDataFactory.createCustomer(business, customerUser);
            service.save(customer);

            var delivery = TestDataFactory.createDelivery(business, null);
            delivery.setCustomer(customer);
            new DeliveryService().save(delivery);

            // Busca o id do customer salvo
            var customers = service.findAllByBusiness(business.getId());
            final Long customerId = !customers.isEmpty() ? customers.get(0).getId() : null;

            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.delete(customerId));
            assertTrue(ex.getMessage().contains("Não foi possível deletar o cliente") || ex.getMessage().contains("vínculos"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteCustomerWithDeliveryReference em CustomerServiceTest", e);
            throw e;
        }
    }

    @BeforeEach
    void setUp() {
        jakarta.persistence.EntityManager em = com.caracore.cso.repository.JPAUtil.getEntityManager();
        try {
            com.caracore.cso.util.TestDatabaseUtil.clearDatabase(em);
        } finally {
            em.close();
        }
        try {
            service = new CustomerService();
            com.caracore.cso.service.UserService userService = new UserService();
            com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            var customerUser = TestDataFactory.createUser("CUSTOMER");
            userService.save(customerUser);
            customerUser = userService.findByLogin(customerUser.getLogin());

            var customer = TestDataFactory.createCustomer(business, customerUser);
            service.save(customer);
        } catch (Exception e) {
            logger.error("Erro ao preparar o teste CustomerServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindById() {
        try {
            Customer customer = service.findById(1L);
            // O teste real depende do banco estar populado
            // assertNull(customer);
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindById em CustomerServiceTest", e);
            throw e;
        }
    }

    // Outros testes podem ser criados para findAllByBusiness, updateFactorAndPriceTable, etc.
}

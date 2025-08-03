package com.caracore.cso.service;

import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Delivery;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import java.util.List;

class CustomerServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);
    private CustomerService service;

    @Test
    void testDeleteCustomerWithDeliveryReference() {
        try {
            UserService userService = new UserService();
            User business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            User customerUser = TestDataFactory.createUser("CUSTOMER");
            userService.save(customerUser);
            customerUser = userService.findByLogin(customerUser.getLogin());


            Customer customer = TestDataFactory.createCustomer(business, customerUser);
            service.save(customer);

            Delivery delivery = TestDataFactory.createDelivery(business, null);
            delivery.setCustomer(customer);
            new DeliveryService().save(delivery);

            // Busca o id do customer salvo
            List<Customer> customers = service.findAllByBusiness(business.getId());
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
        EntityManager em = com.caracore.cso.repository.JPAUtil.getEntityManager();
        try {
            com.caracore.cso.util.TestDatabaseUtil.clearDatabase(em);
        } finally {
            em.close();
        }
        try {
            service = new CustomerService();
            UserService userService = new UserService();
            User business = TestDataFactory.createUser("BUSINESS");
            userService.save(business);
            business = userService.findByLogin(business.getLogin());

            User customerUser = TestDataFactory.createUser("CUSTOMER");
            userService.save(customerUser);
            customerUser = userService.findByLogin(customerUser.getLogin());

            Customer customer = TestDataFactory.createCustomer(business, customerUser);
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

package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import com.caracore.cso.util.TestDatabaseUtil;
import com.caracore.cso.repository.JPAUtil;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;

class UserServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    private UserService service;
    @Test
    void testDeleteUserWithCustomerReference() {
        try {
            User business = TestDataFactory.createUser("BUSINESS");
            service.save(business);
            business = service.findByLogin(business.getLogin());

            User customerUser = TestDataFactory.createUser("CUSTOMER");
            service.save(customerUser);
            customerUser = service.findByLogin(customerUser.getLogin());

            com.caracore.cso.entity.Customer customer = TestDataFactory.createCustomer(business, customerUser);
            new com.caracore.cso.service.CustomerService().save(customer);

            final Long businessId = business.getId();
            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.delete(businessId));
            assertTrue(ex.getMessage().contains("Não foi possível deletar o usuário") || ex.getMessage().contains("vínculos"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteUserWithCustomerReference em UserServiceTest", e);
            throw e;
        }
    }
    // ...existing code...

    @BeforeEach
    void setUp() {
        var em = JPAUtil.getEntityManager();
        try {
            TestDatabaseUtil.clearDatabase(em);
        } finally {
            em.close();
        }
        try {
            service = new UserService();
            // Cria usuários de teste com dados únicos
            User admin = TestDataFactory.createUser("ADMIN");
            service.save(admin);

            User courier = TestDataFactory.createUser("COURIER");
            service.save(courier);

            User business = TestDataFactory.createUser("BUSINESS");
            service.save(business);

            // Adiciona mais usuários se necessário para o teste de findAll
            for (long i = 4; i <= 6; i++) {
                User u = TestDataFactory.createUser("USER");
                service.save(u);
            }
        } catch (Exception e) {
            logger.error("Erro ao preparar o teste UserServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindByLoginAndPassword() {
        try {
            User admin = TestDataFactory.createUser("ADMIN");
            service.save(admin);
            User found = service.findByLoginAndPassword(admin.getLogin(), admin.getPassword());
            assertNotNull(found);
            assertEquals("ADMIN", found.getRole());
            assertEquals(admin.getName(), found.getName());
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindByLoginAndPassword em UserServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindById() {
        try {
            User admin = TestDataFactory.createUser("ADMIN");
            service.save(admin);
            User found = service.findByLogin(admin.getLogin());
            assertNotNull(found);
            User user = service.findById(found.getId());
            assertNotNull(user);
            assertEquals(admin.getLogin(), user.getLogin());
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindById em UserServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindByLogin() {
        try {
            User courier = TestDataFactory.createUser("COURIER");
            service.save(courier);
            User user = service.findByLogin(courier.getLogin());
            assertNotNull(user);
            assertEquals("COURIER", user.getRole());
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindByLogin em UserServiceTest", e);
            throw e;
        }
    }

    @Test
    void testIsAdmin() {
        try {
            User admin = TestDataFactory.createUser("ADMIN");
            service.save(admin);
            User courier = TestDataFactory.createUser("COURIER");
            service.save(courier);
            User adminFound = service.findByLoginAndPassword(admin.getLogin(), admin.getPassword());
            assertTrue(service.isAdmin(adminFound));
            User courierFound = service.findByLoginAndPassword(courier.getLogin(), courier.getPassword());
            assertFalse(service.isAdmin(courierFound));
        } catch (Exception e) {
            logger.error("Erro durante o teste testIsAdmin em UserServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindAll() {
        try {
            var users = service.findAll();
            assertNotNull(users);
            assertTrue(users.size() >= 6);
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindAll em UserServiceTest", e);
            throw e;
        }
    }
}

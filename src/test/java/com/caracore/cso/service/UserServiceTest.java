package com.caracore.cso.service;

import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;
import java.util.List;

class UserServiceTest extends BaseServiceTest {
    @Test
    void testNaoPermiteDuplicidadeDeLoginOuEmail() {
        User user1 = TestDataFactory.createUser("ADMIN");
        service.save(user1);
        // Tenta criar outro usuário com mesmo login
        User user2 = TestDataFactory.createUser("ADMIN");
        user2.setLogin(user1.getLogin());
        user2.setEmail("email_unico@test.com");
        Exception ex1 = assertThrows(IllegalArgumentException.class, () -> service.save(user2));
        assertTrue(ex1.getMessage().toLowerCase().contains("login"));

        // Tenta criar outro usuário com mesmo email
        User user3 = TestDataFactory.createUser("ADMIN");
        user3.setEmail(user1.getEmail());
        user3.setLogin("login_unico_para_email");
        Exception ex2 = assertThrows(IllegalArgumentException.class, () -> service.save(user3));
        assertTrue(ex2.getMessage().toLowerCase().contains("email"));
    }
    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    private TestableUserService service;
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

            final Long businessId = business.getId();
            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteById(businessId));
            assertTrue(ex.getMessage().contains("Não foi possível deletar o usuário") || ex.getMessage().contains("vínculos"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteUserWithCustomerReference em UserServiceTest", e);
            throw e;
        }
    }
    // ...existing code...

    @BeforeEach
    void setUp() {
        try {
            service = new TestableUserService(true);
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
            List<User> users = service.findAll();
            assertNotNull(users);
            assertTrue(users.size() >= 6);
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindAll em UserServiceTest", e);
            throw e;
        }
    }
}





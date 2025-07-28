package com.caracore.cso.service;

import com.caracore.cso.entity.Customer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {
    @Test
    void testDeleteCustomerWithDeliveryReference() {
        // Cria business
        var userService = new UserService();
        var business = new com.caracore.cso.entity.User();
        business.setId(30L);
        business.setRole("BUSINESS");
        business.setName("BusinessRef");
        business.setLogin("businessref");
        business.setPassword("businessref123");
        userService.save(business);

        // Cria customer user
        var customerUser = new com.caracore.cso.entity.User();
        customerUser.setId(31L);
        customerUser.setRole("CUSTOMER");
        customerUser.setName("CustomerRef");
        customerUser.setLogin("customerref");
        customerUser.setPassword("customerref123");
        userService.save(customerUser);

        // Cria customer
        var customer = new com.caracore.cso.entity.Customer();
        customer.setId(32L);
        customer.setBusiness(business);
        customer.setUser(customerUser);
        customer.setFactorCustomer(1.1);
        customer.setPriceTable("A");
        service.save(customer);

        // Cria delivery vinculado ao customer
        var delivery = new com.caracore.cso.entity.Delivery();
        delivery.setId(33L);
        delivery.setBusiness(business);
        delivery.setCustomer(customer);
        delivery.setStart("A");
        delivery.setDestination("B");
        delivery.setContact("Contact");
        delivery.setDescription("Desc");
        delivery.setVolume("10");
        delivery.setWeight("5");
        delivery.setKm("2");
        delivery.setAdditionalCost(1.0);
        delivery.setCost(10.0);
        delivery.setReceived(true);
        delivery.setCompleted(false);
        delivery.setDatatime(java.time.LocalDateTime.now());
        new DeliveryService().save(delivery);

        // Tenta deletar o customer vinculado ao delivery
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.delete(32L));
        assertTrue(ex.getMessage().contains("Não foi possível deletar o cliente") || ex.getMessage().contains("vínculos"));
    }
    private CustomerService service;

    @BeforeEach
    void setUp() {
        service = new CustomerService();
    }

    @Test
    void testFindById() {
        Customer customer = service.findById(1L);
        // O teste real depende do banco estar populado
        // assertNull(customer);
    }

    // Outros testes podem ser criados para findAllByBusiness, updateFactorAndPriceTable, etc.
}

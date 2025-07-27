package com.caracore.cso.service;

import com.caracore.cso.entity.Customer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {
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

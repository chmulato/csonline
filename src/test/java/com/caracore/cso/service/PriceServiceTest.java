package com.caracore.cso.service;

import com.caracore.cso.entity.Price;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class PriceServiceTest {
    private PriceService service;

    @BeforeEach
    void setUp() {
        service = new PriceService();
    }

    @Test
    void testFindById() {
        Price price = service.findById(1L);
        // O teste real depende do banco estar populado
        // assertNull(price);
    }

    // Outros testes podem ser criados para findAllByBusiness, updatePrice, etc.
}

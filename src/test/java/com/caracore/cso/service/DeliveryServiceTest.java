package com.caracore.cso.service;

import com.caracore.cso.entity.Delivery;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryServiceTest {
    private DeliveryService service;

    @BeforeEach
    void setUp() {
        service = new DeliveryService();
    }

    @Test
    void testFindById() {
        Delivery delivery = service.findById(1L);
        // O teste real depende do banco estar populado
        // assertNull(delivery);
    }

    // Outros testes podem ser criados para findAllByBusiness, updateDeliveryStatus, etc.
}

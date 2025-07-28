
package com.caracore.cso.service;

import com.caracore.cso.entity.Price;
import com.caracore.cso.entity.User;
import com.caracore.cso.util.TestDataFactory;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PriceServiceTest {

    private PriceService priceService;
    private User business;

    @BeforeEach
    void setUp() {
        priceService = new PriceService();
        business = TestDataFactory.createUser("BUSINESS");
        new UserService().save(business);
    }

    @Test
    void testSaveAndFindPrice() {
        // Cria um usuário CUSTOMER e um Customer entidade para associar ao preço
        var customerUser = TestDataFactory.createUser("CUSTOMER");
        new UserService().save(customerUser);
        customerUser = new UserService().findByLogin(customerUser.getLogin());

        var customer = com.caracore.cso.util.TestDataFactory.createCustomer(business, customerUser);
        new CustomerService().save(customer);

        Price price = TestDataFactory.createPrice(business, customer);
        priceService.save(price);
        Price found = priceService.findById(price.getId());
        assertNotNull(found);
        assertEquals(price.getPrice(), found.getPrice());
    }

    // Outros testes podem ser adicionados aqui usando TestDataFactory
}

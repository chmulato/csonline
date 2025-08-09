
package com.caracore.cso.service;

import com.caracore.cso.entity.Price;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import com.caracore.cso.util.TestDataFactory;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class PriceServiceTest extends BaseServiceTest {

    private TestablePriceService priceService;
    private TestableUserService userService;
    private TestableCustomerService customerService;
    private User business;
    private User customerUser;
    private Customer customer;

    @BeforeEach
    void setUp() {
        priceService = new TestablePriceService(true);
        userService = new TestableUserService(true);
        customerService = new TestableCustomerService(true);
        
        // Criar business user
        business = TestDataFactory.createUser("BUSINESS");
        userService.save(business);
        
        // Criar customer user e customer entity
        customerUser = TestDataFactory.createUser("CUSTOMER");
        userService.save(customerUser);
        
        customer = TestDataFactory.createCustomer(business, customerUser);
        customerService.save(customer);
    }

    @Test
    void testSaveAndFindPrice() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        
        // When
        priceService.save(price);
        Price found = priceService.findById(price.getId());
        
        // Then
        assertNotNull(found);
        assertEquals(price.getPrice(), found.getPrice());
        assertEquals(price.getBusiness().getId(), found.getBusiness().getId());
        assertEquals(price.getCustomer().getId(), found.getCustomer().getId());
        assertEquals(price.getTableName(), found.getTableName());
        assertEquals(price.getVehicle(), found.getVehicle());
        assertEquals(price.getLocal(), found.getLocal());
    }

    @Test
    void testSavePriceWithNullId() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        price.setId(null);
        
        // When
        priceService.save(price);
        
        // Then
        assertNotNull(price.getId());
        assertTrue(price.getId() > 0);
    }

    @Test
    void testSaveExistingPrice() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        priceService.save(price);
        
        // When
        price.setPrice(150.00);
        price.setLocal("Local Atualizado");
        priceService.save(price);
        
        // Then
        Price updated = priceService.findById(price.getId());
        assertEquals(150.00, updated.getPrice());
        assertEquals("Local Atualizado", updated.getLocal());
    }

    @Test
    void testFindByNonExistentId() {
        // When
        Price found = priceService.findById(999999L);
        
        // Then
        assertNull(found);
    }

    @Test
    void testFindAllPrices() {
        // Given
        Price price1 = TestDataFactory.createPrice(business, customer);
        Price price2 = TestDataFactory.createPrice(business, customer);
        price2.setTableName("TABELA_2");
        
        priceService.save(price1);
        priceService.save(price2);
        
        // When
        List<Price> allPrices = priceService.findAll();
        
        // Then
        assertTrue(allPrices.size() >= 2);
        assertTrue(allPrices.stream().anyMatch(p -> p.getId().equals(price1.getId())));
        assertTrue(allPrices.stream().anyMatch(p -> p.getId().equals(price2.getId())));
    }

    @Test
    void testFindAllByBusiness() {
        // Given
        User anotherBusiness = TestDataFactory.createUser("BUSINESS");
        anotherBusiness.setLogin("outro_business@test.com");
        userService.save(anotherBusiness);
        
        Customer anotherCustomer = TestDataFactory.createCustomer(anotherBusiness, customerUser);
        customerService.save(anotherCustomer);
        
        Price price1 = TestDataFactory.createPrice(business, customer);
        Price price2 = TestDataFactory.createPrice(business, customer);
        Price price3 = TestDataFactory.createPrice(anotherBusiness, anotherCustomer);
        
        priceService.save(price1);
        priceService.save(price2);
        priceService.save(price3);
        
        // When
        List<Price> businessPrices = priceService.findByBusiness(business.getId());
        
        // Then
        assertEquals(2, businessPrices.size());
        assertTrue(businessPrices.stream().allMatch(p -> p.getBusiness().getId().equals(business.getId())));
        assertFalse(businessPrices.stream().anyMatch(p -> p.getId().equals(price3.getId())));
    }

    @Test
    void testUpdatePrice() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        priceService.save(price);
        
        // When - Atualizar via modificação direta
        price.setPrice(99.99);
        priceService.save(price);
        
        // Then
        Price updated = priceService.findById(price.getId());
        assertEquals(99.99, updated.getPrice());
    }

    @Test
    void testUpdatePriceNonExistent() {
        // Given - Criar um price que não existe
        Price nonExistentPrice = TestDataFactory.createPrice(business, customer);
        nonExistentPrice.setId(999999L);
        
        // When & Then - Should not throw exception but won't update anything
        assertDoesNotThrow(() -> priceService.save(nonExistentPrice));
    }

    @Test
    void testDeletePrice() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        priceService.save(price);
        Long priceId = price.getId();
        
        // When
        priceService.deleteById(priceId);
        
        // Then
        Price deleted = priceService.findById(priceId);
        assertNull(deleted);
    }

    @Test
    void testDeleteNonExistentPrice() {
        // When & Then
        assertDoesNotThrow(() -> priceService.deleteById(999999L));
    }

    @Test
    void testSaveWithInvalidBusiness() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        price.setBusiness(null);
        
        // When & Then - Verificar se funciona sem business (depende da implementação)
        assertDoesNotThrow(() -> priceService.save(price));
        // Se quiser testar validação, pode verificar se o preço foi salvo corretamente
        assertNotNull(price.getId());
    }

    @Test
    void testSaveWithInvalidCustomer() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        price.setCustomer(null);
        
        // When & Then - Verificar se funciona sem customer (depende da implementação)
        assertDoesNotThrow(() -> priceService.save(price));
        // Se quiser testar validação, pode verificar se o preço foi salvo corretamente
        assertNotNull(price.getId());
    }

    @Test
    void testPriceBusinessLogic() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        price.setPrice(50.75);
        price.setVehicle("MOTO");
        price.setLocal("CENTRO");
        price.setTableName("PROMOCIONAL");
        
        // When
        priceService.save(price);
        Price saved = priceService.findById(price.getId());
        
        // Then
        assertEquals(50.75, saved.getPrice());
        assertEquals("MOTO", saved.getVehicle());
        assertEquals("CENTRO", saved.getLocal());
        assertEquals("PROMOCIONAL", saved.getTableName());
        assertEquals(business.getId(), saved.getBusiness().getId());
        assertEquals(customer.getId(), saved.getCustomer().getId());
    }

    @Test
    void testMultiplePricesForSameBusiness() {
        // Given
        Customer customer2 = TestDataFactory.createCustomer(business, customerUser);
        customerService.save(customer2);
        
        Price price1 = TestDataFactory.createPrice(business, customer);
        price1.setTableName("TABELA_A");
        Price price2 = TestDataFactory.createPrice(business, customer2);
        price2.setTableName("TABELA_B");
        
        // When
        priceService.save(price1);
        priceService.save(price2);
        
        // Then
        List<Price> businessPrices = priceService.findByBusiness(business.getId());
        assertEquals(2, businessPrices.size());
    }

    @Test
    void testPriceIdGeneration() {
        // Given
        Price price1 = TestDataFactory.createPrice(business, customer);
        Price price2 = TestDataFactory.createPrice(business, customer);
        price1.setId(null);
        price2.setId(null);
        
        // When
        priceService.save(price1);
        priceService.save(price2);
        
        // Then
        assertNotNull(price1.getId());
        assertNotNull(price2.getId());
        assertNotEquals(price1.getId(), price2.getId());
        assertTrue(price1.getId() > 0);
        assertTrue(price2.getId() > 0);
    }

    @Test
    void testUpdatePriceValue() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        price.setPrice(100.00);
        priceService.save(price);
        
        // When - Atualizar via modificação direta
        price.setPrice(125.50);
        priceService.save(price);
        
        // Then
        Price updated = priceService.findById(price.getId());
        assertEquals(125.50, updated.getPrice());
        // Outros campos devem permanecer inalterados
        assertEquals(price.getTableName(), updated.getTableName());
        assertEquals(price.getVehicle(), updated.getVehicle());
        assertEquals(price.getLocal(), updated.getLocal());
    }

    @Test
    void testServiceErrorHandling() {
        // Test error handling by passing invalid parameters
        assertThrows(Exception.class, () -> {
            priceService.findById(null);
        });
        
        assertThrows(Exception.class, () -> {
            priceService.save(null);
        });
        
        assertThrows(Exception.class, () -> {
            priceService.deleteById(null);
        });
    }

    @Test
    void testPriceWithZeroValue() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        price.setPrice(0.0);
        
        // When
        priceService.save(price);
        Price saved = priceService.findById(price.getId());
        
        // Then
        assertEquals(0.0, saved.getPrice());
    }

    @Test
    void testPriceWithNegativeValue() {
        // Given
        Price price = TestDataFactory.createPrice(business, customer);
        price.setPrice(-10.0);
        
        // When & Then - Should allow negative prices (business logic decision)
        assertDoesNotThrow(() -> priceService.save(price));
        
        Price saved = priceService.findById(price.getId());
        assertEquals(-10.0, saved.getPrice());
    }
}


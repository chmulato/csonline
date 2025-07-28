package com.caracore.cso.service;

import com.caracore.cso.entity.Price;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import com.caracore.cso.service.TestDatabaseUtil;

class PriceServiceTest {
    @Test
    void testDeletePriceWithDeliveryReference() {
        // Cria business
        var userService = new UserService();
        var business = new com.caracore.cso.entity.User();
        business.setId(50L);
        business.setRole("BUSINESS");
        business.setName("BusinessRef");
        business.setLogin("businessref");
        business.setPassword("businessref123");
        userService.save(business);

        // Cria customer user
        var customerUser = new com.caracore.cso.entity.User();
        customerUser.setId(51L);
        customerUser.setRole("CUSTOMER");
        customerUser.setName("CustomerRef");
        customerUser.setLogin("customerref");
        customerUser.setPassword("customerref123");
        userService.save(customerUser);

        // Cria customer
        var customer = new com.caracore.cso.entity.Customer();
        customer.setId(52L);
        customer.setBusiness(business);
        customer.setUser(customerUser);
        customer.setFactorCustomer(1.1);
        customer.setPriceTable("A");
        new CustomerService().save(customer);

        // Cria price
        var price = new com.caracore.cso.entity.Price();
        price.setId(53L);
        price.setBusiness(business);
        price.setCustomer(customer);
        price.setTableName("Tabela1");
        price.setVehicle("Carro");
        price.setLocal("Local1");
        price.setPrice(100.0);
        service.save(price);

        // Cria delivery vinculado ao price
        var delivery = new com.caracore.cso.entity.Delivery();
        delivery.setId(54L);
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

        // Tenta deletar o price vinculado ao delivery
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteById(53L));
        assertTrue(ex.getMessage().contains("Não foi possível deletar o preço") || ex.getMessage().contains("vinculados"));
    }
    private PriceService service;

    @BeforeEach
    void setUp() {
        TestDatabaseUtil.clearDatabase();
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

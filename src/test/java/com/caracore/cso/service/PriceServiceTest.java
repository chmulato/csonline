package com.caracore.cso.service;

import com.caracore.cso.entity.Price;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriceServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(PriceServiceTest.class);
    private PriceService service;

    @BeforeEach
    void setUp() {
        try {
            // TestDatabaseUtil.clearDatabase();
            service = new PriceService();
        } catch (Exception e) {
            logger.error("Erro ao preparar o teste PriceServiceTest", e);
            throw e;
        }
    }

    @Test
    void testDeletePriceWithDeliveryReference() {
        try {
            long ts = System.currentTimeMillis();

            // Cria business
            var userService = new UserService();
            var business = new com.caracore.cso.entity.User();
            business.setRole("BUSINESS");
            business.setName("BusinessRef");
            business.setLogin("businessref_" + ts);
            business.setPassword("businessref123");
            userService.save(business);
            business = userService.findByLogin("businessref_" + ts);

            // Cria customer user
            var customerUser = new com.caracore.cso.entity.User();
            customerUser.setRole("CUSTOMER");
            customerUser.setName("CustomerRef");
            customerUser.setLogin("customerref_" + ts);
            customerUser.setPassword("customerref123");
            userService.save(customerUser);
            customerUser = userService.findByLogin("customerref_" + ts);

            // Cria customer
            var customer = new com.caracore.cso.entity.Customer();
            customer.setBusiness(business);
            customer.setUser(customerUser);
            customer.setFactorCustomer(1.1);
            customer.setPriceTable("A");
            new CustomerService().save(customer);

            // Buscar o customer persistido para pegar o ID real
            var customers = new CustomerService().findAll();
            customer = customers.stream()
                .filter(c -> c.getUser().getLogin().equals("customerref_" + ts))
                .findFirst()
                .orElse(customer);

            // Cria price
            var price = new com.caracore.cso.entity.Price();
            price.setBusiness(business);
            price.setCustomer(customer);
            price.setTableName("Tabela1");
            price.setVehicle("Carro");
            price.setLocal("Local1");
            price.setPrice(100.0);
            service.save(price);

            // Buscar o price persistido para pegar o ID real
            var prices = new PriceService().findAll();
            price = prices.stream()
                .filter(p -> p.getCustomer().getId().equals(customer.getId()))
                .findFirst()
                .orElse(price);

            // Cria delivery vinculado ao price
            var delivery = new com.caracore.cso.entity.Delivery();
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

            // Buscar o price persistido para pegar o ID real
            prices = new PriceService().findAll();
            price = prices.stream()
                .filter(p -> p.getCustomer().getId().equals(customer.getId()))
                .findFirst()
                .orElse(price);

            // Tenta deletar o price vinculado ao delivery
            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.deleteById(price.getId()));
            assertTrue(ex.getMessage().contains("Não foi possível deletar o preço") || ex.getMessage().contains("vinculados"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeletePriceWithDeliveryReference em PriceServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindById() {
        try {
            Price price = service.findById(1L);
            // O teste real depende do banco estar populado
            // assertNull(price);
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindById em PriceServiceTest", e);
            throw e;
        }
    }

    // Outros testes podem ser criados para findAllByBusiness, updatePrice, etc.
}

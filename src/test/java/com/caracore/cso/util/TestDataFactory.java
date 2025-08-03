package com.caracore.cso.util;

import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Courier;
import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.Delivery;
import com.caracore.cso.entity.Price;
import com.caracore.cso.entity.SMS;
import com.caracore.cso.entity.Team;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestDataFactory {

    public static Price createPrice(User business, Customer customer) {
        Price price = new Price();
        price.setBusiness(business);
        price.setCustomer(customer);
        price.setTableName("Tabela_" + UUID.randomUUID().toString().substring(0, 8));
        price.setVehicle("Carro");
        price.setLocal("Local");
        price.setPrice(99.99);
        return price;
    }

    public static SMS createSMS(Delivery delivery) {
        SMS sms = new SMS();
        sms.setDelivery(delivery);
        sms.setPiece(1);
        sms.setType("INFO");
        sms.setMobileTo("+5511999999999");
        sms.setMobileFrom("+5511888888888");
        sms.setMessage("Mensagem de teste " + UUID.randomUUID().toString().substring(0, 8));
        sms.setDatetime(LocalDateTime.now().toString());
        return sms;
    }

    public static Team createTeam(User business, Courier courier) {
        Team team = new Team();
        team.setBusiness(business);
        team.setCourier(courier);
        team.setFactorCourier(1.0);
        return team;
    }
    public static User createUser(String role) {
        String unique = UUID.randomUUID().toString().replace("-", "");
        User user = new User();
        user.setId(null); // Garante que o ID será sempre gerado pelo banco
        user.setRole(role);
        user.setName(role + "_Name_" + unique);
        user.setLogin(role.toLowerCase() + "_login_" + unique);
        user.setPassword(role.toLowerCase() + "_pass_" + unique);
        user.setEmail(role.toLowerCase() + unique + "@test.com");
        user.setEmail2(role.toLowerCase() + unique + "2@test.com");
        user.setAddress("Rua Teste, 123 - " + unique);
        user.setMobile("+55" + (long)(Math.random()*1_000_000_0000L + 10_000_000_00L));
        return user;
    }

    public static Courier createCourier(User business, User courierUser) {
        Courier courier = new Courier();
        courier.setBusiness(business);
        courier.setUser(courierUser);
        courier.setFactorCourier(1.2);
        return courier;
    }

    public static Customer createCustomer(User business, User customerUser) {
        Customer customer = new Customer();
        customer.setBusiness(business);
        customer.setUser(customerUser);
        customer.setFactorCustomer(1.1);
        customer.setPriceTable("A");
        return customer;
    }

    public static Delivery createDelivery(User business, Courier courier) {
        Delivery delivery = new Delivery();
        delivery.setBusiness(business);
        delivery.setCourier(courier);
        delivery.setStart("Start");
        delivery.setDestination("Destination");
        delivery.setContact("Contact");
        delivery.setDescription("Description");
        delivery.setVolume("10");
        delivery.setWeight("5");
        delivery.setKm("2");
        delivery.setAdditionalCost(1.0);
        delivery.setCost(10.0);
        delivery.setReceived(true);
        delivery.setCompleted(false);
        delivery.setDatatime(LocalDateTime.now());
        return delivery;
    }
}

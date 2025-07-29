package com.caracore.cso.factory;

import com.caracore.cso.entity.Delivery;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.Courier;
import java.time.LocalDateTime;
import java.util.UUID;

public class DeliveryFullFactory {
    public static Delivery createUniqueDelivery(User business, Customer customer, Courier courier) {
        Delivery delivery = new Delivery();
        delivery.setBusiness(business);
        delivery.setCustomer(customer);
        delivery.setCourier(courier);
        delivery.setStart("A-" + UUID.randomUUID());
        delivery.setDestination("B-" + UUID.randomUUID());
        delivery.setContact("Contact-" + UUID.randomUUID());
        delivery.setDescription("Desc-" + UUID.randomUUID());
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

package com.caracore.cso.factory;

import com.caracore.cso.entity.Price;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import java.util.UUID;

public class PriceFactory {
    private static long idSeq = System.currentTimeMillis();
    public static Price createUniquePrice(User business, Customer customer) {
        Price price = new Price();
        price.setId(++idSeq);
        price.setBusiness(business);
        price.setCustomer(customer);
        price.setTableName("Tabela-" + UUID.randomUUID());
        price.setVehicle("Carro");
        price.setLocal("Local-" + UUID.randomUUID());
        price.setPrice(100.0);
        return price;
    }
}



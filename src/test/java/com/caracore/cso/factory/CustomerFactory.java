package com.caracore.cso.factory;

import com.caracore.cso.entity.Customer;
import com.caracore.cso.entity.User;
import java.util.UUID;

public class CustomerFactory {
    public static Customer createUniqueCustomer(User business) {
        Customer customer = new Customer();
        customer.setBusiness(business);
        customer.setFactorCustomer(1.1);
        customer.setPriceTable("D-" + UUID.randomUUID());
        return customer;
    }
}



package com.caracore.cso.factory;

import com.caracore.cso.entity.Courier;
import com.caracore.cso.entity.User;
import java.util.UUID;

public class CourierFactory {
    public static Courier createUniqueCourier(User business, User user) {
        Courier courier = new Courier();
        courier.setBusiness(business);
        courier.setUser(user);
        courier.setFactorCourier(2.5);
        return courier;
    }
}



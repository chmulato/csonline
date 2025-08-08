package com.caracore.cso.factory;

import com.caracore.cso.entity.User;
import java.util.UUID;

public class UserFactory {
    public static User createUniqueUser() {
        User user = new User();
        user.setRole("ADMIN");
        user.setName("Test User " + UUID.randomUUID());
        user.setLogin("user_" + UUID.randomUUID());
        user.setPassword("password");
        user.setEmail("email_" + UUID.randomUUID() + "@test.com");
        user.setEmail2("email2_" + UUID.randomUUID() + "@test.com");
        user.setAddress("Address " + UUID.randomUUID());
        user.setMobile("+55" + (int)(Math.random()*1000000000));
        return user;
    }
}



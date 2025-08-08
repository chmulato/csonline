package com.caracore.cso.service;

import com.caracore.cso.entity.User;

public class UserServiceTestHelper {
    public static void persistUser(User user) {
        new UserService().save(user);
    }
}



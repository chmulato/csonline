package com.caracore.cso.factory;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Courier;

public class TeamFactory {
    public static Team createTeam(User business, Courier courier, double factorCourier) {
        Team team = new Team();
        team.setBusiness(business);
        team.setCourier(courier);
        team.setFactorCourier(factorCourier);
        return team;
    }
}

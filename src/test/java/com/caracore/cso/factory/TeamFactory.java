package com.caracore.cso.factory;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;

public class TeamFactory {
    public static Team createTeam(User business, User courier, double factorCourier) {
        Team team = new Team();
        team.setBusiness(business);
        team.setCourier(courier);
        team.setFactorCourier(factorCourier);
        return team;
    }
}

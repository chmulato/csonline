package com.caracore.cso.service;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TeamServiceTest {
    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService();
        // Limpa todos os registros
        for (Team t : teamService.findAll()) {
            teamService.delete(t.getId());
        }
    }

    @Test
    void testSaveAndFindById() {
        Team team = new Team();
        team.setFactorCourier(1.5);
        User business = new User();
        business.setLogin("business_login");
        business.setName("Business Name");
        business.setPassword("123456");
        business.setRole("BUSINESS");
        User courier = new User();
        courier.setLogin("courier_login");
        courier.setName("Courier Name");
        courier.setPassword("654321");
        courier.setRole("COURIER");
        team.setBusiness(business);
        team.setCourier(courier);
        teamService.save(team);

        Team found = teamService.findById(team.getId());
        assertNotNull(found);
        assertEquals(1.5, found.getFactorCourier());
        assertEquals("Business Name", found.getBusiness().getName());
        assertEquals("Courier Name", found.getCourier().getName());
    }

    @Test
    void testFindAll() {
        Team team1 = new Team(); team1.setFactorCourier(1.1);
        Team team2 = new Team(); team2.setFactorCourier(2.2);
        teamService.save(team1);
        teamService.save(team2);
        List<Team> all = teamService.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testDelete() {
        Team team = new Team(); team.setFactorCourier(3.3);
        teamService.save(team);
        Long id = team.getId();
        teamService.delete(id);
        assertNull(teamService.findById(id));
    }
}

package com.caracore.cso.service;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TeamServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(TeamServiceTest.class);
    private TeamService teamService;
    @Test
    void testDeleteBusinessWithTeamReference() {
        try {
            // Cria um usuário BUSINESS
            User business = new User();
            business.setId(200L);
            business.setRole("BUSINESS");
            business.setName("BusinessRef");
            business.setLogin("businessref");
            business.setPassword("businessref123");
            new UserService().save(business);

            // Cria um usuário COURIER
            User courier = new User();
            courier.setId(201L);
            courier.setRole("COURIER");
            courier.setName("CourierRef");
            courier.setLogin("courierref");
            courier.setPassword("courierref123");
            new UserService().save(courier);

            // Cria um time vinculado ao business
            Team team = new Team();
            team.setBusiness(business);
            team.setCourier(courier);
            team.setFactorCourier(1.5);
            teamService.save(team);

            // Tenta deletar o usuário business vinculado ao time
            RuntimeException ex = assertThrows(RuntimeException.class, () -> new UserService().delete(200L));
            assertTrue(ex.getMessage().contains("Não é possível excluir o time") || ex.getMessage().contains("vinculados"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteBusinessWithTeamReference em TeamServiceTest", e);
            throw e;
        }
    }
    // ...existing code...

    @BeforeEach
    void setUp() {
        try {
            teamService = new TeamService();
            // Limpa todos os registros
            for (Team t : teamService.findAll()) {
                teamService.delete(t.getId());
            }
        } catch (Exception e) {
            logger.error("Erro ao preparar o teste TeamServiceTest", e);
            throw e;
        }
    }

    @Test
    void testSaveAndFindById() {
        try {
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
        } catch (Exception e) {
            logger.error("Erro durante o teste testSaveAndFindById em TeamServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindAll() {
        try {
            Team team1 = new Team(); team1.setFactorCourier(1.1);
            Team team2 = new Team(); team2.setFactorCourier(2.2);
            teamService.save(team1);
            teamService.save(team2);
            List<Team> all = teamService.findAll();
            assertEquals(2, all.size());
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindAll em TeamServiceTest", e);
            throw e;
        }
    }

    @Test
    void testDelete() {
        try {
            Team team = new Team(); team.setFactorCourier(3.3);
            teamService.save(team);
            Long id = team.getId();
            teamService.delete(id);
            assertNull(teamService.findById(id));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDelete em TeamServiceTest", e);
            throw e;
        }
    }
}

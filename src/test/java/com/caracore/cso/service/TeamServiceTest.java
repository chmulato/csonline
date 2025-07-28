package com.caracore.cso.service;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;

class TeamServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(TeamServiceTest.class);
    private TeamService teamService;
    @Test
    void testDeleteBusinessWithTeamReference() {
        try {
            // Cria usuários BUSINESS e COURIER únicos
            final User business = TestDataFactory.createUser("BUSINESS");
            new UserService().save(business);
            User businessPersisted = new UserService().findByLogin(business.getLogin());

            User courier = TestDataFactory.createUser("COURIER");
            new UserService().save(courier);
            courier = new UserService().findByLogin(courier.getLogin());

            // Cria um time vinculado ao business
            Team team = TestDataFactory.createTeam(businessPersisted, courier);
            teamService.save(team);

            // Tenta deletar o usuário business vinculado ao time
            RuntimeException ex = assertThrows(RuntimeException.class, () -> new UserService().delete(businessPersisted.getId()));
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
            User business = TestDataFactory.createUser("BUSINESS");
            new UserService().save(business);
            business = new UserService().findByLogin(business.getLogin());

            User courier = TestDataFactory.createUser("COURIER");
            new UserService().save(courier);
            courier = new UserService().findByLogin(courier.getLogin());

            Team team = TestDataFactory.createTeam(business, courier);
            teamService.save(team);

            Team found = teamService.findById(team.getId());
            assertNotNull(found);
            assertEquals(team.getFactorCourier(), found.getFactorCourier());
            assertEquals(business.getName(), found.getBusiness().getName());
            assertEquals(courier.getName(), found.getCourier().getName());
        } catch (Exception e) {
            logger.error("Erro durante o teste testSaveAndFindById em TeamServiceTest", e);
            throw e;
        }
    }

    @Test
    void testFindAll() {
        try {
            User business = TestDataFactory.createUser("BUSINESS");
            new UserService().save(business);
            business = new UserService().findByLogin(business.getLogin());

            User courier1 = TestDataFactory.createUser("COURIER");
            new UserService().save(courier1);
            courier1 = new UserService().findByLogin(courier1.getLogin());
            Team team1 = TestDataFactory.createTeam(business, courier1);
            team1.setFactorCourier(1.1);
            teamService.save(team1);

            User courier2 = TestDataFactory.createUser("COURIER");
            new UserService().save(courier2);
            courier2 = new UserService().findByLogin(courier2.getLogin());
            Team team2 = TestDataFactory.createTeam(business, courier2);
            team2.setFactorCourier(2.2);
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
            User business = TestDataFactory.createUser("BUSINESS");
            new UserService().save(business);
            business = new UserService().findByLogin(business.getLogin());

            User courier = TestDataFactory.createUser("COURIER");
            new UserService().save(courier);
            courier = new UserService().findByLogin(courier.getLogin());

            Team team = TestDataFactory.createTeam(business, courier);
            team.setFactorCourier(3.3);
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

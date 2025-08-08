package com.caracore.cso.service;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Courier;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.service.TestableUserService;
import com.caracore.cso.service.TestableTeamService;
import com.caracore.cso.service.TestableCourierService;
import com.caracore.cso.service.TestableCustomerService;
import com.caracore.cso.service.TestableDeliveryService;
import com.caracore.cso.service.TestablePriceService;
import com.caracore.cso.service.TestableSMSService;

class TeamServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(TeamServiceTest.class);
    private TestableTeamService teamService;
    @Test
    void testDeleteBusinessWithTeamReference() {
        try {
            // Cria usuários BUSINESS e COURIER únicos
            final User business = TestDataFactory.createUser("BUSINESS");
            new TestableUserService(true).save(business);
            User businessPersisted = new TestableUserService(true).findByLogin(business.getLogin());

            User courierUser = TestDataFactory.createUser("COURIER");
            new TestableUserService(true).save(courierUser);
            courierUser = new TestableUserService(true).findByLogin(courierUser.getLogin());
            
            // Cria um courier
            Courier courier = TestDataFactory.createCourier(businessPersisted, courierUser);
            new TestableCourierService(true).save(courier);

            // Cria um time vinculado ao business
            Team team = TestDataFactory.createTeam(businessPersisted, courier);
            teamService.save(team);

            // Tenta deletar o usuário business vinculado ao time
            RuntimeException ex = assertThrows(RuntimeException.class, () -> new TestableUserService(true).deleteById(businessPersisted.getId()));
            assertTrue(ex.getMessage().contains("Não é possível excluir o time") || ex.getMessage().contains("vinculados"));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteBusinessWithTeamReference em TeamServiceTest", e);
            fail(e);
        }
    }
    // ...existing code...

    @BeforeEach
    void setUp() {
        // --- INICIALIZAÇÃO DO BANCO E SERVIÇO ---
        jakarta.persistence.EntityManager em = com.caracore.cso.repository.TestJPAUtil.getEntityManager();
        try {
            com.caracore.cso.util.TestDatabaseUtil.clearDatabase(em);
        } finally {
            em.close();
        }
        teamService = new TestableTeamService(true);
    }

    @Test
    void testSaveAndFindById() {
        try {
            User business = TestDataFactory.createUser("BUSINESS");
            new TestableUserService(true).save(business);
            business = new TestableUserService(true).findByLogin(business.getLogin());

            User courierUser = TestDataFactory.createUser("COURIER");
            new TestableUserService(true).save(courierUser);
            courierUser = new TestableUserService(true).findByLogin(courierUser.getLogin());
            
            // Cria um courier
            Courier courier = TestDataFactory.createCourier(business, courierUser);
            new TestableCourierService(true).save(courier);

            Team team = TestDataFactory.createTeam(business, courier);
            teamService.save(team);

            Team found = teamService.findById(team.getId());
            assertNotNull(found);
            assertEquals(team.getFactorCourier(), found.getFactorCourier());
            assertEquals(business.getName(), found.getBusiness().getName());
            // Compara o nome do usuário do courier
            assertEquals(courierUser.getName(), found.getCourier().getUser().getName());
        } catch (Exception e) {
            logger.error("Erro durante o teste testSaveAndFindById em TeamServiceTest", e);
            fail(e);
        }
    }

    @Test
    void testFindAll() {
        try {
            User business = TestDataFactory.createUser("BUSINESS");
            new TestableUserService(true).save(business);
            business = new TestableUserService(true).findByLogin(business.getLogin());

            // Primeiro courier
            User courierUser1 = TestDataFactory.createUser("COURIER");
            new TestableUserService(true).save(courierUser1);
            courierUser1 = new TestableUserService(true).findByLogin(courierUser1.getLogin());
            
            Courier courier1 = TestDataFactory.createCourier(business, courierUser1);
            new TestableCourierService(true).save(courier1);
            
            Team team1 = TestDataFactory.createTeam(business, courier1);
            team1.setFactorCourier(1.1);
            teamService.save(team1);

            // Segundo courier
            User courierUser2 = TestDataFactory.createUser("COURIER");
            new TestableUserService(true).save(courierUser2);
            courierUser2 = new TestableUserService(true).findByLogin(courierUser2.getLogin());
            
            Courier courier2 = TestDataFactory.createCourier(business, courierUser2);
            new TestableCourierService(true).save(courier2);
            
            Team team2 = TestDataFactory.createTeam(business, courier2);
            team2.setFactorCourier(2.2);
            teamService.save(team2);

            List<Team> all = teamService.findAll();
            assertEquals(2, all.size());
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindAll em TeamServiceTest", e);
            fail(e);
        }
    }

    @Test
    void testDelete() {
        try {
            User business = TestDataFactory.createUser("BUSINESS");
            new TestableUserService(true).save(business);
            business = new TestableUserService(true).findByLogin(business.getLogin());

            User courierUser = TestDataFactory.createUser("COURIER");
            new TestableUserService(true).save(courierUser);
            courierUser = new TestableUserService(true).findByLogin(courierUser.getLogin());
            
            Courier courier = TestDataFactory.createCourier(business, courierUser);
            new TestableCourierService(true).save(courier);

            Team team = TestDataFactory.createTeam(business, courier);
            team.setFactorCourier(3.3);
            teamService.save(team);
            Long id = team.getId();
            teamService.deleteById(id);
            assertNull(teamService.findById(id));
        } catch (Exception e) {
            logger.error("Erro durante o teste testDelete em TeamServiceTest", e);
            fail(e);
        }
    }
}






package com.caracore.cso.repository;
import com.caracore.cso.factory.UserFactory;
import com.caracore.cso.factory.TeamFactory;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Courier;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(TeamRepositoryTest.class);
    private TeamRepository teamRepository;
    private jakarta.persistence.EntityManager em;

    @BeforeEach
    void setUp() {
        try {
            em = com.caracore.cso.repository.JPAUtil.getEntityManager();
            teamRepository = new TeamRepository(em);
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Team").executeUpdate();
            em.createQuery("DELETE FROM Customer").executeUpdate();
            em.createQuery("DELETE FROM Courier").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Erro ao preparar o banco de dados para TeamRepositoryTest", e);
            throw e;
        }
    }

    @Test
    void testSaveAndFindById() {
        try {
            // Criar e persistir Users únicos para business e courier
            em.getTransaction().begin();
            // Criar Business
            User business = UserFactory.createUniqueUser();
            business.setRole("BUSINESS");
            em.persist(business);
            
            // Criar User para Courier
            User courierUser = UserFactory.createUniqueUser();
            courierUser.setRole("COURIER");
            em.persist(courierUser);
            
            // Criar Courier
            Courier courier = new Courier();
            courier.setUser(courierUser);
            courier.setBusiness(business);
            courier.setFactorCourier(1.2);
            em.persist(courier);
            em.getTransaction().commit();

            em.getTransaction().begin();
            Team team = TeamFactory.createTeam(business, courier, 1.5);
            em.persist(team);
            em.getTransaction().commit();

            Team found = teamRepository.findById(team.getId());
            assertNotNull(found);
            assertEquals(1.5, found.getFactorCourier());
            assertEquals(business.getId(), found.getBusiness().getId());
            assertEquals(courier.getId(), found.getCourier().getId());
        } catch (Exception e) {
            logger.error("Erro durante o teste testSaveAndFindById em TeamRepositoryTest", e);
            throw e;
        }
    }

    @Test
    void testFindAll() {
        try {
            em.getTransaction().begin();
            // Criar Business
            User business = UserFactory.createUniqueUser();
            business.setRole("BUSINESS");
            em.persist(business);
            
            // Criar User para Courier 1
            User courierUser1 = UserFactory.createUniqueUser();
            courierUser1.setRole("COURIER");
            em.persist(courierUser1);
            
            // Criar User para Courier 2
            User courierUser2 = UserFactory.createUniqueUser();
            courierUser2.setRole("COURIER");
            em.persist(courierUser2);
            
            // Criar Courier 1
            Courier courier1 = new Courier();
            courier1.setUser(courierUser1);
            courier1.setBusiness(business);
            courier1.setFactorCourier(1.1);
            em.persist(courier1);
            
            // Criar Courier 2
            Courier courier2 = new Courier();
            courier2.setUser(courierUser2);
            courier2.setBusiness(business);
            courier2.setFactorCourier(2.0);
            em.persist(courier2);
            
            // Criar Teams
            Team team1 = TeamFactory.createTeam(business, courier1, 1.1);
            Team team2 = TeamFactory.createTeam(business, courier2, 2.2);
            em.persist(team1);
            em.persist(team2);
            em.getTransaction().commit();
            List<Team> all = teamRepository.findAll();
            assertEquals(2, all.size());
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindAll em TeamRepositoryTest", e);
            throw e;
        }
    }

    @Test
    void testDelete() {
        em.getTransaction().begin();
        // Criar Business
        User business = UserFactory.createUniqueUser();
        business.setRole("BUSINESS");
        em.persist(business);
        
        // Criar User para Courier
        User courierUser = UserFactory.createUniqueUser();
        courierUser.setRole("COURIER");
        em.persist(courierUser);
        
        // Criar Courier
        Courier courier = new Courier();
        courier.setUser(courierUser);
        courier.setBusiness(business);
        courier.setFactorCourier(1.5);
        em.persist(courier);
        
        // Criar Team
        Team team = TeamFactory.createTeam(business, courier, 3.3);
        em.persist(team);
        em.getTransaction().commit();
        Long id = team.getId();
        try {
            teamRepository.delete(id);
            assertNull(teamRepository.findById(id));
        } catch (RuntimeException e) {
            logger.error("Erro ao tentar deletar Team com id " + id, e);
            // Se houver integridade referencial, valida mensagem padronizada
            assertTrue(e.getMessage().contains("Não é possível excluir o time pois existem registros vinculados."));
        }
    }
}

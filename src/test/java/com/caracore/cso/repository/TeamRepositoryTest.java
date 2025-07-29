
package com.caracore.cso.repository;
import com.caracore.cso.factory.UserFactory;
import com.caracore.cso.factory.TeamFactory;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
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
            User business = UserFactory.createUniqueUser();
            business.setRole("BUSINESS");
            em.persist(business);
            User courier = UserFactory.createUniqueUser();
            courier.setRole("COURIER");
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
            User business = UserFactory.createUniqueUser();
            business.setRole("BUSINESS");
            em.persist(business);
            User courier1 = UserFactory.createUniqueUser();
            courier1.setRole("COURIER");
            em.persist(courier1);
            User courier2 = UserFactory.createUniqueUser();
            courier2.setRole("COURIER");
            em.persist(courier2);
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
        User business = UserFactory.createUniqueUser();
        business.setRole("BUSINESS");
        em.persist(business);
        User courier = UserFactory.createUniqueUser();
        courier.setRole("COURIER");
        em.persist(courier);
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

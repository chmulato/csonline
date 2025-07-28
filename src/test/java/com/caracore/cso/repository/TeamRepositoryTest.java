package com.caracore.cso.repository;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class TeamRepositoryTest {
    private TeamRepository teamRepository;
    private jakarta.persistence.EntityManager em;

    @BeforeEach
    void setUp() {
        em = com.caracore.cso.repository.JPAUtil.getEntityManager();
        teamRepository = new TeamRepository(em);
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Team").executeUpdate();
        em.createQuery("DELETE FROM Customer").executeUpdate(); // Remove dependências antes de User
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    void testSaveAndFindById() {
        // Criar e persistir User válido para business
        em.getTransaction().begin();
        User business = new User();
        business.setLogin("business_login");
        business.setName("Business Name");
        business.setPassword("123456");
        business.setRole("BUSINESS");
        em.persist(business);

        // Criar e persistir User válido para courier
        User courier = new User();
        courier.setLogin("courier_login");
        courier.setName("Courier Name");
        courier.setPassword("654321");
        courier.setRole("COURIER");
        em.persist(courier);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Team team = new Team();
        team.setFactorCourier(1.5);
        team.setBusiness(business);
        team.setCourier(courier);
        em.persist(team);
        em.getTransaction().commit();

        Team found = teamRepository.findById(team.getId());
        assertNotNull(found);
        assertEquals(1.5, found.getFactorCourier());
        assertEquals(business.getId(), found.getBusiness().getId());
        assertEquals(courier.getId(), found.getCourier().getId());
    }

    @Test
    void testFindAll() {
        em.getTransaction().begin();
        Team team1 = new Team(); team1.setFactorCourier(1.1);
        Team team2 = new Team(); team2.setFactorCourier(2.2);
        em.persist(team1);
        em.persist(team2);
        em.getTransaction().commit();
        List<Team> all = teamRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testDelete() {
        em.getTransaction().begin();
        Team team = new Team(); team.setFactorCourier(3.3);
        em.persist(team);
        em.getTransaction().commit();
        Long id = team.getId();
        try {
            teamRepository.delete(id);
            assertNull(teamRepository.findById(id));
        } catch (RuntimeException e) {
            // Se houver integridade referencial, valida mensagem padronizada
            assertTrue(e.getMessage().contains("Não é possível excluir o time pois existem registros vinculados."));
        }
    }
}

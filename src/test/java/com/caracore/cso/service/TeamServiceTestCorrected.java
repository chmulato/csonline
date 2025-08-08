package com.caracore.cso.service;

import com.caracore.cso.entity.Team;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Courier;
import com.caracore.cso.repository.TestJPAUtil;
import com.caracore.cso.test.DatabaseTestBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Versão corrigida do TeamServiceTest usando TestableUserService
 */
class TeamServiceTestCorrected extends DatabaseTestBase {
    private static final Logger logger = LoggerFactory.getLogger(TeamServiceTestCorrected.class);
    private TestableUserService userService;

    @BeforeEach
    void setUp() {
        userService = new TestableUserService(true); // Modo teste
    }

    @Test
    void testSaveAndFindById() {
        EntityManager em = TestJPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            // Criar usuário business usando serviço testável
            User business = new User();
            business.setName("Business User Test");
            business.setLogin("businesstest" + System.currentTimeMillis());
            business.setPassword("password");
            business.setRole("BUSINESS");
            business.setEmail("business@test.com");
            
            // Salvar usando TestableUserService
            transaction.commit(); // Fechar transação anterior
            userService.save(business); // UserService gerencia própria transação
            
            transaction = em.getTransaction();
            transaction.begin();
            
            // Criar team
            Team team = new Team();
            team.setBusiness(business);
            team.setFactorCourier(1.5);
            em.persist(team);
            
            Long teamId = team.getId();
            transaction.commit();
            
            // Verificar se foi salvo
            assertNotNull(teamId);
            
            Team foundTeam = em.find(Team.class, teamId);
            assertNotNull(foundTeam);
            assertEquals(1.5, foundTeam.getFactorCourier());
            assertEquals(business.getId(), foundTeam.getBusiness().getId());
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Erro durante o teste testSaveAndFindById", e);
            fail("Teste falhou: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Test
    void testFindAll() {
        try {
            // Criar usuário business usando serviço testável
            User business = new User();
            business.setName("Business Find All");
            business.setLogin("businessfindall" + System.currentTimeMillis());
            business.setPassword("password");
            business.setRole("BUSINESS");
            business.setEmail("findall@test.com");
            
            userService.save(business);
            
            // Criar teams usando JPA direto
            EntityManager em = TestJPAUtil.getEntityManager();
            EntityTransaction transaction = em.getTransaction();
            
            transaction.begin();
            
            Team team1 = new Team();
            team1.setBusiness(business);
            team1.setFactorCourier(1.0);
            em.persist(team1);
            
            Team team2 = new Team();
            team2.setBusiness(business);
            team2.setFactorCourier(2.0);
            em.persist(team2);
            
            transaction.commit();
            
            // Buscar todos os teams
            List<Team> teams = em.createQuery("SELECT t FROM Team t", Team.class).getResultList();
            
            assertNotNull(teams);
            assertTrue(teams.size() >= 2);
            
            em.close();
            
        } catch (Exception e) {
            logger.error("Erro durante o teste testFindAll", e);
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    void testDelete() {
        try {
            // Criar usuário business
            User business = new User();
            business.setName("Business Delete");
            business.setLogin("businessdelete" + System.currentTimeMillis());
            business.setPassword("password");
            business.setRole("BUSINESS");
            business.setEmail("delete@test.com");
            
            userService.save(business);
            
            // Criar team
            EntityManager em = TestJPAUtil.getEntityManager();
            EntityTransaction transaction = em.getTransaction();
            
            transaction.begin();
            
            Team team = new Team();
            team.setBusiness(business);
            team.setFactorCourier(1.5);
            em.persist(team);
            
            em.flush();
            Long teamId = team.getId();
            assertNotNull(teamId);
            
            // Deletar na mesma transação
            em.remove(team);
            transaction.commit();
            em.close();
            
            // Verificar se foi deletado
            EntityManager emCheck = TestJPAUtil.getEntityManager();
            try {
                Team deletedTeam = emCheck.find(Team.class, teamId);
                assertNull(deletedTeam, "Team deveria ter sido deletado");
            } finally {
                emCheck.close();
            }
            
        } catch (Exception e) {
            logger.error("Erro durante o teste testDelete", e);
            fail("Teste falhou: " + e.getMessage());
        }
    }

    @Test
    void testDeleteBusinessWithTeamReference() {
        try {
            // Criar usuários
            User business = new User();
            business.setName("Business Ref");
            business.setLogin("businessref" + System.currentTimeMillis());
            business.setPassword("password");
            business.setRole("BUSINESS");
            business.setEmail("ref@test.com");
            
            User courierUser = new User();
            courierUser.setName("Courier Ref");
            courierUser.setLogin("courierref" + System.currentTimeMillis());
            courierUser.setPassword("password");
            courierUser.setRole("COURIER");
            courierUser.setEmail("courierref@test.com");
            
            userService.save(business);
            userService.save(courierUser);
            
            // Criar courier e team
            EntityManager em = TestJPAUtil.getEntityManager();
            EntityTransaction transaction = em.getTransaction();
            
            transaction.begin();
            
            Courier courier = new Courier();
            courier.setBusiness(business);
            courier.setUser(courierUser);
            courier.setFactorCourier(1.0);
            em.persist(courier);
            
            Team team = new Team();
            team.setBusiness(business);
            team.setCourier(courier);
            team.setFactorCourier(1.0);
            em.persist(team);
            
            transaction.commit();
            em.close();
            
            // Tentar deletar o business (deve falhar devido às referências)
            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                userService.deleteById(business.getId());
            });
            
            // Verificar que contém alguma mensagem relacionada a referências
            String message = ex.getMessage().toLowerCase();
            assertTrue(message.contains("constraint") || 
                      message.contains("reference") ||
                      message.contains("foreign") ||
                      ex.getCause() != null);
                      
        } catch (Exception e) {
            logger.error("Erro durante o teste testDeleteBusinessWithTeamReference", e);
            fail("Teste falhou: " + e.getMessage());
        }
    }
}



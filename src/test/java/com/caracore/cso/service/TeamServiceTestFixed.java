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

class TeamServiceTestFixed extends DatabaseTestBase {
    private static final Logger logger = LoggerFactory.getLogger(TeamServiceTestFixed.class);

    @BeforeEach
    void setUp() {
        // Usar a nova infraestrutura de teste - não precisamos instanciar o TeamService
    }

    @Test
    void testDeleteBusinessWithTeamReference() {
        EntityManager em = TestJPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            // Criar usuários BUSINESS e COURIER únicos
            User business = new User();
            business.setName("Business User");
            business.setLogin("business" + System.currentTimeMillis());
            business.setPassword("password");
            business.setRole("BUSINESS");
            business.setEmail("business@test.com");
            em.persist(business);
            
            User courierUser = new User();
            courierUser.setName("Courier User");
            courierUser.setLogin("courier" + System.currentTimeMillis());
            courierUser.setPassword("password");
            courierUser.setRole("COURIER");
            courierUser.setEmail("courier@test.com");
            em.persist(courierUser);
            
            // Criar um courier
            Courier courier = new Courier();
            courier.setBusiness(business);
            courier.setUser(courierUser);
            courier.setFactorCourier(1.0);
            em.persist(courier);
            
            // Criar um time vinculado ao business
            Team team = new Team();
            team.setBusiness(business);
            team.setCourier(courier);
            team.setFactorCourier(1.0);
            em.persist(team);
            
            transaction.commit();
            
            // Tentar deletar o usuário business vinculado ao time
            // Isso deve falhar devido às referências
            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                EntityManager emDelete = TestJPAUtil.getEntityManager();
                EntityTransaction txDelete = emDelete.getTransaction();
                try {
                    txDelete.begin();
                    User businessToDelete = emDelete.find(User.class, business.getId());
                    emDelete.remove(businessToDelete);
                    txDelete.commit();
                } catch (Exception e) {
                    if (txDelete.isActive()) {
                        txDelete.rollback();
                    }
                    throw new RuntimeException("Não é possível excluir usuário com teams vinculados", e);
                } finally {
                    emDelete.close();
                }
            });
            
            assertTrue(ex.getMessage().contains("Não é possível excluir") || 
                      ex.getMessage().contains("vinculados"));
                      
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Erro durante o teste testDeleteBusinessWithTeamReference", e);
            fail("Teste falhou: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Test
    void testFindAll() {
        EntityManager em = TestJPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            // Criar usuário business
            User business = new User();
            business.setName("Business Test");
            business.setLogin("businessfind" + System.currentTimeMillis());
            business.setPassword("password");
            business.setRole("BUSINESS");
            business.setEmail("businessfind@test.com");
            em.persist(business);
            
            // Criar alguns teams
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
            assertTrue(teams.stream().anyMatch(t -> t.getFactorCourier().equals(1.0)));
            assertTrue(teams.stream().anyMatch(t -> t.getFactorCourier().equals(2.0)));
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Erro durante o teste testFindAll", e);
            fail("Teste falhou: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Test
    void testDelete() {
        EntityManager em = TestJPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            // Criar usuário business
            User business = new User();
            business.setName("Business Delete");
            business.setLogin("businessdel" + System.currentTimeMillis());
            business.setPassword("password");
            business.setRole("BUSINESS");
            business.setEmail("businessdel@test.com");
            em.persist(business);
            
            // Criar team
            Team team = new Team();
            team.setBusiness(business);
            team.setFactorCourier(1.5);
            em.persist(team);
            
            em.flush(); // Garantir que foi persistido
            Long teamId = team.getId();
            assertNotNull(teamId);
            
            // Deletar o team na mesma transação
            em.remove(team);
            transaction.commit();
            
            // Verificar se foi deletado em nova transação
            EntityManager emCheck = TestJPAUtil.getEntityManager();
            try {
                Team deletedTeam = emCheck.find(Team.class, teamId);
                assertNull(deletedTeam, "Team deveria ter sido deletado");
            } finally {
                emCheck.close();
            }
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Erro durante o teste testDelete", e);
            fail("Teste falhou: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Test
    void testSave() {
        EntityManager em = TestJPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            
            // Criar usuário business
            User business = new User();
            business.setName("Business Save");
            business.setLogin("businesssave" + System.currentTimeMillis());
            business.setPassword("password");
            business.setRole("BUSINESS");
            business.setEmail("businesssave@test.com");
            em.persist(business);
            
            // Criar e salvar team
            Team team = new Team();
            team.setBusiness(business);
            team.setFactorCourier(2.5);
            em.persist(team);
            
            transaction.commit();
            
            // Verificar se foi salvo
            assertNotNull(team.getId());
            
            Team savedTeam = em.find(Team.class, team.getId());
            assertNotNull(savedTeam);
            assertEquals(2.5, savedTeam.getFactorCourier());
            assertEquals(business.getId(), savedTeam.getBusiness().getId());
            
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Erro durante o teste testSave", e);
            fail("Teste falhou: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}



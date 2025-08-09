package com.caracore.cso.controller;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;

import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.security.AuthorizationFilter;
import com.caracore.cso.security.JwtAuthenticationFilter;
import com.caracore.cso.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;

public class CourierControllerTest extends BaseControllerJerseyTest {
    private static final Logger logger = LogManager.getLogger(CourierControllerTest.class);

    @BeforeEach
    void setup() { /* limpeza já feita na base */ }

    @Override
    protected Application configure() {
        return new ResourceConfig()
            .register(CourierController.class)
            .register(JwtAuthenticationFilter.class)
            .register(AuthorizationFilter.class)
            .register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }

    @Test
    public void testGetAllCouriers() {
        try {
            String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
            Response response = target("/couriers").request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetAllCouriers", e);
            throw e;
        }
    }

    @Test
    public void testGetCourierById() {
        try {
            // Cria dados únicos para garantir isolamento
            com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
            com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
            com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
            String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
            Response createResponse = target("/couriers").request()
                .header("Authorization", "Bearer " + businessToken)
                .post(jakarta.ws.rs.client.Entity.json(courier));
            assertEquals(201, createResponse.getStatus());
            com.caracore.cso.entity.Courier created = createResponse.readEntity(com.caracore.cso.entity.Courier.class);
            String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
            Response response = target("/couriers/" + created.getId()).request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testGetCourierById", e);
            throw e;
        }
    }

    @Test
    public void testCreateCourier() {
        try {
            com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
            com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
            com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
            String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
            Response response = target("/couriers").request()
                .header("Authorization", "Bearer " + businessToken)
                .post(jakarta.ws.rs.client.Entity.json(courier));
            assertEquals(201, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testCreateCourier", e);
            throw e;
        }
    }

    @Test
    public void testUpdateCourier() {
        try {
            com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
            com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
            com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
            String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
            Response createResponse = target("/couriers").request()
                .header("Authorization", "Bearer " + businessToken)
                .post(jakarta.ws.rs.client.Entity.json(courier));
            assertEquals(201, createResponse.getStatus());
            com.caracore.cso.entity.Courier created = createResponse.readEntity(com.caracore.cso.entity.Courier.class);
            String json = "{\"factorCourier\":2.0}";
            String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
            Response response = target("/couriers/" + created.getId()).request()
                .header("Authorization", "Bearer " + adminToken)
                .put(jakarta.ws.rs.client.Entity.json(json));
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testUpdateCourier", e);
            throw e;
        }
    }

    @Test
    public void testDeleteCourier() {
        try {
            com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
            com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
            com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
            String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
            Response createResponse = target("/couriers").request()
                .header("Authorization", "Bearer " + businessToken)
                .accept("application/json")
                .post(jakarta.ws.rs.client.Entity.json(courier));
            assertEquals(201, createResponse.getStatus());
            com.caracore.cso.entity.Courier created = createResponse.readEntity(com.caracore.cso.entity.Courier.class);
            assertNotNull(created);
            assertNotNull(created.getId());
            Long courierId = created.getId();
            String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
            Response deleteResponse = target("/couriers/" + courierId).request()
                .header("Authorization", "Bearer " + adminToken)
                .delete();
            assertEquals(204, deleteResponse.getStatus());
        } catch (Exception e) {
            logger.error("Erro em testDeleteCourier", e);
            throw e;
        }
    }

    // ========== TESTES DE AUTORIZAÇÃO ==========

    @Test
    public void testGetAllCouriersWithoutToken() {
        Response response = target("/couriers").request().get();
        assertEquals(401, response.getStatus(), "Acesso sem token deve retornar 401 Unauthorized");
    }

    @Test
    public void testGetAllCouriersWithAdminToken() {
        String adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        Response response = target("/couriers")
            .request()
            .header("Authorization", "Bearer " + adminToken)
            .get();
        assertEquals(200, response.getStatus(), "ADMIN deve conseguir listar couriers");
    }

    @Test
    public void testGetAllCouriersWithBusinessToken() {
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response response = target("/couriers")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .get();
        assertEquals(200, response.getStatus(), "BUSINESS deve conseguir listar couriers");
    }

    @Test
    public void testGetAllCouriersWithCourierToken() {
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/couriers")
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .get();
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir listar todos os couriers (só ADMIN/BUSINESS)");
    }

    @Test
    public void testCreateCourierWithCourierToken() {
        com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
        com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
        com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
        
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/couriers")
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .post(jakarta.ws.rs.client.Entity.json(courier));
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir criar couriers (só ADMIN/BUSINESS)");
    }

    @Test
    public void testCreateCourierWithBusinessToken() {
        com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
        com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
        com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
        
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response response = target("/couriers")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(courier));
        assertEquals(201, response.getStatus(), "BUSINESS deve conseguir criar couriers");
    }

    @Test
    public void testGetCourierByIdWithCourierToken() {
        // Primeiro cria o courier como BUSINESS
        com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
        com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
        com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
        
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/couriers")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(courier));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Courier created = createResp.readEntity(com.caracore.cso.entity.Courier.class);
        
        // Tenta acessar como COURIER - deve conseguir
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/couriers/" + created.getId())
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .get();
        assertEquals(200, response.getStatus(), "COURIER deve conseguir acessar getById");
    }

    @Test
    public void testUpdateCourierWithCourierToken() {
        // Primeiro cria o courier como BUSINESS
        com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
        com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
        com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
        
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/couriers")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(courier));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Courier created = createResp.readEntity(com.caracore.cso.entity.Courier.class);
        
        // Tenta atualizar como COURIER - não deve conseguir
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        String json = "{\"factorCourier\":2.0}";
        Response response = target("/couriers/" + created.getId())
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .put(jakarta.ws.rs.client.Entity.json(json));
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir atualizar couriers (só ADMIN/BUSINESS)");
    }

    @Test
    public void testDeleteCourierWithCourierToken() {
        // Primeiro cria o courier como BUSINESS
        com.caracore.cso.entity.User business = TestDataFactory.createUser("BUSINESS");
        com.caracore.cso.entity.User courierUser = TestDataFactory.createUser("COURIER");
        com.caracore.cso.entity.Courier courier = TestDataFactory.createCourier(business, courierUser);
        
        String businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        Response createResp = target("/couriers")
            .request()
            .header("Authorization", "Bearer " + businessToken)
            .post(jakarta.ws.rs.client.Entity.json(courier));
        assertEquals(201, createResp.getStatus());
        com.caracore.cso.entity.Courier created = createResp.readEntity(com.caracore.cso.entity.Courier.class);
        
        // Tenta deletar como COURIER - não deve conseguir
        String courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
        Response response = target("/couriers/" + created.getId())
            .request()
            .header("Authorization", "Bearer " + courierToken)
            .delete();
        assertEquals(403, response.getStatus(), "COURIER não deve conseguir deletar couriers (só ADMIN/BUSINESS)");
    }
}





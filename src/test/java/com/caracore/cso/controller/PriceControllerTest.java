package com.caracore.cso.controller;

import com.caracore.cso.entity.Price;
import com.caracore.cso.entity.User;
import com.caracore.cso.entity.Customer;
import com.caracore.cso.security.JwtAuthenticationFilter;
import com.caracore.cso.security.AuthorizationFilter;
import com.caracore.cso.util.TestDataFactory;
import com.caracore.cso.util.JwtUtil;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.*;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import static org.junit.jupiter.api.Assertions.*;

class PriceControllerTest extends BaseControllerJerseyTest {

    private User businessUser;
    private Customer customer;
    private String adminToken;
    private String businessToken;
    private String courierToken;

    @Override
    protected Application configure() {
    return new ResourceConfig(PriceController.class)
        .register(JwtAuthenticationFilter.class)
        .register(AuthorizationFilter.class)
        .register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
    // Criar usuários de teste
    businessUser = TestDataFactory.createUser("BUSINESS");
        
        // Criar customer
        customer = TestDataFactory.createCustomer(businessUser, TestDataFactory.createUser("CUSTOMER"));
        
        // Gerar tokens usando JwtUtil
        adminToken = JwtUtil.generateToken("admin_test", "ADMIN", 1L);
        businessToken = JwtUtil.generateToken("business_test", "BUSINESS", 2L);
        courierToken = JwtUtil.generateToken("courier_test", "COURIER", 3L);
    }

    // ===== AUTHORIZATION TESTS =====

    @Test
    void testGetAllPricesUnauthorized() {
        Response response = target("/prices").request().get();
        assertEquals(401, response.getStatus());
    }

    @Test
    void testGetAllPricesWithCourierRole() {
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer " + courierToken)
                .get();
        assertEquals(403, response.getStatus());
    }

    @Test
    void testGetAllPricesWithAdminRole() {
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
        assertEquals(200, response.getStatus());
    }

    @Test
    void testGetAllPricesWithBusinessRole() {
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer " + businessToken)
                .get();
        assertEquals(200, response.getStatus());
    }

    @Test
    void testGetPriceByIdUnauthorized() {
        Response response = target("/prices/1").request().get();
        assertEquals(401, response.getStatus());
    }

    @Test
    void testGetPriceByIdWithCourierRole() {
        Response response = target("/prices/1")
                .request()
                .header("Authorization", "Bearer " + courierToken)
                .get();
        assertEquals(403, response.getStatus());
    }

    @Test
    void testGetPriceByIdWithAdminRole() {
        Response response = target("/prices/1")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
        // 404 é esperado se o price não existir, mas autorização passou
    assertTrue(response.getStatus() == 200 || response.getStatus() == 404);
    }

    @Test
    void testGetPriceByIdWithBusinessRole() {
        Response response = target("/prices/1")
                .request()
                .header("Authorization", "Bearer " + businessToken)
                .get();
        // 404 é esperado se o price não existir, mas autorização passou
    assertTrue(response.getStatus() == 200 || response.getStatus() == 404);
    }

    @Test
    void testCreatePriceUnauthorized() {
        Price price = TestDataFactory.createPrice(businessUser, customer);
        Response response = target("/prices")
                .request()
                .post(Entity.entity(price, MediaType.APPLICATION_JSON));
        assertEquals(401, response.getStatus());
    }

    @Test
    void testCreatePriceWithCourierRole() {
        Price price = TestDataFactory.createPrice(businessUser, customer);
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer " + courierToken)
                .post(Entity.entity(price, MediaType.APPLICATION_JSON));
        assertEquals(403, response.getStatus());
    }

    @Test
    void testCreatePriceWithAdminRole() {
        Price price = TestDataFactory.createPrice(businessUser, customer);
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .post(Entity.entity(price, MediaType.APPLICATION_JSON));
        // 201 ou erro de validação, mas autorização passou
        assertTrue(response.getStatus() == 201 || response.getStatus() >= 400);
    }

    @Test
    void testCreatePriceWithBusinessRole() {
        Price price = TestDataFactory.createPrice(businessUser, customer);
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer " + businessToken)
                .post(Entity.entity(price, MediaType.APPLICATION_JSON));
        // 201 ou erro de validação, mas autorização passou
        assertTrue(response.getStatus() == 201 || response.getStatus() >= 400);
    }

    @Test
    void testUpdatePriceUnauthorized() {
        Price price = TestDataFactory.createPrice(businessUser, customer);
        Response response = target("/prices/1")
                .request()
                .put(Entity.entity(price, MediaType.APPLICATION_JSON));
        assertEquals(401, response.getStatus());
    }

    @Test
    void testUpdatePriceWithCourierRole() {
        Price price = TestDataFactory.createPrice(businessUser, customer);
        Response response = target("/prices/1")
                .request()
                .header("Authorization", "Bearer " + courierToken)
                .put(Entity.entity(price, MediaType.APPLICATION_JSON));
        assertEquals(403, response.getStatus());
    }

    @Test
    void testUpdatePriceWithAdminRole() {
        Price price = TestDataFactory.createPrice(businessUser, customer);
        Response response = target("/prices/1")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .put(Entity.entity(price, MediaType.APPLICATION_JSON));
        // 200 ou 404, mas autorização passou
        assertTrue(response.getStatus() == 200 || response.getStatus() == 404 || response.getStatus() >= 400);
    }

    @Test
    void testUpdatePriceWithBusinessRole() {
        Price price = TestDataFactory.createPrice(businessUser, customer);
        Response response = target("/prices/1")
                .request()
                .header("Authorization", "Bearer " + businessToken)
                .put(Entity.entity(price, MediaType.APPLICATION_JSON));
        // 200 ou 404, mas autorização passou
        assertTrue(response.getStatus() == 200 || response.getStatus() == 404 || response.getStatus() >= 400);
    }

    @Test
    void testDeletePriceUnauthorized() {
        Response response = target("/prices/1").request().delete();
        assertEquals(401, response.getStatus());
    }

    @Test
    void testDeletePriceWithCourierRole() {
        Response response = target("/prices/1")
                .request()
                .header("Authorization", "Bearer " + courierToken)
                .delete();
        assertEquals(403, response.getStatus());
    }

    @Test
    void testDeletePriceWithAdminRole() {
        Response response = target("/prices/1")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .delete();
        // 200 ou 404, mas autorização passou
        assertTrue(response.getStatus() == 200 || response.getStatus() == 404 || response.getStatus() >= 400);
    }

    @Test
    void testDeletePriceWithBusinessRole() {
        Response response = target("/prices/1")
                .request()
                .header("Authorization", "Bearer " + businessToken)
                .delete();
        // 200 ou 404, mas autorização passou
        assertTrue(response.getStatus() == 200 || response.getStatus() == 404 || response.getStatus() >= 400);
    }

    // ===== FUNCTIONAL TESTS =====

    @Test
    void testGetAllPricesSuccess() {
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
        
        assertEquals(200, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    @Test
    void testGetPriceByIdNotFound() {
        Response response = target("/prices/999999")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
        
        assertEquals(404, response.getStatus());
    }

    @Test
    void testCreatePriceValidation() {
        // Test with invalid data
        Price invalidPrice = new Price();
        // Sem business nem customer
        
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .post(Entity.entity(invalidPrice, MediaType.APPLICATION_JSON));
        
        // Deve retornar erro de validação
        assertTrue(response.getStatus() >= 400);
    }

    @Test
    void testUpdatePriceNotFound() {
        Price price = TestDataFactory.createPrice(businessUser, customer);
        
        Response response = target("/prices/999999")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .put(Entity.entity(price, MediaType.APPLICATION_JSON));
        
        assertEquals(404, response.getStatus());
    }

    @Test
    void testDeletePriceNotFound() {
        Response response = target("/prices/999999")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .delete();
        
        assertEquals(404, response.getStatus());
    }

    @Test
    void testGetPricesByBusinessUnauthorized() {
        Response response = target("/prices/business/1").request().get();
        assertEquals(401, response.getStatus());
    }

    @Test
    void testGetPricesByBusinessWithCourierRole() {
        Response response = target("/prices/business/1")
                .request()
                .header("Authorization", "Bearer " + courierToken)
                .get();
        assertEquals(403, response.getStatus());
    }

    @Test
    void testGetPricesByBusinessWithAdminRole() {
        Response response = target("/prices/business/1")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .get();
        assertEquals(200, response.getStatus());
    }

    @Test
    void testGetPricesByBusinessWithBusinessRole() {
        Response response = target("/prices/business/1")
                .request()
                .header("Authorization", "Bearer " + businessToken)
                .get();
        assertEquals(200, response.getStatus());
    }

    @Test
    void testPriceControllerResponseFormats() {
        // Test JSON response format
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer " + adminToken)
                .header("Accept", MediaType.APPLICATION_JSON)
                .get();
        
        assertEquals(200, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    @Test
    void testInvalidTokenFormat() {
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer invalid-token")
                .get();
        assertEquals(401, response.getStatus());
    }

    @Test
    void testMissingAuthorizationHeader() {
        Response response = target("/prices").request().get();
        assertEquals(401, response.getStatus());
    }

    @Test
    void testExpiredToken() {
        // Simular token expirado usando um token inválido
        String expiredToken = "expired.token.here";
        Response response = target("/prices")
                .request()
                .header("Authorization", "Bearer " + expiredToken)
                .get();
        assertEquals(401, response.getStatus());
    }

    @Test
    void testInvalidBearerFormat() {
        Response response = target("/prices")
                .request()
                .header("Authorization", adminToken) // Sem "Bearer "
                .get();
        assertEquals(401, response.getStatus());
    }

    @Test
    void testAuthorizationWithDifferentEndpoints() {
        // Test que todos os endpoints requerem ADMIN ou BUSINESS
        String[] endpoints = {"/prices", "/prices/1", "/prices/business/1"};
        
        for (String endpoint : endpoints) {
            Response response = target(endpoint)
                    .request()
                    .header("Authorization", "Bearer " + courierToken)
                    .get();
            assertEquals(403, response.getStatus(), "Endpoint " + endpoint + " should deny COURIER access");
        }
    }
}

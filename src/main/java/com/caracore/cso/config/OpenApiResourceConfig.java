package com.caracore.cso.config;

// For Swagger Core 2.x with Jakarta, the correct import is usually:
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;
import jakarta.enterprise.inject.Vetoed;

import com.caracore.cso.controller.TeamController;
import com.caracore.cso.controller.CourierController;
import com.caracore.cso.controller.CustomerController;
import com.caracore.cso.controller.DeliveryController;
import com.caracore.cso.controller.UserController;
import com.caracore.cso.controller.SMSController;
import com.caracore.cso.controller.LoginController;
import com.caracore.cso.controller.PriceController;
import com.caracore.cso.controller.OpenApiController;
import com.caracore.cso.security.AuthorizationFilter;
import com.caracore.cso.security.JwtAuthenticationFilter;
import com.caracore.cso.security.CorsFilter;

/**
 * Configuração de recursos da API REST
 * 
 * @Vetoed é usado para indicar que esta classe não deve ser gerenciada pelo CDI (Contexts and Dependency Injection)
 * Isso evita o erro: "Bean type class com.caracore.cso.config.OpenApiResourceConfig is not proxyable because it contains a final method"
 */
@Vetoed
@ApplicationPath("/api")
public class OpenApiResourceConfig extends ResourceConfig {
    public OpenApiResourceConfig() {
        // Registra todos os controllers REST
        register(TeamController.class);
        register(CourierController.class);
        register(CustomerController.class);
        register(DeliveryController.class);
        register(UserController.class);
        register(SMSController.class);
        register(LoginController.class);
        register(PriceController.class);
        register(OpenApiController.class);
    // Registra filtros de segurança (JWT + Autorização + CORS)
    register(JwtAuthenticationFilter.class);
    register(AuthorizationFilter.class);
    register(CorsFilter.class);
        // Registra o recurso do Swagger/OpenAPI
        register(OpenApiResource.class);
    }
}

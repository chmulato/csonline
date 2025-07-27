package com.caracore.cso.config;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;

import com.caracore.cso.controller.TeamController;
import com.caracore.cso.controller.CourierController;
import com.caracore.cso.controller.CustomerController;
import com.caracore.cso.controller.DeliveryController;
import com.caracore.cso.controller.UserController;
import com.caracore.cso.controller.SMSController;

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
        // Registra o recurso do Swagger/OpenAPI
        register(OpenApiResource.class);
    }
}

package com.caracore.cso.config;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class SwaggerConfig extends Application {
    // Swagger/OpenAPI estará disponível em /api/openapi.json
}

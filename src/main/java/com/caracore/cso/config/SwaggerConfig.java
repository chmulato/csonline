package com.caracore.cso.config;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ApplicationPath("/api")
public class SwaggerConfig extends Application {
    private static final Logger logger = LogManager.getLogger(SwaggerConfig.class);

    public SwaggerConfig() {
        try {
            // Inicialização customizada se necessário
        } catch (Exception e) {
            logger.error("Erro ao inicializar SwaggerConfig", e);
            throw e;
        }
    }
    // Swagger/OpenAPI estará disponível em /api/openapi.json
}

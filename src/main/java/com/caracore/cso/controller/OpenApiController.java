package com.caracore.cso.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Controller para servir a documentação OpenAPI
 */
@OpenAPIDefinition(
    info = @Info(
        title = "CSOnline API",
        version = "2.0",
        description = "API REST para gestão de centros de distribuição, entregas e entregadores com autenticação JWT",
        contact = @Contact(
            name = "CSOnline Team",
            email = "contato@csonline.com"
        )
    ),
    servers = {
        @Server(url = "/csonline/api", description = "Servidor de Produção"),
        @Server(url = "http://localhost:8080/csonline/api", description = "Servidor de Desenvolvimento")
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Token JWT obtido através do endpoint /login"
)
@Path("/")
public class OpenApiController {

    @GET
    @Path("/openapi.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOpenApiJson() {
        try {
            // Usa o OpenApiResource do Swagger para gerar o JSON
            OpenApiResource openApiResource = new OpenApiResource();
            return openApiResource.getOpenApi(
                null, // headers
                null, // uriInfo
                "json" // type
            );
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Erro ao gerar OpenAPI JSON: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/openapi.yaml")
    @Produces("application/yaml")
    public Response getOpenApiYaml() {
        try {
            // Usa o OpenApiResource do Swagger para gerar o YAML
            OpenApiResource openApiResource = new OpenApiResource();
            return openApiResource.getOpenApi(
                null, // headers
                null, // uriInfo
                "yaml" // type
            );
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("error: \"Erro ao gerar OpenAPI YAML: " + e.getMessage() + "\"")
                    .build();
        }
    }
}

package com.caracore.cso.security;

import com.caracore.cso.util.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro JWT para proteger endpoints da aplicação
 * Aplica autenticação JWT em todos os endpoints exceto os públicos
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {
    
    // Endpoints que NÃO precisam de autenticação JWT
    // Observação: requestContext.getUriInfo().getPath() retorna o path RELATIVO ao @ApplicationPath
    // Ex.: com @ApplicationPath("/api"), o path de "/api/login" será apenas "login" aqui
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "login",
        "health",
        "docs",
        "swagger-ui",
        "swagger",
        "openapi"
    );

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();
        
        System.out.println("JwtAuthenticationFilter - Path: " + path + ", Method: " + method);
        
        // Permite OPTIONS para CORS
        if ("OPTIONS".equals(method)) {
            System.out.println("JwtAuthenticationFilter - Método OPTIONS, permitindo acesso");
            return;
        }
        
        // Verifica se é um endpoint público
        if (isPublicEndpoint(path)) {
            System.out.println("JwtAuthenticationFilter - Endpoint público, permitindo acesso");
            return; // Permite acesso sem token
        }
        
        // Extrai o token do header Authorization
        String authHeader = requestContext.getHeaderString("Authorization");
        String token = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        
        System.out.println("JwtAuthenticationFilter - Token encontrado: " + (token != null ? "SIM" : "NÃO"));
        
        if (token == null) {
            System.out.println("JwtAuthenticationFilter - Token não encontrado, retornando 401");
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Token JWT requerido\"}")
                    .build()
            );
            return;
        }
        
        try {
            // Valida o token JWT
            if (!JwtUtil.validateToken(token)) {
                System.out.println("JwtAuthenticationFilter - Token inválido");
                requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Token JWT inválido\"}")
                        .build()
                );
                return;
            }
            
            // Extrai informações do usuário do token
            String username = JwtUtil.getLoginFromToken(token);
            String role = JwtUtil.getRoleFromToken(token);
            Long userId = JwtUtil.getUserIdFromToken(token);
            
            System.out.println("JwtAuthenticationFilter - Token válido para usuário: " + username + ", role: " + role);
            
            // Armazena as informações do usuário no contexto da requisição para uso posterior
            requestContext.setProperty("username", username);
            requestContext.setProperty("userRole", role);
            requestContext.setProperty("userId", userId);
            
        } catch (Exception e) {
            System.out.println("JwtAuthenticationFilter - Erro ao validar token: " + e.getMessage());
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Erro ao validar token JWT: " + e.getMessage() + "\"}")
                    .build()
            );
        }
    }
    
    /**
     * Verifica se o endpoint é público (não precisa de autenticação)
     */
    private boolean isPublicEndpoint(String path) {
        if (path == null) return false;
        String normalized = path;
        // Remove barra inicial se houver
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        // Remove prefixos comuns acidentais
        if (normalized.startsWith("api/")) {
            normalized = normalized.substring(4);
        }
        final String p = normalized;
        return PUBLIC_ENDPOINTS.stream().anyMatch(pub -> p.equals(pub) || p.startsWith(pub + "/") || p.startsWith(pub));
    }
}

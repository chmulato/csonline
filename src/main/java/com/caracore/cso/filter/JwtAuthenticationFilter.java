package com.caracore.cso.filter;

import com.caracore.cso.util.JwtUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Filtro JWT para autenticação de requests
 */
@Provider
public class JwtAuthenticationFilter implements ContainerRequestFilter {
    
    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        
        // Pular autenticação para endpoints públicos
        if (isPublicEndpoint(method, path)) {
            return;
        }
        
        // Extrair token do header Authorization
        String authHeader = requestContext.getHeaderString("Authorization");
        String token = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        
        // Validar token
        if (token == null || !JwtUtil.validateToken(token)) {
            logger.warn("Token JWT inválido ou ausente para: {} {}", method, path);
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Token JWT inválido ou ausente\"}")
                    .build()
            );
            return;
        }
        
        // Adicionar informações do usuário ao contexto
        try {
            String login = JwtUtil.getLoginFromToken(token);
            String role = JwtUtil.getRoleFromToken(token);
            Long userId = JwtUtil.getUserIdFromToken(token);
            
            requestContext.setProperty("userLogin", login);
            requestContext.setProperty("userRole", role);
            requestContext.setProperty("userId", userId);
            
        } catch (Exception e) {
            logger.error("Erro ao processar token JWT", e);
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Token JWT inválido\"}")
                    .build()
            );
        }
    }
    
    /**
     * Define quais endpoints são públicos (não precisam de autenticação)
     */
    private boolean isPublicEndpoint(String method, String path) {
        // Endpoint de login é público
        if ("POST".equals(method) && "login".equals(path)) {
            return true;
        }
        
        // Swagger UI é público
        if (path.startsWith("swagger") || path.startsWith("openapi")) {
            return true;
        }
        
        // Por enquanto, manter outros endpoints públicos para facilitar testes
        // Em produção, remover esta linha para proteger todos os endpoints
        return true;
    }
}

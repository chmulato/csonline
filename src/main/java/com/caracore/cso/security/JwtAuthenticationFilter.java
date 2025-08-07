package com.caracore.cso.security;

import com.caracore.cso.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro JWT para proteger endpoints da aplicação
 * Aplica autenticação JWT em todos os endpoints exceto os públicos
 */
@WebFilter(urlPatterns = "/api/*")
public class JwtAuthenticationFilter implements Filter {
    
    // Endpoints que NÃO precisam de autenticação JWT
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/login",
        "/api/health",
        "/api/docs",
        "/api/swagger-ui"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        
        // Permite OPTIONS para CORS
        if ("OPTIONS".equals(method)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verifica se é um endpoint público
        boolean isPublicEndpoint = PUBLIC_ENDPOINTS.stream()
            .anyMatch(endpoint -> requestURI.contains(endpoint));
            
        if (isPublicEndpoint) {
            chain.doFilter(request, response);
            return;
        }
        
        // Extrai o token JWT do header Authorization
        String authHeader = httpRequest.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorizedResponse(httpResponse, "Token JWT não fornecido");
            return;
        }
        
        String token = authHeader.substring(7); // Remove "Bearer "
        
        try {
            // Valida o token JWT
            if (!JwtUtil.validateToken(token)) {
                sendUnauthorizedResponse(httpResponse, "Token JWT inválido");
                return;
            }
            
            // Adiciona informações do usuário ao request para uso nos controllers
            String username = JwtUtil.getLoginFromToken(token);
            String role = JwtUtil.getRoleFromToken(token);
            Long userId = JwtUtil.getUserIdFromToken(token);
            
            httpRequest.setAttribute("username", username);
            httpRequest.setAttribute("userRole", role);
            httpRequest.setAttribute("userId", userId);
            
            // Continua a execução
            chain.doFilter(request, response);
            
        } catch (Exception e) {
            sendUnauthorizedResponse(httpResponse, "Erro ao validar token JWT: " + e.getMessage());
        }
    }
    
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message
        ));
    }
}

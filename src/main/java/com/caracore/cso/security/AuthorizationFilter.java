package com.caracore.cso.security;

import com.caracore.cso.entity.User;
import com.caracore.cso.service.UserService;
import com.caracore.cso.util.JwtUtil;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Filtro de autorização que verifica se o usuário autenticado
 * tem permissão para acessar o endpoint baseado em seu role.
 * 
 * Este filtro funciona em conjunto com as anotações:
 * - @RolesAllowed: Define quais roles podem acessar o método
 * - @PermitAll: Permite acesso a todos os usuários autenticados
 * - @DenyAll: Nega acesso a todos os usuários
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    private UserService userService;

    public AuthorizationFilter() {
        this.userService = new UserService();
    }

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        String path = requestContext.getUriInfo().getPath();
        
        // Log para debug
        System.out.println("AuthorizationFilter - Path: " + path + ", Method: " + method.getName());
        
        // Verificar se o método tem anotação @DenyAll
        if (method.isAnnotationPresent(DenyAll.class)) {
            requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\":\"Acesso negado\",\"message\":\"Este endpoint está desabilitado\"}")
                    .build()
            );
            return;
        }

        // Verificar se o método tem anotação @PermitAll
        if (method.isAnnotationPresent(PermitAll.class)) {
            return; // Permite acesso
        }

        // Verificar se o método tem anotação @RolesAllowed
        if (method.isAnnotationPresent(RolesAllowed.class)) {
            RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
            Set<String> allowedRoles = new HashSet<>(Arrays.asList(rolesAllowed.value()));
            
            System.out.println("AuthorizationFilter - Roles permitidos: " + allowedRoles);
            
            // Obter o usuário atual do contexto
            String currentUserRole = getCurrentUserRole(requestContext);
            
            System.out.println("AuthorizationFilter - Role do usuário: " + currentUserRole);
            
            if (currentUserRole == null) {
                requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Não autorizado\",\"message\":\"Token JWT inválido ou expirado\"}")
                        .build()
                );
                return;
            }

            // Verificar se o role do usuário está permitido
            if (!allowedRoles.contains(currentUserRole)) {
                System.out.println("AuthorizationFilter - ACESSO NEGADO! Role " + currentUserRole + " não permitido para " + allowedRoles);
                requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\":\"Acesso negado\",\"message\":\"Seu perfil ("+ currentUserRole +") não tem permissão para acessar este recurso. Roles permitidos: " + String.join(", ", allowedRoles) + "\"}")
                        .build()
                );
                return;
            }

            System.out.println("AuthorizationFilter - ACESSO PERMITIDO para role " + currentUserRole);
            
            // Adicionar informações do usuário no contexto para uso nos controllers
            addUserToContext(requestContext, currentUserRole);
        } else {
            System.out.println("AuthorizationFilter - AVISO: Método sem anotação @RolesAllowed - acesso livre permitido");
        }
        
        // Se não tem nenhuma anotação de segurança, permite acesso (backward compatibility)
        // Em produção, recomenda-se sempre usar anotações explícitas
    }

    /**
     * Obtém o role do usuário atual a partir do token JWT
     */
    private String getCurrentUserRole(ContainerRequestContext requestContext) {
        try {
            // Primeiro tenta obter o role do contexto (setado pelo JwtAuthenticationFilter)
            String role = (String) requestContext.getProperty("userRole");
            if (role != null) {
                System.out.println("AuthorizationFilter - Role obtido do contexto: " + role);
                return role;
            }
            
            // Fallback: extrai do token JWT se não encontrou no contexto
            String authHeader = requestContext.getHeaderString("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("AuthorizationFilter - Nenhum token Bearer encontrado");
                return null;
            }

            String token = authHeader.substring(7);
            String username = JwtUtil.getLoginFromToken(token);
            
            System.out.println("AuthorizationFilter - Username do token: " + username);
            
            if (username != null) {
                User user = userService.findByLogin(username);
                if (user != null) {
                    System.out.println("AuthorizationFilter - Role obtido do banco: " + user.getRole());
                    return user.getRole();
                }
            }
        } catch (Exception e) {
            // Log do erro (em produção, usar logger apropriado)
            System.err.println("AuthorizationFilter - Erro ao obter role do usuário: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("AuthorizationFilter - Nenhum role encontrado, retornando null");
        return null;
    }

    /**
     * Adiciona informações do usuário no contexto da requisição
     */
    private void addUserToContext(ContainerRequestContext requestContext, String userRole) {
        try {
            String authHeader = requestContext.getHeaderString("Authorization");
            String token = authHeader.substring(7);
            String username = JwtUtil.getLoginFromToken(token);
            
            if (username != null) {
                User user = userService.findByLogin(username);
                if (user != null) {
                    // Adicionar propriedades customizadas no contexto
                    requestContext.setProperty("currentUser", user);
                    requestContext.setProperty("currentUserRole", userRole);
                    requestContext.setProperty("currentUserId", user.getId());
                    
                    // Para perfil BUSINESS, COURIER e CUSTOMER, adicionar IDs específicos
                    // Nota: Implementação futura - buscar relacionamentos específicos
                }
            }
        } catch (Exception e) {
            // Log do erro
            System.err.println("Erro ao adicionar usuário no contexto: " + e.getMessage());
        }
    }
}

package com.caracore.cso.security;

import com.caracore.cso.entity.User;
import com.caracore.cso.service.UserService;
import com.caracore.cso.util.JwtUtil;

/**
 * Utilitário para obter informações do usuário autenticado
 * nos controllers de forma fácil e segura.
 */
public class SecurityContextHelper {

    private UserService userService;

    public SecurityContextHelper() {
        this.userService = new UserService();
    }

    /**
     * Obtém o usuário atual a partir do token JWT no cabeçalho
     */
    public User getCurrentUser(String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return null;
            }

            String token = authorizationHeader.substring(7);
            String username = JwtUtil.getLoginFromToken(token);
            
            if (username != null) {
                return userService.findByLogin(username);
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter usuário atual: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Obtém o role do usuário atual
     */
    public String getCurrentUserRole(String authorizationHeader) {
        User user = getCurrentUser(authorizationHeader);
        return user != null ? user.getRole() : null;
    }

    /**
     * Obtém o ID do usuário atual
     */
    public Long getCurrentUserId(String authorizationHeader) {
        User user = getCurrentUser(authorizationHeader);
        return user != null ? user.getId() : null;
    }

    /**
     * Verifica se o usuário atual tem um role específico
     */
    public boolean hasRole(String authorizationHeader, String role) {
        String currentRole = getCurrentUserRole(authorizationHeader);
        return currentRole != null && currentRole.equals(role);
    }

    /**
     * Verifica se o usuário atual é ADMIN
     */
    public boolean isAdmin(String authorizationHeader) {
        return hasRole(authorizationHeader, "ADMIN");
    }

    /**
     * Verifica se o usuário atual é BUSINESS
     */
    public boolean isBusiness(String authorizationHeader) {
        return hasRole(authorizationHeader, "BUSINESS");
    }

    /**
     * Verifica se o usuário atual é COURIER
     */
    public boolean isCourier(String authorizationHeader) {
        return hasRole(authorizationHeader, "COURIER");
    }

    /**
     * Verifica se o usuário atual é CUSTOMER
     */
    public boolean isCustomer(String authorizationHeader) {
        return hasRole(authorizationHeader, "CUSTOMER");
    }
}

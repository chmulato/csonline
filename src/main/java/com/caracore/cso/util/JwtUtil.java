package com.caracore.cso.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilitário para geração e validação de JWT tokens
 */
public class JwtUtil {
    
    // Chave secreta para assinar tokens (em produção, deve vir de variáveis de ambiente)
    private static final String SECRET_KEY = "csonline-jwt-secret-key-very-long-and-secure-2025-more-than-32-chars";
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    
    // Tempo de expiração: 24 horas
    private static final long EXPIRATION_TIME = 86400000; // 24 * 60 * 60 * 1000
    
    /**
     * Gera um token JWT para o usuário
     */
    public static String generateToken(String login, String role, Long userId) {
        return Jwts.builder()
                .subject(login)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SIGNING_KEY)
                .compact();
    }
    
    /**
     * Extrai o login do usuário do token
     */
    public static String getLoginFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
    
    /**
     * Extrai o role do usuário do token
     */
    public static String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }
    
    /**
     * Extrai o ID do usuário do token
     */
    public static Long getUserIdFromToken(String token) {
        return getClaimsFromToken(token).get("userId", Long.class);
    }
    
    /**
     * Verifica se o token está expirado
     */
    public static boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }
    
    /**
     * Valida o token
     */
    public static boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extrai todas as claims do token
     */
    private static Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(SIGNING_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

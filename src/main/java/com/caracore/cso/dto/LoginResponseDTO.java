package com.caracore.cso.dto;

/**
 * DTO para resposta de login com JWT token
 */
public class LoginResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String login;
    private String role;
    private Long expiresIn = 86400L; // 24 horas em segundos

    // Constructors
    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, Long id, String name, String login, String role) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.login = login;
        this.role = role;
    }

    // Getters e Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
}

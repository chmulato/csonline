package com.caracore.cso.controller;

import com.caracore.cso.dto.LoginDTO;
import com.caracore.cso.dto.UserDTO;
import com.caracore.cso.entity.User;
import com.caracore.cso.service.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
public class LoginController {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    private final LoginService loginService;

    public LoginController() {
        this.loginService = new LoginService();
    }

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO loginDTO) {
        try {
            User user = loginService.authenticate(loginDTO.getLogin(), loginDTO.getPassword());
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setLogin(user.getLogin());
            userDTO.setRole(user.getRole());
            return Response.ok(userDTO).build();
        } catch (SecurityException e) {
            logger.warn("Login inválido para usuário: {}", loginDTO.getLogin());
            return Response.status(Response.Status.UNAUTHORIZED).entity("Login ou senha inválidos").build();
        } catch (Exception e) {
            logger.error("Erro ao autenticar login", e);
            return Response.serverError().entity("Erro ao autenticar login").build();
        }
    }
}

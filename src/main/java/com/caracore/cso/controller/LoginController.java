package com.caracore.cso.controller;

import com.caracore.cso.dto.LoginDTO;
import com.caracore.cso.dto.UserDTO;
import com.caracore.cso.entity.User;
import com.caracore.cso.service.LoginService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
public class LoginController {
    private final LoginService loginService = new LoginService();

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
            return Response.status(Response.Status.UNAUTHORIZED).entity("Login ou senha inválidos").build();
        }
    }
}

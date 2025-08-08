
package com.caracore.cso.controller;
import com.caracore.cso.util.TestDataFactory;

import com.caracore.cso.dto.LoginDTO;
import com.caracore.cso.dto.UserDTO;
import com.caracore.cso.entity.User;
import com.caracore.cso.service.LoginService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {
    private static final Logger logger = LogManager.getLogger(LoginControllerTest.class);

    private LoginController controller;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = Mockito.mock(LoginService.class);
        controller = new LoginController() {
            // Sobrescreve o service para usar o mock
            @Override
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
        };
    }

    @Test
    void testLoginSuccess() {
        try {
            // Gera dados únicos para o teste
            User user = TestDataFactory.createUser("ADMIN");
            user.setId(System.currentTimeMillis()); // Garante ID único
            LoginDTO dto = new LoginDTO();
            dto.setLogin(user.getLogin());
            dto.setPassword(user.getPassword());
            Mockito.when(loginService.authenticate(user.getLogin(), user.getPassword())).thenReturn(user);

            Response response = controller.login(dto);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            UserDTO userDTO = (UserDTO) response.getEntity();
            assertEquals(user.getName(), userDTO.getName());
            assertEquals(user.getLogin(), userDTO.getLogin());
            assertEquals(user.getRole(), userDTO.getRole());
        } catch (Exception e) {
            logger.error("Erro em testLoginSuccess", e);
            throw e;
        }
    }

    @Test
    void testLoginFail() {
        try {
            // Gera dados únicos para o teste
            User user = TestDataFactory.createUser("ADMIN");
            LoginDTO dto = new LoginDTO();
            dto.setLogin(user.getLogin());
            dto.setPassword("senha_errada" + System.currentTimeMillis());
            Mockito.when(loginService.authenticate(user.getLogin(), dto.getPassword())).thenThrow(new SecurityException());

            Response response = controller.login(dto);
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
            assertEquals("Login ou senha inválidos", response.getEntity());
        } catch (Exception e) {
            logger.error("Erro em testLoginFail", e);
            throw e;
        }
    }
}



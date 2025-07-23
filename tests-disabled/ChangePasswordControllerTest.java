package br.com.mulato.cso.view.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ChangePasswordController changePasswordController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(changePasswordController).build();
    }

    @Test
    void shouldDisplayChangePasswordForm() throws Exception {
        mockMvc.perform(get("/change-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attributeExists("changePasswordForm"));
    }

    @Test
    void shouldChangePasswordSuccessfully() throws Exception {
        when(userService.validateCurrentPassword(anyString(), anyString())).thenReturn(true);
        when(userService.updatePassword(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/change-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("currentPassword", "oldPassword123")
                .param("newPassword", "newPassword123")
                .param("confirmPassword", "newPassword123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attribute("successMessage", "Senha alterada com sucesso!"));

        verify(userService).updatePassword(anyString(), anyString());
    }

    @Test
    void shouldFailWhenCurrentPasswordIsInvalid() throws Exception {
        when(userService.validateCurrentPassword(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/change-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("currentPassword", "wrongPassword")
                .param("newPassword", "newPassword123")
                .param("confirmPassword", "newPassword123"))
                .andExpected(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attribute("errorMessage", "Senha atual incorreta"));
    }

    @Test
    void shouldFailWhenPasswordsDoNotMatch() throws Exception {
        mockMvc.perform(post("/change-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("currentPassword", "oldPassword123")
                .param("newPassword", "newPassword123")
                .param("confirmPassword", "differentPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attribute("errorMessage", "As senhas não coincidem"));
    }

    @Test
    void shouldFailWhenNewPasswordIsTooWeak() throws Exception {
        mockMvc.perform(post("/change-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("currentPassword", "oldPassword123")
                .param("newPassword", "123")
                .param("confirmPassword", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("change-password"))
                .andExpect(model().attribute("errorMessage", "A nova senha deve ter pelo menos 8 caracteres"));
    }
}

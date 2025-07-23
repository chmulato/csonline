package br.com.mulato.cso.view.page;

import br.com.mulato.cso.service.PasswordService;
import br.com.mulato.cso.service.UserService;
import br.com.mulato.cso.model.User;
import br.com.mulato.cso.exception.InvalidPasswordException;
import br.com.mulato.cso.exception.UserNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangePasswordPage Tests")
class ChangePasswordPageTest {

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserService userService;

    @Mock
    private FacesContext facesContext;

    @InjectMocks
    private ChangePasswordPage changePasswordPage;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        changePasswordPage.setCurrentPassword("");
        changePasswordPage.setNewPassword("");
        changePasswordPage.setConfirmPassword("");
    }

    @Test
    @DisplayName("Deve inicializar a página corretamente")
    void shouldInitializePageCorrectly() {
        // When
        changePasswordPage.init();

        // Then
        assertNotNull(changePasswordPage);
        assertEquals("", changePasswordPage.getCurrentPassword());
        assertEquals("", changePasswordPage.getNewPassword());
        assertEquals("", changePasswordPage.getConfirmPassword());
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void shouldChangePasswordSuccessfully() {
        // Given
        changePasswordPage.setCurrentPassword("oldPassword123");
        changePasswordPage.setNewPassword("newPassword123");
        changePasswordPage.setConfirmPassword("newPassword123");

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(passwordService.validateCurrentPassword(mockUser, "oldPassword123")).thenReturn(true);
        when(passwordService.updateUserPassword(mockUser, "newPassword123")).thenReturn(true);

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("success", result);
        verify(passwordService).updateUserPassword(mockUser, "newPassword123");
        assertEquals("", changePasswordPage.getCurrentPassword());
        assertEquals("", changePasswordPage.getNewPassword());
        assertEquals("", changePasswordPage.getConfirmPassword());
    }

    @Test
    @DisplayName("Deve falhar quando senha atual está incorreta")
    void shouldFailWhenCurrentPasswordIsIncorrect() {
        // Given
        changePasswordPage.setCurrentPassword("wrongPassword");
        changePasswordPage.setNewPassword("newPassword123");
        changePasswordPage.setConfirmPassword("newPassword123");

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(passwordService.validateCurrentPassword(mockUser, "wrongPassword")).thenReturn(false);

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("failure", result);
        verify(passwordService, never()).updateUserPassword(any(User.class), anyString());
    }

    @Test
    @DisplayName("Deve falhar quando novas senhas não coincidem")
    void shouldFailWhenNewPasswordsDoNotMatch() {
        // Given
        changePasswordPage.setCurrentPassword("oldPassword123");
        changePasswordPage.setNewPassword("newPassword123");
        changePasswordPage.setConfirmPassword("differentPassword");

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("failure", result);
        verify(passwordService, never()).validateCurrentPassword(any(User.class), anyString());
        verify(passwordService, never()).updateUserPassword(any(User.class), anyString());
    }

    @Test
    @DisplayName("Deve validar força da nova senha")
    void shouldValidateNewPasswordStrength() {
        // Given
        changePasswordPage.setCurrentPassword("oldPassword123");
        changePasswordPage.setNewPassword("123"); // Senha fraca
        changePasswordPage.setConfirmPassword("123");

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("failure", result);
        verify(passwordService, never()).validateCurrentPassword(any(User.class), anyString());
    }

    @Test
    @DisplayName("Deve tratar exceção de senha inválida")
    void shouldHandleInvalidPasswordException() throws Exception {
        // Given
        changePasswordPage.setCurrentPassword("oldPassword123");
        changePasswordPage.setNewPassword("newPassword123");
        changePasswordPage.setConfirmPassword("newPassword123");

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(passwordService.validateCurrentPassword(mockUser, "oldPassword123")).thenReturn(true);
        when(passwordService.updateUserPassword(mockUser, "newPassword123"))
                .thenThrow(new InvalidPasswordException("Senha não atende aos critérios"));

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("failure", result);
    }

    @Test
    @DisplayName("Deve tratar usuário não encontrado")
    void shouldHandleUserNotFoundException() {
        // Given
        changePasswordPage.setCurrentPassword("oldPassword123");
        changePasswordPage.setNewPassword("newPassword123");
        changePasswordPage.setConfirmPassword("newPassword123");

        when(userService.getCurrentUser()).thenThrow(new UserNotFoundException("Usuário não encontrado"));

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("failure", result);
        verify(passwordService, never()).validateCurrentPassword(any(User.class), anyString());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios")
    void shouldValidateRequiredFields() {
        // Given - campos vazios
        changePasswordPage.setCurrentPassword("");
        changePasswordPage.setNewPassword("");
        changePasswordPage.setConfirmPassword("");

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("failure", result);
        verify(passwordService, never()).validateCurrentPassword(any(User.class), anyString());
    }

    @Test
    @DisplayName("Deve cancelar alteração de senha")
    void shouldCancelPasswordChange() {
        // Given
        changePasswordPage.setCurrentPassword("somePassword");
        changePasswordPage.setNewPassword("newPassword");
        changePasswordPage.setConfirmPassword("newPassword");

        // When
        String result = changePasswordPage.cancel();

        // Then
        assertEquals("cancelled", result);
        assertEquals("", changePasswordPage.getCurrentPassword());
        assertEquals("", changePasswordPage.getNewPassword());
        assertEquals("", changePasswordPage.getConfirmPassword());
    }

    @Test
    @DisplayName("Deve verificar se senhas são iguais")
    void shouldCheckIfPasswordsMatch() {
        // Given
        changePasswordPage.setNewPassword("password123");
        changePasswordPage.setConfirmPassword("password123");

        // When
        boolean match = changePasswordPage.doPasswordsMatch();

        // Then
        assertTrue(match);
    }

    @Test
    @DisplayName("Deve verificar se senhas são diferentes")
    void shouldCheckIfPasswordsAreDifferent() {
        // Given
        changePasswordPage.setNewPassword("password123");
        changePasswordPage.setConfirmPassword("differentPassword");

        // When
        boolean match = changePasswordPage.doPasswordsMatch();

        // Then
        assertFalse(match);
    }
}

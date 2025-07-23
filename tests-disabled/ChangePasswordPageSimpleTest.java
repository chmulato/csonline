package br.com.mulato.cso.view.page;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordPageSimpleTest {

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserSession userSession;

    @Mock
    private MessageService messageService;

    private ChangePasswordPage changePasswordPage;

    @BeforeEach
    void setUp() {
        changePasswordPage = new ChangePasswordPage();
        changePasswordPage.setPasswordService(passwordService);
        changePasswordPage.setUserSession(userSession);
        changePasswordPage.setMessageService(messageService);
    }

    @Test
    void shouldInitializePageCorrectly() {
        // When
        changePasswordPage.init();

        // Then
        assertNotNull(changePasswordPage.getCurrentPassword());
        assertNotNull(changePasswordPage.getNewPassword());
        assertNotNull(changePasswordPage.getConfirmPassword());
        assertEquals("", changePasswordPage.getCurrentPassword());
        assertEquals("", changePasswordPage.getNewPassword());
        assertEquals("", changePasswordPage.getConfirmPassword());
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        // Given
        changePasswordPage.setCurrentPassword("oldPassword123");
        changePasswordPage.setNewPassword("newPassword123");
        changePasswordPage.setConfirmPassword("newPassword123");
        
        when(passwordService.validateCurrentPassword(anyString(), anyString())).thenReturn(true);
        when(passwordService.updatePassword(anyString(), anyString())).thenReturn(true);

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("success", result);
        verify(passwordService).updatePassword(anyString(), anyString());
        verify(messageService).addSuccessMessage("Senha alterada com sucesso!");
    }

    @Test
    void shouldFailWhenCurrentPasswordIsWrong() {
        // Given
        changePasswordPage.setCurrentPassword("wrongPassword");
        changePasswordPage.setNewPassword("newPassword123");
        changePasswordPage.setConfirmPassword("newPassword123");
        
        when(passwordService.validateCurrentPassword(anyString(), anyString())).thenReturn(false);

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("failure", result);
        verify(messageService).addErrorMessage("Senha atual incorreta");
        verify(passwordService, never()).updatePassword(anyString(), anyString());
    }

    @Test
    void shouldFailWhenPasswordsDoNotMatch() {
        // Given
        changePasswordPage.setCurrentPassword("oldPassword123");
        changePasswordPage.setNewPassword("newPassword123");
        changePasswordPage.setConfirmPassword("differentPassword");

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("failure", result);
        verify(messageService).addErrorMessage("As novas senhas não coincidem");
        verify(passwordService, never()).validateCurrentPassword(anyString(), anyString());
    }

    @Test
    void shouldFailWhenNewPasswordIsTooShort() {
        // Given
        changePasswordPage.setCurrentPassword("oldPassword123");
        changePasswordPage.setNewPassword("123");
        changePasswordPage.setConfirmPassword("123");

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("failure", result);
        verify(messageService).addErrorMessage("A nova senha deve ter pelo menos 8 caracteres");
    }

    @Test
    void shouldFailWhenFieldsAreEmpty() {
        // Given
        changePasswordPage.setCurrentPassword("");
        changePasswordPage.setNewPassword("");
        changePasswordPage.setConfirmPassword("");

        // When
        String result = changePasswordPage.changePassword();

        // Then
        assertEquals("failure", result);
        verify(messageService).addErrorMessage("Todos os campos são obrigatórios");
    }

    @Test
    void shouldClearFormAfterSuccessfulChange() {
        // Given
        changePasswordPage.setCurrentPassword("oldPassword123");
        changePasswordPage.setNewPassword("newPassword123");
        changePasswordPage.setConfirmPassword("newPassword123");
        
        when(passwordService.validateCurrentPassword(anyString(), anyString())).thenReturn(true);
        when(passwordService.updatePassword(anyString(), anyString())).thenReturn(true);

        // When
        changePasswordPage.changePassword();

        // Then
        assertEquals("", changePasswordPage.getCurrentPassword());
        assertEquals("", changePasswordPage.getNewPassword());
        assertEquals("", changePasswordPage.getConfirmPassword());
    }
}

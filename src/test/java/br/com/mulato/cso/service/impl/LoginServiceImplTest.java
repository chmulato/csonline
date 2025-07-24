package br.com.mulato.cso.service.impl;

import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.dao.LoginDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.LoginVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {
    private LoginServiceImpl loginService;
    private LoginDAO loginDAOMock;

    @BeforeEach
    void setUp() {
        loginService = new LoginServiceImpl();
        loginDAOMock = mock(LoginDAO.class);
    }

    @Test
    @DisplayName("Deve autenticar com sucesso quando login e senha válidos")
    void deveAutenticarComSucesso() throws Exception {
        LoginVO login = new LoginVO();
        login.setLogin("admin");
        login.setPassword("123");

        try (MockedStatic<FactoryDAO> factoryDAOStatic = mockStatic(FactoryDAO.class)) {
            FactoryDAO factoryDAOMock = mock(FactoryDAO.class);
            factoryDAOStatic.when(FactoryDAO::getInstancia).thenReturn(factoryDAOMock);
            when(factoryDAOMock.getLoginDAO()).thenReturn(loginDAOMock);
            doThrow(new DAOException("erro")).when(loginDAOMock).authenticate(login);

            Boolean result = loginService.authenticate(login);
            assertTrue(result);
        }
    }

    @Test
    @DisplayName("Deve lançar WebException se login for null na autenticação")
    void deveLancarExcecaoSeLoginNullAutenticacao() {
        assertThrows(WebException.class, () -> loginService.authenticate(null));
    }

    @Test
    @DisplayName("Deve lançar WebException se login.getLogin() for null na autenticação")
    void deveLancarExcecaoSeLoginLoginNullAutenticacao() {
        LoginVO login = new LoginVO();
        login.setLogin(null);
        login.setPassword("123");
        assertThrows(WebException.class, () -> loginService.authenticate(login));
    }

    @Test
    @DisplayName("Deve lançar WebException se login.getPassword() for null na autenticação")
    void deveLancarExcecaoSePasswordNullAutenticacao() {
        LoginVO login = new LoginVO();
        login.setLogin("admin");
        login.setPassword(null);
        assertThrows(WebException.class, () -> loginService.authenticate(login));
    }

    @Test
    @DisplayName("Deve lançar WebException se DAOException for lançada na autenticação")
    void deveLancarWebExceptionSeDAOExceptionAutenticacao() throws Exception {
        LoginVO login = new LoginVO();
        login.setLogin("admin");
        login.setPassword("123");

        try (MockedStatic<FactoryDAO> factoryDAOStatic = mockStatic(FactoryDAO.class)) {
            FactoryDAO factoryDAOMock = mock(FactoryDAO.class);
            factoryDAOStatic.when(FactoryDAO::getInstancia).thenReturn(factoryDAOMock);
            when(factoryDAOMock.getLoginDAO()).thenReturn(loginDAOMock);
            doThrow(new DAOException("erro")).when(loginDAOMock).authenticate(login);
            assertThrows(WebException.class, () -> loginService.authenticate(login));
        }
    }

    @Test
    @DisplayName("Deve trocar senha com sucesso")
    void deveTrocarSenhaComSucesso() throws Exception {
        LoginVO login = new LoginVO();
        login.setLogin("admin");
        login.setPassword("123");
        login.setRepeat("123");
        login.setNewPassword("novaSenha");
        login.setNewRepeat("novaSenha");
        login.setEmail("admin@email.com");

        try (MockedStatic<FactoryDAO> factoryDAOStatic = mockStatic(FactoryDAO.class)) {
            FactoryDAO factoryDAOMock = mock(FactoryDAO.class);
            factoryDAOStatic.when(FactoryDAO::getInstancia).thenReturn(factoryDAOMock);
            when(factoryDAOMock.getLoginDAO()).thenReturn(loginDAOMock);
            doNothing().when(loginDAOMock).passwordChange(login);

            assertDoesNotThrow(() -> loginService.changePassword(login, false));
        }
    }

    @Test
    @DisplayName("Deve lançar WebException se login for null na troca de senha")
    void deveLancarExcecaoSeLoginNullTrocaSenha() {
        assertThrows(WebException.class, () -> loginService.changePassword(null, false));
    }

    @Test
    @DisplayName("Deve lançar WebException se login.getLogin() for null na troca de senha")
    void deveLancarExcecaoSeLoginLoginNullTrocaSenha() {
        LoginVO login = new LoginVO();
        login.setLogin(null);
        login.setPassword("123");
        login.setRepeat("123");
        login.setNewPassword("novaSenha");
        login.setNewRepeat("novaSenha");
        login.setEmail("admin@email.com");
        assertThrows(WebException.class, () -> loginService.changePassword(login, false));
    }

    @Test
    @DisplayName("Deve lançar WebException se login.getPassword() for null na troca de senha")
    void deveLancarExcecaoSePasswordNullTrocaSenha() {
        LoginVO login = new LoginVO();
        login.setLogin("admin");
        login.setPassword(null);
        login.setRepeat("123");
        login.setNewPassword("novaSenha");
        login.setNewRepeat("novaSenha");
        login.setEmail("admin@email.com");
        assertThrows(WebException.class, () -> loginService.changePassword(login, false));
    }

    // Adicione outros testes de validação de senha, repetição, email, etc. conforme necessário

    @Test
    @DisplayName("Deve lançar WebException se nova senha e repetição forem diferentes na troca de senha")
    void deveLancarExcecaoSeNovaSenhaRepeticaoDiferenteTrocaSenha() {
        LoginVO login = new LoginVO();
        login.setLogin("admin");
        login.setPassword("123");
        login.setRepeat("123");
        login.setNewPassword("novaSenha");
        login.setNewRepeat("diferenteSenha");
        login.setEmail("admin@email.com");
        assertThrows(WebException.class, () -> loginService.changePassword(login, false));
    }

    @Test
    @DisplayName("Deve lançar WebException se email for inválido na troca de senha")
    void deveLancarExcecaoSeEmailInvalidoTrocaSenha() {
        LoginVO login = new LoginVO();
        login.setLogin("admin");
        login.setPassword("123");
        login.setRepeat("123");
        login.setNewPassword("novaSenha");
        login.setNewRepeat("novaSenha");
        login.setEmail("email-invalido"); // email sem @
        assertThrows(WebException.class, () -> loginService.changePassword(login, false));
    }
}

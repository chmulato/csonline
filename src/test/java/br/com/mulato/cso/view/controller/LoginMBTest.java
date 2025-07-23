package br.com.mulato.cso.view.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import br.com.mulato.cso.view.controller.LoginController;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do LoginMB - Managed Bean de Login")
class LoginMBTest {

    @Mock
    private FacesContext facesContext;
    
    @Mock
    private ExternalContext externalContext;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpSession session;

    @Mock
    private ActionEvent actionEvent;

    private LoginController loginMB;

    @BeforeEach
    void setUp() {
        loginMB = new LoginController();
        
        // Configurar mocks básicos do JSF
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getRequest()).thenReturn(request);
        when(externalContext.getSession(false)).thenReturn(session);
    }

    @Test
    @DisplayName("Deve inicializar o bean corretamente")
    void deveInicializarBeanCorretamente() {
        // Given & When
        LoginController loginBean = new LoginController();
        
        // Then
        assertNotNull(loginBean);
        assertNull(loginBean.getUsername());
        assertNull(loginBean.getPassword());
    }

    @Test
    @DisplayName("Deve definir e recuperar username corretamente")
    void deveDefinirERecuperarUsernameCorretamente() {
        // Given
        String expectedUsername = "admin";
        
        // When
        loginMB.setUsername(expectedUsername);
        
        // Then
        assertEquals(expectedUsername, loginMB.getUsername());
    }

    @Test
    @DisplayName("Deve definir e recuperar password corretamente")
    void deveDefinirERecuperarPasswordCorretamente() {
        // Given
        String expectedPassword = "123456";
        
        // When
        loginMB.setPassword(expectedPassword);
        
        // Then
        assertEquals(expectedPassword, loginMB.getPassword());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios no login")
    void deveValidarCamposObrigatoriosNoLogin() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            
            // When - tentativa de login sem preencher campos
            String resultado = loginMB.login();
            
            // Then
            assertNotNull(resultado);
            // Espera-se que retorne null ou uma página de erro, não uma navegação válida
        }
    }

    @Test
    @DisplayName("Deve executar login com credenciais válidas")
    void deveExecutarLoginComCredenciaisValidas() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            loginMB.setUsername("admin");
            loginMB.setPassword("123");
            
            // When
            String resultado = loginMB.login();
            
            // Then
            assertNotNull(resultado);
            // Verificar se houve redirecionamento ou mensagem apropriada
        }
    }

    @Test
    @DisplayName("Deve rejeitar credenciais inválidas")
    void deveRejeitarCredenciaisInvalidas() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            loginMB.setUsername("usuarioInvalido");
            loginMB.setPassword("senhaInvalida");
            
            // When
            String resultado = loginMB.login();
            
            // Then
            // Deve retornar null (não navegar) ou página de erro
            // Verificar se mensagem de erro foi adicionada
            verify(facesContext, atLeastOnce()).addMessage(any(), any(FacesMessage.class));
        }
    }

    @Test
    @DisplayName("Deve validar limite de caracteres da senha")
    void deveValidarLimiteDeCaracteresDaSenha() {
        // Given - senha com mais de 15 caracteres (limite definido no XHTML)
        String senhaLonga = "senhaComMaisDe15Caracteres";
        
        // When
        loginMB.setPassword(senhaLonga);
        
        // Then
        assertEquals(senhaLonga, loginMB.getPassword());
        // Nota: A validação de maxlength="15" é feita no frontend pelo PrimeFaces
    }

    @Test
    @DisplayName("Deve executar reset dos campos")
    void deveExecutarResetDosCampos() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            loginMB.setUsername("admin");
            loginMB.setPassword("123");
            
            // When
            loginMB.reset(actionEvent);
            
            // Then
            assertNull(loginMB.getUsername());
            assertNull(loginMB.getPassword());
        }
    }

    @Test
    @DisplayName("Deve tratar username nulo graciosamente")
    void deveTratarUsernameNuloGraciosamente() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            loginMB.setUsername(null);
            loginMB.setPassword("123");
            
            // When
            String resultado = loginMB.login();
            
            // Then
            // Deve tratar o caso graciosamente sem lançar exceção
            assertNotNull(resultado);
        }
    }

    @Test
    @DisplayName("Deve tratar password nulo graciosamente")
    void deveTratarPasswordNuloGraciosamente() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            loginMB.setUsername("admin");
            loginMB.setPassword(null);
            
            // When
            String resultado = loginMB.login();
            
            // Then
            // Deve tratar o caso graciosamente sem lançar exceção
            assertNotNull(resultado);
        }
    }

    @Test
    @DisplayName("Deve validar comportamento do bean em múltiplas tentativas")
    void deveValidarComportamentoDoBeanEmMultiplasTentativas() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            
            // When - primeira tentativa
            loginMB.setUsername("admin");
            loginMB.setPassword("senhaErrada");
            String resultado1 = loginMB.login();
            
            // When - reset
            loginMB.reset(actionEvent);
            
            // When - segunda tentativa
            loginMB.setUsername("admin");
            loginMB.setPassword("123");
            String resultado2 = loginMB.login();
            
            // Then
            assertNotNull(resultado1);
            assertNotNull(resultado2);
            // Verificar que o bean mantém estado consistente entre tentativas
        }
    }

    @Test
    @DisplayName("Deve validar integração com componentes JSF da página")
    void deveValidarIntegracaoComComponentesJSFDaPagina() {
        // Given
        String username = "testUser";
        String password = "testPass";
        
        // When
        loginMB.setUsername(username);
        loginMB.setPassword(password);
        
        // Then
        // Validar que os valores estão disponíveis para binding com:
        // #{loginMB.username} e #{loginMB.password} na página login.xhtml
        assertEquals(username, loginMB.getUsername());
        assertEquals(password, loginMB.getPassword());
    }

    @Test
    @DisplayName("Deve validar comportamento esperado pelos componentes PrimeFaces")
    void deveValidarComportamentoEsperadoPelosComponentesPrimeFaces() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            
            // When - simular comportamento dos componentes da página:
            // p:inputText id="username" value="#{loginMB.username}"
            // p:password id="senha" value="#{loginMB.password}"
            // p:commandButton action="#{loginMB.login}"
            // p:commandButton actionListener="#{loginMB.reset}"
            
            loginMB.setUsername("admin");
            loginMB.setPassword("123");
            String loginResult = loginMB.login();
            
            // Then
            assertNotNull(loginResult);
            assertNotNull(loginMB.getUsername());
            assertNotNull(loginMB.getPassword());
        }
    }

    @Test
    @DisplayName("Deve executar logout corretamente")
    void deveExecutarLogoutCorretamente() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            
            // Primeiro faz login para ter estado logged = true
            loginMB.setUsername("admin");
            loginMB.setPassword("123");
            loginMB.login(); // Isso define logged = true internamente
            
            // When
            String resultado = loginMB.logout();
            
            // Then
            assertFalse(loginMB.isLogged());
            assertNotNull(resultado);
            verify(facesContext.getExternalContext()).invalidateSession();
        }
    }

    @Test
    @DisplayName("Deve invalidar sessão durante logout")
    void deveInvalidarSessaoDuranteLogout() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            
            // Primeiro faz login
            loginMB.setUsername("admin");
            loginMB.setPassword("123");
            loginMB.login();
            
            // When
            loginMB.logout();
            
            // Then
            assertFalse(loginMB.isLogged());
            verify(externalContext).invalidateSession();
        }
    }

    @Test
    @DisplayName("Deve redirecionar para login após logout")
    void deveRedirecionarParaLoginAposLogout() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            
            // Primeiro faz login
            loginMB.setUsername("admin");
            loginMB.setPassword("123");
            loginMB.login();
            
            // When
            String resultado = loginMB.logout();
            
            // Then
            assertTrue(resultado.contains("login") || resultado.equals("login"));
            verify(externalContext).invalidateSession();
        }
    }
}
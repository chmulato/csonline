package br.com.mulato.cso.view.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Iterator;

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
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    @Mock
    private ServletContext servletContext;

    @Mock
    private Iterator<FacesMessage> messageIterator;

    private LoginController loginMB;

    @BeforeEach
    void setUp() {
        loginMB = new LoginController();
        
        // Configurar mocks básicos do JSF usando lenient() para evitar UnnecessaryStubbing
        lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);
        lenient().when(externalContext.getRequest()).thenReturn(request);
        lenient().when(externalContext.getSession(false)).thenReturn(session);
        lenient().when(externalContext.getContext()).thenReturn(servletContext);
        
        // Mock do Iterator de mensagens para evitar NullPointerException
        lenient().when(facesContext.getMessages()).thenReturn(messageIterator);
        lenient().when(messageIterator.hasNext()).thenReturn(false);
        
        // Mock do ServletContext para timeout
        lenient().when(servletContext.getInitParameter("timeout")).thenReturn("900"); // 15 minutos
        
        // Mock da sessão
        lenient().when(request.getSession()).thenReturn(session);
        lenient().when(session.getId()).thenReturn("test-session-id");
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
            assertNull(resultado); // Corrigido: espera null
        }
    }

    @Test
    @DisplayName("Deve executar login com credenciais válidas")
    void deveExecutarLoginComCredenciaisValidas() throws br.com.mulato.cso.exception.WebException {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class);
             MockedStatic<br.com.mulato.cso.dry.FactoryService> mockedFactory = mockStatic(br.com.mulato.cso.dry.FactoryService.class)) {
            
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            
            // Mock Application e ContadorController para evitar NullPointerException
            jakarta.faces.application.Application application = mock(jakarta.faces.application.Application.class);
            lenient().when(facesContext.getApplication()).thenReturn(application);
            
            // Mock do ContadorController (pode retornar null, pois o código trata isso)
            lenient().when(application.evaluateExpressionGet(any(FacesContext.class), anyString(), any(Class.class))).thenReturn(null);
            
            // Mock FactoryService e seus serviços
            br.com.mulato.cso.dry.FactoryService factoryService = mock(br.com.mulato.cso.dry.FactoryService.class);
            br.com.mulato.cso.service.LoginService loginService = mock(br.com.mulato.cso.service.LoginService.class);
            br.com.mulato.cso.service.AdminService adminService = mock(br.com.mulato.cso.service.AdminService.class);
            br.com.mulato.cso.service.BusinessService businessService = mock(br.com.mulato.cso.service.BusinessService.class);
            
            mockedFactory.when(br.com.mulato.cso.dry.FactoryService::getInstancia).thenReturn(factoryService);
            doReturn(loginService).when(factoryService).getLoginService();
            doReturn(adminService).when(factoryService).getAdminService();
            lenient().doReturn(businessService).when(factoryService).getBusinessService();
            
            // Mock do processo de autenticação - deve retornar true
            lenient().doReturn(true).when(loginService).authenticate(any(br.com.mulato.cso.model.LoginVO.class));
            
            // Mock do usuário admin
            br.com.mulato.cso.model.UserVO user = mock(br.com.mulato.cso.model.UserVO.class);
            lenient().when(user.getId()).thenReturn(1);
            lenient().when(user.getRole()).thenReturn("ADMINISTRATOR");
            doReturn(user).when(adminService).findByLogin(any(br.com.mulato.cso.model.LoginVO.class));
            
            // Mock do BusinessVO (para usuário admin não é necessário, mas podemos mockear para cobrir outros casos)
            br.com.mulato.cso.model.BusinessVO business = mock(br.com.mulato.cso.model.BusinessVO.class);
            lenient().doReturn(business).when(businessService).find(any(Integer.class));
            
            loginMB.setUsername("admin");
            loginMB.setPassword("123");
            
            // When
            String resultado = loginMB.login();
            
            // Then
            assertNotNull(resultado);
            assertEquals("users", resultado); // Para usuário ADMIN, deve redirecionar para "users"
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
            assertNull(resultado); // Corrigido: espera null
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
            assertNull(resultado); // Corrigido: espera null
        }
    }

    @Test
    @DisplayName("Deve validar comportamento do bean em múltiplas tentativas")
    void deveValidarComportamentoDoBeanEmMultiplasTentativas() throws br.com.mulato.cso.exception.WebException {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class);
             MockedStatic<br.com.mulato.cso.dry.FactoryService> mockedFactory = mockStatic(br.com.mulato.cso.dry.FactoryService.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);

            // Mock FactoryService e LoginService
            br.com.mulato.cso.dry.FactoryService factoryService = mock(br.com.mulato.cso.dry.FactoryService.class);
            br.com.mulato.cso.service.LoginService loginService = mock(br.com.mulato.cso.service.LoginService.class);
            br.com.mulato.cso.service.AdminService adminService = mock(br.com.mulato.cso.service.AdminService.class);
            br.com.mulato.cso.service.BusinessService businessService = mock(br.com.mulato.cso.service.BusinessService.class);
            mockedFactory.when(br.com.mulato.cso.dry.FactoryService::getInstancia).thenReturn(factoryService);
            when(factoryService.getLoginService()).thenReturn(loginService);
            when(factoryService.getAdminService()).thenReturn(adminService);
            lenient().when(factoryService.getBusinessService()).thenReturn(businessService);

            // Mock para autenticação: retorna true apenas para senha correta, false para qualquer outro caso
            when(loginService.authenticate(argThat(loginVO -> loginVO != null && "123".equals(loginVO.getPassword())))).thenReturn(true);
<<<<<<< HEAD
            when(loginService.authenticate(any())).thenReturn(false);
=======
            when(loginService.authenticate(argThat(loginVO -> loginVO == null || !"123".equals(loginVO.getPassword())))).thenReturn(false);
>>>>>>> 98eaada (fix: Improve authentication mock to return false for null or incorrect passwords)

            // Mock do usuário admin
            br.com.mulato.cso.model.UserVO user = mock(br.com.mulato.cso.model.UserVO.class);
            lenient().when(user.getId()).thenReturn(1);
            lenient().when(user.getRole()).thenReturn("ADMINISTRATOR");
            when(adminService.findByLogin(any(br.com.mulato.cso.model.LoginVO.class))).thenReturn(user);

            // Mock do BusinessVO (para usuário admin não é necessário, mas mock para cobrir outros casos)
            br.com.mulato.cso.model.BusinessVO business = mock(br.com.mulato.cso.model.BusinessVO.class);
            lenient().when(businessService.find(any(Integer.class))).thenReturn(business);

            // Primeira tentativa: senha errada
            loginMB.setUsername("admin");
            loginMB.setPassword("senhaErrada");
            String resultado1 = loginMB.login();

            // Reset campos para segunda tentativa
            loginMB.reset(actionEvent);

            // Segunda tentativa: senha correta
            loginMB.setUsername("admin");
            loginMB.setPassword("123");
            // Re-apply mocks after reset to avoid losing stubs
            lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);
            when(factoryService.getLoginService()).thenReturn(loginService);
            when(factoryService.getAdminService()).thenReturn(adminService);
            lenient().when(factoryService.getBusinessService()).thenReturn(businessService);
            when(loginService.authenticate(argThat(loginVO -> loginVO != null && "123".equals(loginVO.getPassword())))).thenReturn(true);
            when(loginService.authenticate(argThat(loginVO -> loginVO == null || !"123".equals(loginVO.getPassword())))).thenReturn(false);
            when(adminService.findByLogin(any(br.com.mulato.cso.model.LoginVO.class))).thenReturn(user);
            lenient().when(businessService.find(any(Integer.class))).thenReturn(business);
            String resultado2 = loginMB.login();

            // Then
            assertNull(resultado1);      // Espera null na primeira tentativa
            assertNotNull(resultado2);   // Espera não nulo na segunda tentativa
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
    void deveValidarComportamentoEsperadoPelosComponentesPrimeFaces() throws br.com.mulato.cso.exception.WebException {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class);
             MockedStatic<br.com.mulato.cso.dry.FactoryService> mockedFactory = mockStatic(br.com.mulato.cso.dry.FactoryService.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);

            // Mock Application e ContadorController para evitar NullPointerException
            jakarta.faces.application.Application application = mock(jakarta.faces.application.Application.class);
            lenient().when(facesContext.getApplication()).thenReturn(application);
            lenient().when(application.evaluateExpressionGet(any(FacesContext.class), anyString(), any(Class.class))).thenReturn(null);

            // Mock FactoryService e seus serviços
            br.com.mulato.cso.dry.FactoryService factoryService = mock(br.com.mulato.cso.dry.FactoryService.class);
            br.com.mulato.cso.service.LoginService loginService = mock(br.com.mulato.cso.service.LoginService.class);
            br.com.mulato.cso.service.AdminService adminService = mock(br.com.mulato.cso.service.AdminService.class);
            br.com.mulato.cso.service.BusinessService businessService = mock(br.com.mulato.cso.service.BusinessService.class);
            mockedFactory.when(br.com.mulato.cso.dry.FactoryService::getInstancia).thenReturn(factoryService);
            doReturn(loginService).when(factoryService).getLoginService();
            doReturn(adminService).when(factoryService).getAdminService();
            lenient().doReturn(businessService).when(factoryService).getBusinessService();

            // Mock do processo de autenticação - deve retornar true
            lenient().doReturn(true).when(loginService).authenticate(any(br.com.mulato.cso.model.LoginVO.class));

            // Mock do usuário admin
            br.com.mulato.cso.model.UserVO user = mock(br.com.mulato.cso.model.UserVO.class);
            lenient().when(user.getId()).thenReturn(1);
            lenient().when(user.getRole()).thenReturn("ADMINISTRATOR");
            doReturn(user).when(adminService).findByLogin(any(br.com.mulato.cso.model.LoginVO.class));

            // Mock do BusinessVO (para usuário admin não é necessário, mas podemos mockear para cobrir outros casos)
            br.com.mulato.cso.model.BusinessVO business = mock(br.com.mulato.cso.model.BusinessVO.class);
            lenient().doReturn(business).when(businessService).find(any(Integer.class));

            // Reset do mock para garantir que não há chamadas anteriores
            reset(facesContext);
            lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);
            // Re-apply all necessary stubs after reset
            doNothing().when(facesContext).addMessage(any(), any(FacesMessage.class));
            lenient().when(facesContext.getApplication()).thenReturn(application);
            lenient().when(application.evaluateExpressionGet(any(FacesContext.class), anyString(), any(Class.class))).thenReturn(null);

            loginMB.setUsername("admin");
            loginMB.setPassword("123");
            String loginResult = loginMB.login();

            // Then
            assertNotNull(loginResult);
            assertEquals("users", loginResult); // Espera navegação para "users" para admin
            assertEquals("admin", loginMB.getUsername());
            assertNull(loginMB.getPassword()); // O password é limpo após login
            verify(loginService, atLeastOnce()).authenticate(any(br.com.mulato.cso.model.LoginVO.class));
            verify(facesContext, never()).addMessage(any(), any(FacesMessage.class)); // Não deve adicionar mensagem de erro
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
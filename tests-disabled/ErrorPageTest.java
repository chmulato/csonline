package br.com.mulato.cso.view.page;

import br.com.mulato.cso.service.LogService;
import br.com.mulato.cso.service.ErrorService;
import br.com.mulato.cso.model.ErrorInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ErrorPage Tests")
class ErrorPageTest {

    @Mock
    private LogService logService;

    @Mock
    private ErrorService errorService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ErrorPage errorPage;

    private ErrorInfo mockErrorInfo;

    @BeforeEach
    void setUp() {
        mockErrorInfo = new ErrorInfo();
        mockErrorInfo.setStatusCode(500);
        mockErrorInfo.setMessage("Internal Server Error");
        mockErrorInfo.setRequestUri("/test/page");
        mockErrorInfo.setTimestamp(System.currentTimeMillis());

        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getRequest()).thenReturn(request);
    }

    @Test
    @DisplayName("Deve inicializar página de erro corretamente")
    void shouldInitializeErrorPageCorrectly() {
        // Given
        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(404);
        when(request.getAttribute("javax.servlet.error.message")).thenReturn("Page Not Found");
        when(request.getAttribute("javax.servlet.error.request_uri")).thenReturn("/missing/page");

        // When
        errorPage.init();

        // Then
        assertNotNull(errorPage.getErrorInfo());
        assertEquals(404, errorPage.getErrorInfo().getStatusCode());
        assertEquals("Page Not Found", errorPage.getErrorInfo().getMessage());
        assertEquals("/missing/page", errorPage.getErrorInfo().getRequestUri());
    }

    @Test
    @DisplayName("Deve tratar erro 404 - Página não encontrada")
    void shouldHandle404Error() {
        // Given
        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(404);
        when(request.getAttribute("javax.servlet.error.message")).thenReturn("Page Not Found");

        // When
        errorPage.init();

        // Then
        assertEquals("Página não encontrada", errorPage.getUserFriendlyMessage());
        assertEquals("error-404", errorPage.getErrorTemplate());
        verify(logService).logWarning(contains("404"));
    }

    @Test
    @DisplayName("Deve tratar erro 500 - Erro interno do servidor")
    void shouldHandle500Error() {
        // Given
        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(500);
        when(request.getAttribute("javax.servlet.error.message")).thenReturn("Internal Server Error");
        when(request.getAttribute("javax.servlet.error.exception")).thenReturn(new RuntimeException("Database error"));

        // When
        errorPage.init();

        // Then
        assertEquals("Erro interno do servidor", errorPage.getUserFriendlyMessage());
        assertEquals("error-500", errorPage.getErrorTemplate());
        verify(logService).logError(contains("500"), any(Exception.class));
    }

    @Test
    @DisplayName("Deve tratar erro 403 - Acesso negado")
    void shouldHandle403Error() {
        // Given
        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(403);
        when(request.getAttribute("javax.servlet.error.message")).thenReturn("Access Denied");

        // When
        errorPage.init();

        // Then
        assertEquals("Acesso negado", errorPage.getUserFriendlyMessage());
        assertEquals("error-403", errorPage.getErrorTemplate());
        verify(logService).logWarning(contains("403"));
    }

    @Test
    @DisplayName("Deve gerar ID único para o erro")
    void shouldGenerateUniqueErrorId() {
        // Given
        when(errorService.generateErrorId()).thenReturn("ERR-12345");

        // When
        errorPage.init();

        // Then
        assertEquals("ERR-12345", errorPage.getErrorId());
        verify(errorService).generateErrorId();
    }

    @Test
    @DisplayName("Deve navegar de volta para página anterior")
    void shouldNavigateBackToPreviousPage() {
        // Given
        when(request.getHeader("Referer")).thenReturn("/previous/page");

        // When
        String result = errorPage.goBack();

        // Then
        assertEquals("/previous/page", result);
    }

    @Test
    @DisplayName("Deve navegar para home quando não há página anterior")
    void shouldNavigateToHomeWhenNoPreviousPage() {
        // Given
        when(request.getHeader("Referer")).thenReturn(null);

        // When
        String result = errorPage.goBack();

        // Then
        assertEquals("/home", result);
    }

    @Test
    @DisplayName("Deve registrar erro no sistema")
    void shouldLogErrorInSystem() {
        // Given
        Exception exception = new RuntimeException("Test error");
        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(500);
        when(request.getAttribute("javax.servlet.error.exception")).thenReturn(exception);

        // When
        errorPage.init();

        // Then
        verify(errorService).saveError(any(ErrorInfo.class));
        verify(logService).logError(anyString(), eq(exception));
    }

    @Test
    @DisplayName("Deve reportar erro para administrador")
    void shouldReportErrorToAdmin() {
        // Given
        errorPage.setErrorInfo(mockErrorInfo);
        errorPage.setUserComment("Sistema travou ao salvar dados");

        // When
        String result = errorPage.reportError();

        // Then
        assertEquals("success", result);
        verify(errorService).reportToAdmin(eq(mockErrorInfo), eq("Sistema travou ao salvar dados"));
    }

    @Test
    @DisplayName("Deve validar se erro é crítico")
    void shouldValidateIfErrorIsCritical() {
        // Given
        mockErrorInfo.setStatusCode(500);
        errorPage.setErrorInfo(mockErrorInfo);

        // When
        boolean isCritical = errorPage.isCriticalError();

        // Then
        assertTrue(isCritical);
    }

    @Test
    @DisplayName("Deve validar se erro não é crítico")
    void shouldValidateIfErrorIsNotCritical() {
        // Given
        mockErrorInfo.setStatusCode(404);
        errorPage.setErrorInfo(mockErrorInfo);

        // When
        boolean isCritical = errorPage.isCriticalError();

        // Then
        assertFalse(isCritical);
    }

    @Test
    @DisplayName("Deve obter detalhes técnicos do erro para admin")
    void shouldGetTechnicalDetailsForAdmin() {
        // Given
        Exception exception = new RuntimeException("Database connection failed");
        mockErrorInfo.setException(exception);
        errorPage.setErrorInfo(mockErrorInfo);

        // When
        String details = errorPage.getTechnicalDetails();

        // Then
        assertNotNull(details);
        assertTrue(details.contains("Database connection failed"));
        assertTrue(details.contains("RuntimeException"));
    }
}

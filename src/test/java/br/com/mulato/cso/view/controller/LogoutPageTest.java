package br.com.mulato.cso.view.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes para validação da página logout.xhtml
 * Verifica a estrutura, conteúdo e funcionalidade da página de logout
 */
@DisplayName("Testes da Página logout.xhtml")
class LogoutPageTest {

    private static final String LOGOUT_PAGE_PATH = "src/main/webapp/logout.xhtml";

    @Test
    @DisplayName("Deve verificar se o arquivo logout.xhtml existe")
    void deveVerificarSeArquivoLogoutExiste() {
        // Given
        File logoutFile = new File(LOGOUT_PAGE_PATH);
        
        // Then
        assertTrue(logoutFile.exists(), "O arquivo logout.xhtml deve existir");
        assertTrue(logoutFile.isFile(), "logout.xhtml deve ser um arquivo");
        assertTrue(logoutFile.canRead(), "O arquivo logout.xhtml deve ser legível");
    }

    @Test
    @DisplayName("Deve conter namespace XHTML correto")
    void deveConterNamespaceXhtmlCorreto() throws IOException {
        // Given
        Path logoutPath = Paths.get(LOGOUT_PAGE_PATH);
        String conteudo = Files.readString(logoutPath);
        
        // Then
        assertTrue(conteudo.contains("https://www.w3.org/1999/xhtml"), 
                  "Deve conter namespace XHTML correto");
    }

    @Test
    @DisplayName("Deve conter namespaces JSF necessários")
    void deveConterNamespacesJsfNecessarios() throws IOException {
        // Given
        Path logoutPath = Paths.get(LOGOUT_PAGE_PATH);
        String conteudo = Files.readString(logoutPath);
        
        // Then
        assertTrue(conteudo.contains("jakarta.faces.html"), 
                  "Deve conter namespace h: para components HTML do JSF");
        assertTrue(conteudo.contains("jakarta.faces.core"), 
                  "Deve conter namespace f: para core do JSF");
        assertTrue(conteudo.contains("jakarta.faces.facelets"), 
                  "Deve conter namespace ui: para facelets");
    }

    @Test
    @DisplayName("Deve conter meta refresh para redirecionamento")
    void deveConterMetaRefreshParaRedirecionamento() throws IOException {
        // Given
        Path logoutPath = Paths.get(LOGOUT_PAGE_PATH);
        String conteudo = Files.readString(logoutPath);
        
        // Then
        assertTrue(conteudo.contains("meta http-equiv=\"Refresh\""), 
                  "Deve conter meta tag de refresh");
        assertTrue(conteudo.contains("URL=login.faces"), 
                  "Deve redirecionar para login.faces");
        assertTrue(conteudo.contains("content=\"0;"), 
                  "Deve ter redirecionamento imediato (0 segundos)");
    }

    @Test
    @DisplayName("Deve ter estrutura HTML válida")
    void deveTerEstruturaHtmlValida() throws IOException {
        // Given
        Path logoutPath = Paths.get(LOGOUT_PAGE_PATH);
        String conteudo = Files.readString(logoutPath);
        
        // Then
        assertTrue(conteudo.contains("<html"), "Deve conter tag html de abertura");
        assertTrue(conteudo.contains("</html>"), "Deve conter tag html de fechamento");
        assertTrue(conteudo.contains("<head>"), "Deve conter tag head de abertura");
        assertTrue(conteudo.contains("</head>"), "Deve conter tag head de fechamento");
        assertTrue(conteudo.contains("<body>"), "Deve conter tag body de abertura");
        assertTrue(conteudo.contains("</body>"), "Deve conter tag body de fechamento");
    }

    @Test
    @DisplayName("Deve ser arquivo pequeno e otimizado")
    void deveSerArquivePequenoEOtimizado() throws IOException {
        // Given
        Path logoutPath = Paths.get(LOGOUT_PAGE_PATH);
        long tamanhoArquivo = Files.size(logoutPath);
        
        // Then
        assertTrue(tamanhoArquivo < 1024, // Menos de 1KB
                  "Arquivo logout.xhtml deve ser pequeno e otimizado (menos de 1KB)");
        assertTrue(tamanhoArquivo > 0, 
                  "Arquivo logout.xhtml não deve estar vazio");
    }

    @Test
    @DisplayName("Deve ter conteúdo válido e bem formatado")
    void deveTerConteudoValidoEBemFormatado() throws IOException {
        // Given
        Path logoutPath = Paths.get(LOGOUT_PAGE_PATH);
        String conteudo = Files.readString(logoutPath);
        
        // Then
        assertFalse(conteudo.trim().isEmpty(), "Arquivo não deve estar vazio");
        assertTrue(conteudo.contains("login.faces"), 
                  "Deve referenciar página de login");
        
        // Verifica se não há caracteres especiais problemáticos
        assertFalse(conteudo.contains("<?xml"), 
                   "Não deve conter declaração XML desnecessária para XHTML");
    }

    @Test
    @DisplayName("Deve implementar logout automático via meta refresh")
    void deveImplementarLogoutAutomaticoViaMetaRefresh() throws IOException {
        // Given
        Path logoutPath = Paths.get(LOGOUT_PAGE_PATH);
        String conteudo = Files.readString(logoutPath);
        
        // Then
        // Verifica se implementa o padrão de logout via redirecionamento imediato
        assertTrue(conteudo.matches("(?s).*content=\"0;[^\"]*URL=login\\.faces\".*"), 
                  "Deve implementar redirecionamento imediato para login.faces");
        
        // Verifica se o body está vazio (sem conteúdo desnecessário)
        assertTrue(conteudo.matches("(?s).*<body>\\s*</body>.*"), 
                  "Body deve estar vazio para logout automático");
    }
}

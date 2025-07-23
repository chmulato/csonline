package br.com.mulato.cso.view.beans;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes para validação da integração do seletor de tema na página login.xhtml
 * Verifica se os componentes de seleção de tema estão corretamente configurados
 */
@DisplayName("Testes da Integração de Tema na Página login.xhtml")
class LoginThemeIntegrationTest {

    private static final String LOGIN_PAGE_PATH = "src/main/webapp/login.xhtml";

    @Test
    @DisplayName("Deve verificar se a página login.xhtml existe")
    void deveVerificarSeArquivoLoginExiste() {
        // Given
        File loginFile = new File(LOGIN_PAGE_PATH);
        
        // Then
        assertTrue(loginFile.exists(), "O arquivo login.xhtml deve existir");
        assertTrue(loginFile.isFile(), "login.xhtml deve ser um arquivo");
        assertTrue(loginFile.canRead(), "O arquivo login.xhtml deve ser legível");
    }

    @Test
    @DisplayName("Deve conter referência ao ThemeSwitcherBean")
    void deveConterReferenciaAoThemeSwitcherBean() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        assertTrue(conteudo.contains("themeSwitcherBean"), 
                  "Deve referenciar o themeSwitcherBean");
        assertTrue(conteudo.contains("#{themeSwitcherBean.theme}"), 
                  "Deve usar a propriedade theme do bean");
        assertTrue(conteudo.contains("#{themeSwitcherBean.themesAsSelectItems}"), 
                  "Deve usar themesAsSelectItems para popular o selectOneMenu");
    }

    @Test
    @DisplayName("Deve ter componente selectOneMenu para seleção de tema")
    void deveTerComponenteSelectOneMenuParaSelecaoTema() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        assertTrue(conteudo.contains("p:selectOneMenu"), 
                  "Deve conter componente p:selectOneMenu");
        assertTrue(conteudo.contains("id=\"theme\""), 
                  "SelectOneMenu deve ter id='theme'");
        assertTrue(conteudo.contains("value=\"#{themeSwitcherBean.theme}\""), 
                  "Deve estar vinculado ao valor do tema");
    }

    @Test
    @DisplayName("Deve ter f:selectItems para popular as opções de tema")
    void deveTerFSelectItemsParaPopularAsOpcoesDeTema() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        assertTrue(conteudo.contains("f:selectItems"), 
                  "Deve conter f:selectItems para as opções");
        assertTrue(conteudo.contains("value=\"#{themeSwitcherBean.themesAsSelectItems}\""), 
                  "f:selectItems deve usar themesAsSelectItems");
    }

    @Test
    @DisplayName("Deve ter configuração Ajax para mudança de tema")
    void deveTerConfiguracaoAjaxParaMudancaDeTema() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        assertTrue(conteudo.contains("p:ajax"), 
                  "Deve conter configuração p:ajax");
        assertTrue(conteudo.contains("event=\"change\""), 
                  "Ajax deve escutar o evento 'change'");
        assertTrue(conteudo.contains("listener=\"#{themeSwitcherBean.onThemeChange}\""), 
                  "Deve chamar o método onThemeChange do bean");
    }

    @Test
    @DisplayName("Deve ter tooltip explicativo para seleção de tema")
    void deveTerTooltipExplicativoParaSelecaoDeTema() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        assertTrue(conteudo.contains("p:tooltip"), 
                  "Deve conter tooltip explicativo");
        assertTrue(conteudo.contains("for=\"theme\""), 
                  "Tooltip deve estar associado ao componente theme");
        assertTrue(conteudo.contains("#{rotulo.escolherTheme}"), 
                  "Deve usar rótulo internacionalizado para o tooltip");
    }

    @Test
    @DisplayName("Deve ter label internacionalizado para seleção de tema")
    void deveTerLabelInternacionalizadoParaSelecaoDeTema() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        assertTrue(conteudo.contains("#{rotulo.theme}"), 
                  "Deve usar rótulo internacionalizado para 'theme'");
        assertTrue(conteudo.contains("label=\"#{rotulo.theme}\""), 
                  "SelectOneMenu deve ter label internacionalizado");
    }

    @Test
    @DisplayName("Deve estar posicionado corretamente na estrutura da página")
    void deveEstarPosicionadoCorretamenteNaEstruturaDaPagina() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        // Verifica se está dentro do panelGrid principal
        assertTrue(conteudo.contains("h:panelGrid") && conteudo.contains("p:selectOneMenu"), 
                  "Seletor de tema deve estar dentro do layout principal");
        
        // Verifica se está posicionado após os campos de login
        int posicaoLogin = conteudo.indexOf("p:commandButton");
        int posicaoTheme = conteudo.indexOf("p:selectOneMenu");
        assertTrue(posicaoTheme > posicaoLogin, 
                  "Seletor de tema deve aparecer após os botões de login");
    }

    @Test
    @DisplayName("Deve ter namespace PrimeFaces correto")
    void deveTerNamespacePrimeFacesCorreto() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        assertTrue(conteudo.contains("xmlns:p=\"http://primefaces.org/ui\""), 
                  "Deve ter namespace PrimeFaces correto");
    }

    @Test
    @DisplayName("Deve ter configuração Ajax otimizada")
    void deveTerConfiguracaoAjaxOtimizada() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        assertTrue(conteudo.contains("process=\"@this\""), 
                  "Ajax deve processar apenas o próprio componente");
        assertTrue(conteudo.contains("update=\"@none\""), 
                  "Ajax não deve atualizar componentes desnecessariamente");
    }

    @Test
    @DisplayName("Deve validar estrutura completa da seleção de tema")
    void deveValidarEstruturaCompletaDaSelecaoDeTema() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then - Verifica a sequência completa dos componentes
        assertTrue(conteudo.matches("(?s).*<h:outputText value=\"#\\{rotulo\\.theme\\}:\".*"), 
                  "Deve ter label para o campo");
        assertTrue(conteudo.matches("(?s).*<p:selectOneMenu id=\"theme\".*"), 
                  "Deve ter selectOneMenu com id");
        assertTrue(conteudo.matches("(?s).*<f:selectItems value=\"#\\{themeSwitcherBean\\.themesAsSelectItems\\}\".*"), 
                  "Deve ter f:selectItems");
        assertTrue(conteudo.matches("(?s).*<p:ajax event=\"change\".*"), 
                  "Deve ter configuração Ajax");
        assertTrue(conteudo.matches("(?s).*<p:tooltip for=\"theme\".*"), 
                  "Deve ter tooltip associado");
    }

    @Test
    @DisplayName("Deve ter efeitos visuais configurados no tooltip")
    void deveTerEfeitosVisuaisConfiguradosNoTooltip() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        assertTrue(conteudo.contains("showEffect=\"fade\""), 
                  "Tooltip deve ter efeito de entrada 'fade'");
        assertTrue(conteudo.contains("hideEffect=\"fade\""), 
                  "Tooltip deve ter efeito de saída 'fade'");
    }

    @Test
    @DisplayName("Deve usar espaçamento adequado na interface")
    void deveUsarEspacamentoAdequadoNaInterface() throws IOException {
        // Given
        Path loginPath = Paths.get(LOGIN_PAGE_PATH);
        String conteudo = Files.readString(loginPath);
        
        // Then
        assertTrue(conteudo.contains("p:spacer"), 
                  "Deve usar p:spacer para espaçamento");
        assertTrue(conteudo.contains("height=\"20px\""), 
                  "Deve ter espaçamento consistente de 20px");
    }
}

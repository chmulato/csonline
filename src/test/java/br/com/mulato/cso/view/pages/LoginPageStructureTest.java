package br.com.mulato.cso.view.pages;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@DisplayName("Testes de Estrutura da Página login.xhtml")
class LoginPageStructureTest {

    private static final String LOGIN_PAGE_PATH = "src/main/webapp/login.xhtml";

    @Test
    @DisplayName("Deve conter todos os elementos obrigatórios da página de login")
    void deveConterTodosElementosObrigatoriosDaPaginaDeLogin() throws IOException {
        // Given
        Path loginPagePath = Paths.get(LOGIN_PAGE_PATH);
        assertTrue(Files.exists(loginPagePath), "Arquivo login.xhtml deve existir");
        
        String content = Files.readString(loginPagePath);
        
        // Then - verificar elementos essenciais
        assertTrue(content.contains("xmlns:h=\"jakarta.faces.html\""), "Deve conter namespace JSF HTML");
        assertTrue(content.contains("xmlns:f=\"jakarta.faces.core\""), "Deve conter namespace JSF Core");
        assertTrue(content.contains("xmlns:p=\"http://primefaces.org/ui\""), "Deve conter namespace PrimeFaces");
        
        // Verificar componentes principais
        assertTrue(content.contains("#{loginMB.username}"), "Deve ter binding para username");
        assertTrue(content.contains("#{loginMB.password}"), "Deve ter binding para password");
        assertTrue(content.contains("#{loginMB.login}"), "Deve ter ação de login");
        assertTrue(content.contains("#{loginMB.reset}"), "Deve ter ação de reset");
        
        // Verificar componentes PrimeFaces
        assertTrue(content.contains("p:inputText"), "Deve ter campo de texto PrimeFaces");
        assertTrue(content.contains("p:password"), "Deve ter campo de senha PrimeFaces");
        assertTrue(content.contains("p:commandButton"), "Deve ter botões PrimeFaces");
        assertTrue(content.contains("p:selectOneMenu"), "Deve ter seletor de tema");
        
        // Verificar configurações de tema
        assertTrue(content.contains("#{themeSwitcherBean.theme}"), "Deve ter binding para tema");
        assertTrue(content.contains("#{themeSwitcherBean.themesAsSelectItems}"), "Deve ter itens de tema");
        
        // Verificar internacionalização
        assertTrue(content.contains("basename=\"Rotulos\""), "Deve carregar bundle de recursos");
        assertTrue(content.contains("locale=\"pt_BR\""), "Deve estar configurado para português brasileiro");
    }

    @Test
    @DisplayName("Deve ter validações de formulário configuradas")
    void deveTerValidacoesDeFormularioConfiguradas() throws IOException {
        // Given
        String content = Files.readString(Paths.get(LOGIN_PAGE_PATH));
        
        // Then
        assertTrue(content.contains("maxlength=\"15\""), "Campo senha deve ter limite de 15 caracteres");
        assertTrue(content.contains("feedback=\"false\""), "Feedback de senha deve estar desabilitado");
        assertTrue(content.contains("ajax=\"false\""), "Login deve não usar AJAX");
        assertTrue(content.contains("update=\"panelHeader\""), "Reset deve atualizar o painel");
    }

    @Test
    @DisplayName("Deve ter todos os recursos necessários referenciados")
    void deveTerTodosRecursosNecessariosReferenciados() throws IOException {
        // Given
        String content = Files.readString(Paths.get(LOGIN_PAGE_PATH));
        
        // Then
        assertTrue(content.contains("#{resource['images:favicon.ico']}"), "Deve referenciar favicon");
        assertTrue(content.contains("library=\"images\" name=\"logo.gif\""), "Deve referenciar logo");
        assertTrue(content.contains("#{rotulo.aplicacao}"), "Deve usar labels internacionalizados");
    }
}
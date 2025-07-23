package br.com.mulato.cso.view.pages;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para a página index.jsp
 * Valida a detecção de navegadores e redirecionamento para login
 */
@DisplayName("Testes da Página index.jsp - Detecção de Navegadores")
class IndexPageTest {

    private static final String INDEX_PAGE_PATH = "src/main/webapp/index.jsp";

    @Test
    @DisplayName("Deve verificar se o arquivo index.jsp existe")
    void deveVerificarSeArquivoIndexExiste() {
        // Given
        File indexFile = new File(INDEX_PAGE_PATH);
        
        // Then
        assertTrue(indexFile.exists(), "O arquivo index.jsp deve existir");
        assertTrue(indexFile.isFile(), "index.jsp deve ser um arquivo");
        assertTrue(indexFile.canRead(), "O arquivo index.jsp deve ser legível");
    }

    @Test
    @DisplayName("Deve conter função getBrowserInfo para detecção de navegadores")
    void deveConterFuncaoGetBrowserInfo() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("public String getBrowserInfo(String Information)"), 
                  "Deve conter função getBrowserInfo");
        assertTrue(conteudo.contains("String browsername = \"\";"), 
                  "Deve inicializar variável browsername");
        assertTrue(conteudo.contains("String browserversion = \"\";"), 
                  "Deve inicializar variável browserversion");
    }

    @Test
    @DisplayName("Deve detectar Internet Explorer")
    void deveDetectarInternetExplorer() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("if (browser.contains(\"MSIE\"))"), 
                  "Deve verificar se contém MSIE");
        assertTrue(conteudo.contains("browsername = \"Internet Explorer\";"), 
                  "Deve definir nome como Internet Explorer");
        assertTrue(conteudo.contains("browserversion = Info[1];"), 
                  "Deve extrair versão do IE");
    }

    @Test
    @DisplayName("Deve detectar Firefox")
    void deveDetectarFirefox() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("else if (browser.contains(\"Firefox\"))"), 
                  "Deve verificar se contém Firefox");
        assertTrue(conteudo.contains("browser.substring(browser.indexOf(\"Firefox\"))"), 
                  "Deve extrair substring do Firefox");
    }

    @Test
    @DisplayName("Deve detectar Chrome")
    void deveDetectarChrome() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("else if (browser.contains(\"Chrome\"))"), 
                  "Deve verificar se contém Chrome");
        assertTrue(conteudo.contains("browser.substring(browser.indexOf(\"Chrome\"))"), 
                  "Deve extrair substring do Chrome");
    }

    @Test
    @DisplayName("Deve detectar Opera")
    void deveDetectarOpera() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("else if (browser.contains(\"Opera\"))"), 
                  "Deve verificar se contém Opera");
        assertTrue(conteudo.contains("browser.substring(browser.indexOf(\"Opera\"))"), 
                  "Deve extrair substring do Opera");
    }

    @Test
    @DisplayName("Deve detectar Safari")
    void deveDetectarSafari() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("else if (browser.contains(\"Safari\"))"), 
                  "Deve verificar se contém Safari");
        assertTrue(conteudo.contains("browser.substring(browser.indexOf(\"Safari\"))"), 
                  "Deve extrair substring do Safari");
    }

    @Test
    @DisplayName("Deve conter função checkVersionIE para validar versões do IE")
    void deveConterFuncaoCheckVersionIE() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("public boolean checkVersionIE(String Information)"), 
                  "Deve conter função checkVersionIE");
        assertTrue(conteudo.contains("if ((Info[1].equals(\"7.0\")) || (Info[1].equals(\"8.0\")) || (Info[1].equals(\"9.0\")))"), 
                  "Deve verificar versões 7.0, 8.0 e 9.0 do IE");
    }

    @Test
    @DisplayName("Deve extrair User-Agent do request")
    void deveExtrairUserAgentDoRequest() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("String ua = request.getHeader( \"User-Agent\" );"), 
                  "Deve extrair User-Agent do request");
        assertTrue(conteudo.contains("response.setHeader( \"Vary\", \"User-Agent\" );"), 
                  "Deve definir header Vary para User-Agent");
    }

    @Test
    @DisplayName("Deve criar variáveis booleanas para cada navegador")
    void deveCriarVariaveisBooleanasParaCadaNavegador() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("boolean isFirefox = ( ua != null && ua.indexOf( \"Firefox/\" ) != -1 );"), 
                  "Deve criar variável isFirefox");
        assertTrue(conteudo.contains("boolean isMSIE = ( ua != null && ua.indexOf( \"MSIE\" ) != -1 && checkVersionIE(ua));"), 
                  "Deve criar variável isMSIE com verificação de versão");
        assertTrue(conteudo.contains("boolean isChrome = ( ua != null && ua.indexOf( \"Chrome/\" ) != -1 );"), 
                  "Deve criar variável isChrome");
        assertTrue(conteudo.contains("boolean isSafari = ( ua != null && ua.indexOf( \"Safari/\" ) != -1 );"), 
                  "Deve criar variável isSafari");
        assertTrue(conteudo.contains("boolean isOpera = ( ua != null && ua.indexOf( \"Opera/\" ) != -1 );"), 
                  "Deve criar variável isOpera");
    }

    @Test
    @DisplayName("Deve redirecionar navegadores suportados para login.faces")
    void deveRedirecionarNavegadoresSuportadosParaLogin() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("<% if ( isFirefox ) { %>"), 
                  "Deve verificar se é Firefox");
        assertTrue(conteudo.contains("<jsp:forward page=\"/login.faces\" />"), 
                  "Deve redirecionar para login.faces");
        
        // Verifica se todos os navegadores suportados redirecionam
        String[] navegadores = {"isFirefox", "isMSIE", "isChrome", "isSafari", "isOpera"};
        for (String navegador : navegadores) {
            assertTrue(conteudo.contains("<% } else if ( " + navegador + " ) { %>") || 
                      conteudo.contains("<% if ( " + navegador + " ) { %>"), 
                      "Deve verificar navegador " + navegador);
        }
    }

    @Test
    @DisplayName("Deve exibir mensagem de incompatibilidade para navegadores não suportados")
    void deveExibirMensagemDeIncompatibilidade() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("Este sistema n&atilde;o &eacute;") && 
                  conteudo.contains("compat&iacute;vel com este navegador."), 
                  "Deve exibir mensagem de incompatibilidade");
        assertTrue(conteudo.contains("Utilize:<br> - Internet Explorer 8.0 ou 9.0,<br> -") && 
                  conteudo.contains("Firefox,<br> - Chrome,<br> - Opera, ou<br> - Safari."), 
                  "Deve listar navegadores suportados");
    }

    @Test
    @DisplayName("Deve exibir informações do navegador atual")
    void deveExibirInformacoesDoNavegadorAtual() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("Seu Navegador Web &eacute; o:"), 
                  "Deve exibir texto informativo sobre o navegador");
        assertTrue(conteudo.contains("out.println(getBrowserInfo(request.getHeader(\"User-Agent\")));"), 
                  "Deve chamar getBrowserInfo para exibir informações do navegador");
    }

    @Test
    @DisplayName("Deve conter imagens dos navegadores suportados")
    void deveConterImagensDosNavegadoresSuportados() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("images/firefox.jpg"), 
                  "Deve conter imagem do Firefox");
        assertTrue(conteudo.contains("images/ie.jpg"), 
                  "Deve conter imagem do Internet Explorer");
        assertTrue(conteudo.contains("images/chrome.jpg"), 
                  "Deve conter imagem do Chrome");
        assertTrue(conteudo.contains("images/opera.jpg"), 
                  "Deve conter imagem do Opera");
        assertTrue(conteudo.contains("images/safari.jpg"), 
                  "Deve conter imagem do Safari");
    }

    @Test
    @DisplayName("Deve ter estrutura HTML válida")
    void deveTerEstruturaHtmlValida() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 TRANSITIONAL//EN\">"), 
                  "Deve ter DOCTYPE HTML 4.0 Transitional");
        assertTrue(conteudo.contains("<html>"), 
                  "Deve conter tag html de abertura");
        assertTrue(conteudo.contains("</html>"), 
                  "Deve conter tag html de fechamento");
        assertTrue(conteudo.contains("<head>"), 
                  "Deve conter tag head");
        assertTrue(conteudo.contains("<body>"), 
                  "Deve conter tag body");
        assertTrue(conteudo.contains("<title>CSOnline</title>"), 
                  "Deve ter título CSOnline");
    }

    @Test
    @DisplayName("Deve ter meta tags corretas")
    void deveTerMetaTagsCorretas() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"), 
                  "Deve ter meta tag para Content-Type UTF-8");
    }

    @Test
    @DisplayName("Deve referenciar arquivo CSS de estilo")
    void deveReferenciarArquivoCssDeEstilo() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("<link type=\"text/css\" rel=\"stylesheet\" href=\"/resources/style.css\" />"), 
                  "Deve referenciar arquivo style.css");
    }

    @Test
    @DisplayName("Deve usar encoding correto para caracteres especiais")
    void deveUsarEncodingCorretoParaCaracteresEspeciais() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("&eacute;"), 
                  "Deve usar entidade HTML para caracteres acentuados");
        assertTrue(conteudo.contains("&atilde;"), 
                  "Deve usar entidade HTML para til");
        assertTrue(conteudo.contains("&iacute;"), 
                  "Deve usar entidade HTML para i acentuado");
    }

    @Test
    @DisplayName("Deve ter layout de tabela para navegadores não suportados")
    void deveTerLayoutDeTabelaParaNavegadoresNaoSuportados() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("<table width=\"400px\" border=\"0\" align=\"left\">"), 
                  "Deve ter tabela para layout");
        assertTrue(conteudo.contains("<td colspan=\"5\">"), 
                  "Deve ter célula com colspan para mensagem");
        assertTrue(conteudo.contains("class=\"fontes\""), 
                  "Deve usar classe CSS fontes");
    }

    @Test
    @DisplayName("Deve validar lógica de detecção por User-Agent strings típicos")
    void deveValidarLogicaDeDeteccaoPorUserAgentStringsEcenicos() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then - Verifica se contém as strings de detecção corretas
        assertTrue(conteudo.contains("ua.indexOf( \"Firefox/\" )"), 
                  "Deve buscar 'Firefox/' no User-Agent");
        assertTrue(conteudo.contains("ua.indexOf( \"MSIE\" )"), 
                  "Deve buscar 'MSIE' no User-Agent para IE");
        assertTrue(conteudo.contains("ua.indexOf( \"Chrome/\" )"), 
                  "Deve buscar 'Chrome/' no User-Agent");
        assertTrue(conteudo.contains("ua.indexOf( \"Safari/\" )"), 
                  "Deve buscar 'Safari/' no User-Agent");
        assertTrue(conteudo.contains("ua.indexOf( \"Opera/\" )"), 
                  "Deve buscar 'Opera/' no User-Agent");
    }

    @Test
    @DisplayName("Deve usar JSP scriptlets corretamente")
    void deveUsarJspScriptletsCorretamente() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        // Verifica declaração de métodos
        assertTrue(conteudo.contains("<%!"), 
                  "Deve usar <%! para declarações de métodos");
        
        // Verifica scriptlets de código
        assertTrue(conteudo.contains("<%"), 
                  "Deve usar <% para código Java");
        
        // Verifica expressões
        assertTrue(conteudo.contains("out.println("), 
                  "Deve usar out.println para output");
        
        // Verifica fechamento correto
        assertTrue(conteudo.contains("%>"), 
                  "Deve fechar scriptlets com %>");
    }

    @Test
    @DisplayName("Deve ter tratamento para User-Agent nulo")
    void deveTerTratamentoParaUserAgentNulo() throws IOException {
        // Given
        Path indexPath = Paths.get(INDEX_PAGE_PATH);
        String conteudo = Files.readString(indexPath);
        
        // Then
        assertTrue(conteudo.contains("ua != null &&"), 
                  "Deve verificar se User-Agent não é nulo antes de usar");
        
        // Verifica se todas as verificações de navegador incluem null check
        String[] checks = {
            "( ua != null && ua.indexOf( \"Firefox/\" ) != -1 )",
            "( ua != null && ua.indexOf( \"MSIE\" ) != -1",
            "( ua != null && ua.indexOf( \"Chrome/\" ) != -1 )",
            "( ua != null && ua.indexOf( \"Safari/\" ) != -1 )",
            "( ua != null && ua.indexOf( \"Opera/\" ) != -1 )"
        };
        
        for (String check : checks) {
            assertTrue(conteudo.contains(check), 
                      "Deve incluir null check para: " + check);
        }
    }
}

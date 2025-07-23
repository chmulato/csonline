package test.java.br.com.mulato.cso.view.pages;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para a página error.xhtml
 * Valida a estrutura, componentes e funcionalidades da página de tratamento de erros
 * 
 * @author Test Suite
 */
@DisplayName("Testes da Página de Erro (error.xhtml)")
public class ErrorPageTest {

    private static String errorPageContent;
    private static final String ERROR_XHTML_PATH = "src/main/webapp/error.xhtml";

    @BeforeAll
    static void setUp() throws IOException {
        Path errorPath = Paths.get(ERROR_XHTML_PATH);
        assertTrue(Files.exists(errorPath), "Arquivo error.xhtml deve existir");
        errorPageContent = Files.readString(errorPath);
    }

    @Test
    @DisplayName("Deve validar declaração XML e encoding")
    void deveValidarDeclaracaoXmlEEncoding() {
        assertTrue(errorPageContent.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"),
                "Página deve iniciar com declaração XML UTF-8");
    }

    @Test
    @DisplayName("Deve conter todos os namespaces JSF e PrimeFaces necessários")
    void deveConterTodosNamespacesNecessarios() {
        assertTrue(errorPageContent.contains("xmlns=\"https://www.w3.org/1999/xhtml\""),
                "Deve conter namespace XHTML padrão");
        assertTrue(errorPageContent.contains("xmlns:h=\"jakarta.faces.html\""),
                "Deve conter namespace JSF HTML");
        assertTrue(errorPageContent.contains("xmlns:f=\"jakarta.faces.core\""),
                "Deve conter namespace JSF Core");
        assertTrue(errorPageContent.contains("xmlns:ui=\"jakarta.faces.facelets\""),
                "Deve conter namespace JSF Facelets");
        assertTrue(errorPageContent.contains("xmlns:p=\"http://primefaces.org/ui\""),
                "Deve conter namespace PrimeFaces");
    }

    @Test
    @DisplayName("Deve usar template body.xhtml")
    void deveUsarTemplateBodyXhtml() {
        assertTrue(errorPageContent.contains("template=\"body.xhtml\""),
                "Deve usar template body.xhtml para consistência visual");
    }

    @Test
    @DisplayName("Deve ter composição ui:composition corretamente estruturada")
    void deveTerComposicaoUiCompositionCorretamenteEstruturada() {
        assertTrue(errorPageContent.contains("<ui:composition"),
                "Deve ter tag ui:composition");
        assertTrue(errorPageContent.contains("</ui:composition>"),
                "Deve ter fechamento da tag ui:composition");
    }

    @Test
    @DisplayName("Deve definir seção body com ui:define")
    void deveDefinirSecaoBodyComUiDefine() {
        assertTrue(errorPageContent.contains("<ui:define name=\"body\">"),
                "Deve definir seção body");
        assertTrue(errorPageContent.contains("</ui:define>"),
                "Deve fechar seção ui:define");
    }

    @Test
    @DisplayName("Deve ter painel principal PrimeFaces com header dinâmico")
    void deveTerPainelPrincipalPrimeFacesComHeaderDinamico() {
        assertTrue(errorPageContent.contains("<p:panel"),
                "Deve conter painel PrimeFaces");
        assertTrue(errorPageContent.contains("header=\"#{rotulo.mensagem}\""),
                "Header do painel deve usar mensagem internacionalizada");
        assertTrue(errorPageContent.contains("</p:panel>"),
                "Deve fechar painel PrimeFaces");
    }

    @Test
    @DisplayName("Deve ter estilo CSS para centralização da página de erro")
    void deveTerEstiloCssParaCentralizacaoDaPaginaDeErro() {
        assertTrue(errorPageContent.contains("style=\"margin-left: 35%; margin-right: 40%; margin-top: 10%\""),
                "Deve ter estilo CSS para centralizar painel de erro na tela");
    }

    @Test
    @DisplayName("Deve conter formulário JSF com ID específico")
    void deveConterFormularioJsfComIdEspecifico() {
        assertTrue(errorPageContent.contains("<h:form id=\"form\">"),
                "Deve ter formulário JSF com ID 'form'");
        assertTrue(errorPageContent.contains("</h:form>"),
                "Deve fechar formulário JSF");
    }

    @Test
    @DisplayName("Deve ter estrutura de grid com 3 colunas")
    void deveTerEstruturaDeGridCom3Colunas() {
        assertTrue(errorPageContent.contains("<h:panelGrid columns=\"3\">"),
                "Deve ter panelGrid com 3 colunas para layout");
        assertTrue(errorPageContent.contains("</h:panelGrid>"),
                "Deve fechar panelGrid");
    }

    @Test
    @DisplayName("Deve usar spacers PrimeFaces para layout")
    void deveUsarSpacersPrimeFacesParaLayout() {
        int spacerCount = errorPageContent.split("<p:spacer").length - 1;
        assertTrue(spacerCount >= 6, 
                "Deve ter pelo menos 6 spacers para estruturação do layout");
        
        assertTrue(errorPageContent.contains("<p:spacer height=\"50px\" />"),
                "Deve ter spacer com altura específica para espaçamento vertical");
    }

    @Test
    @DisplayName("Deve exibir mensagem de erro internacionalizada")
    void deveExibirMensagemDeErroInternacionalizada() {
        assertTrue(errorPageContent.contains("<h:outputLabel value=\"#{rotulo.mensagem_06}\" />"),
                "Deve exibir mensagem de erro usando bundle de internacionalização");
    }

    @Test
    @DisplayName("Deve ter botão de retorno para login")
    void deveTerBotaoDeRetornoParaLogin() {
        assertTrue(errorPageContent.contains("<h:commandButton"),
                "Deve ter botão de comando JSF");
        assertTrue(errorPageContent.contains("value=\"#{rotulo.mensagem_03}\""),
                "Texto do botão deve usar mensagem internacionalizada");
        assertTrue(errorPageContent.contains("action=\"#{loginMB.logar}\""),
                "Botão deve chamar método de login do managed bean");
        assertTrue(errorPageContent.contains("styleClass=\"default\""),
                "Botão deve usar classe CSS 'default'");
    }

    @Test
    @DisplayName("Deve ter estrutura bem formada sem elementos órfãos")
    void deveTerEstruturaBemFormadaSemElementosOrfaos() {
        // Verificar que todas as tags estão propriamente fechadas
        int openPanelCount = errorPageContent.split("<p:panel").length - 1;
        int closePanelCount = errorPageContent.split("</p:panel>").length - 1;
        assertEquals(openPanelCount, closePanelCount, 
                "Número de tags p:panel abertas deve igualar as fechadas");

        int openFormCount = errorPageContent.split("<h:form").length - 1;
        int closeFormCount = errorPageContent.split("</h:form>").length - 1;
        assertEquals(openFormCount, closeFormCount, 
                "Número de tags h:form abertas deve igualar as fechadas");

        int openGridCount = errorPageContent.split("<h:panelGrid").length - 1;
        int closeGridCount = errorPageContent.split("</h:panelGrid>").length - 1;
        assertEquals(openGridCount, closeGridCount, 
                "Número de tags h:panelGrid abertas deve igualar as fechadas");
    }

    @Test
    @DisplayName("Deve usar expressões EL corretas para binding")
    void deveUsarExpressoesElCorretasParaBinding() {
        assertTrue(errorPageContent.contains("#{rotulo.mensagem}"),
                "Deve usar EL para título da mensagem");
        assertTrue(errorPageContent.contains("#{rotulo.mensagem_06}"),
                "Deve usar EL para mensagem de erro específica");
        assertTrue(errorPageContent.contains("#{rotulo.mensagem_03}"),
                "Deve usar EL para texto do botão");
        assertTrue(errorPageContent.contains("#{loginMB.logar}"),
                "Deve usar EL para action do botão de retorno");
    }

    @Test
    @DisplayName("Deve ter componentes PrimeFaces para experiência rica")
    void deveTerComponentesPrimeFacesParaExperienciaRica() {
        assertTrue(errorPageContent.contains("p:panel"),
                "Deve usar painel PrimeFaces para container principal");
        assertTrue(errorPageContent.contains("p:spacer"),
                "Deve usar spacers PrimeFaces para layout");
    }

    @Test
    @DisplayName("Deve ter layout responsivo com margens adequadas")
    void deveTerLayoutResponsivoComMargensAdequadas() {
        String style = "margin-left: 35%; margin-right: 40%; margin-top: 10%";
        assertTrue(errorPageContent.contains(style),
                "Deve ter margens CSS para centralização responsiva");
    }

    @Test
    @DisplayName("Deve integrar com sistema de internacionalização")
    void deveIntegrarComSistemaDeInternacionalizacao() {
        // Verificar se usa o bundle 'rotulo' corretamente
        assertTrue(errorPageContent.contains("#{rotulo."),
                "Deve integrar com bundle de internacionalização 'rotulo'");
        
        // Verificar múltiplas chaves de mensagem
        int messageCount = 0;
        if (errorPageContent.contains("#{rotulo.mensagem}")) messageCount++;
        if (errorPageContent.contains("#{rotulo.mensagem_06}")) messageCount++;
        if (errorPageContent.contains("#{rotulo.mensagem_03}")) messageCount++;
        
        assertTrue(messageCount >= 3, 
                "Deve usar pelo menos 3 chaves diferentes do bundle de mensagens");
    }

    @Test
    @DisplayName("Deve ter estrutura adequada para tratamento de erros")
    void deveTerEstruturaAdequadaParaTratamentoDeErros() {
        // Verificar que é uma página dedicada para erros
        assertTrue(errorPageContent.contains("rotulo.mensagem_06"),
                "Deve usar chave específica para mensagem de erro");
        
        // Verificar que oferece retorno ao sistema
        assertTrue(errorPageContent.contains("loginMB.logar"),
                "Deve oferecer retorno ao login após erro");
        
        // Verificar layout centrado apropriado para notificação de erro
        assertTrue(errorPageContent.contains("margin-top: 10%"),
                "Deve ter posicionamento vertical adequado para página de erro");
    }

    @Test
    @DisplayName("Deve ter componentização adequada com separação de responsabilidades")
    void deveTerComponentizacaoAdequadaComSeparacaoDeResponsabilidades() {
        // Verificar uso do template
        assertTrue(errorPageContent.contains("template=\"body.xhtml\""),
                "Deve usar template para reutilização de layout");
        
        // Verificar definição específica de conteúdo
        assertTrue(errorPageContent.contains("ui:define name=\"body\""),
                "Deve definir apenas o conteúdo específico da página");
        
        // Verificar integração com managed bean específico
        assertTrue(errorPageContent.contains("loginMB"),
                "Deve integrar com managed bean apropriado");
    }

    @Test
    @DisplayName("Deve ter sintaxe XML válida")
    void deveTerSintaxeXmlValida() {
        // Verificar tags auto-fechadas
        assertTrue(errorPageContent.contains("<p:spacer />") || errorPageContent.contains("<p:spacer/>"),
                "Tags vazias devem ser auto-fechadas");
        
        // Verificar aspas em atributos
        assertFalse(errorPageContent.contains("='"),
                "Atributos devem usar aspas duplas, não simples");
        
        // Verificar estrutura básica XML
        assertTrue(errorPageContent.trim().endsWith("</ui:composition>"),
                "Documento deve terminar com fechamento da composição");
    }
}

package br.com.mulato.cso.view.pages;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testes de integração para a lógica de detecção de navegadores do index.jsp
 * Simula User-Agent strings reais para validar a lógica de redirecionamento
 */
@DisplayName("Testes de Detecção de Navegadores - index.jsp")
class IndexBrowserDetectionTest {

    /**
     * Simula a lógica de detecção do Firefox baseada no código do index.jsp
     */
    private boolean isFirefoxDetected(String userAgent) {
        return (userAgent != null && userAgent.indexOf("Firefox/") != -1);
    }

    /**
     * Simula a lógica de detecção do Chrome baseada no código do index.jsp
     */
    private boolean isChromeDetected(String userAgent) {
        return (userAgent != null && userAgent.indexOf("Chrome/") != -1);
    }

    /**
     * Simula a lógica de detecção do Safari baseada no código do index.jsp
     */
    private boolean isSafariDetected(String userAgent) {
        return (userAgent != null && userAgent.indexOf("Safari/") != -1);
    }

    /**
     * Simula a lógica de detecção do Opera baseada no código do index.jsp
     */
    private boolean isOperaDetected(String userAgent) {
        return (userAgent != null && userAgent.indexOf("Opera/") != -1);
    }

    /**
     * Simula a lógica de detecção do IE baseada no código do index.jsp
     */
    private boolean isMSIEDetected(String userAgent) {
        return (userAgent != null && userAgent.indexOf("MSIE") != -1 && checkVersionIE(userAgent));
    }

    /**
     * Simula a função getBrowserInfo do index.jsp
     */
    private String getBrowserInfo(String userAgent) {
        String browsername = "";
        String browserversion = "";
        String browser = userAgent;
        
        if (browser.contains("MSIE")) {
            String subsString = browser.substring(browser.indexOf("MSIE"));
            String[] Info = (subsString.split(";")[0]).split(" ");
            browsername = "Internet Explorer";
            browserversion = Info[1];
        } else if (browser.contains("Firefox")) {
            String subsString = browser.substring(browser.indexOf("Firefox"));
            String[] Info = (subsString.split(" ")[0]).split("/");
            browsername = Info[0];
            browserversion = Info[1];
        } else if (browser.contains("Chrome")) {
            String subsString = browser.substring(browser.indexOf("Chrome"));
            String[] Info = (subsString.split(" ")[0]).split("/");
            browsername = Info[0];
            browserversion = Info[1];
        } else if (browser.contains("Opera")) {
            String subsString = browser.substring(browser.indexOf("Opera"));
            String[] Info = (subsString.split(" ")[0]).split("/");
            browsername = Info[0];
            browserversion = Info[1];
        } else if (browser.contains("Safari")) {
            String subsString = browser.substring(browser.indexOf("Safari"));
            String[] Info = (subsString.split(" ")[0]).split("/");
            browsername = Info[0];
            browserversion = Info[1];
        }
        return browsername + "-" + browserversion;
    }

    /**
     * Simula a função checkVersionIE do index.jsp
     */
    private boolean checkVersionIE(String userAgent) {
        boolean isOK = false;
        String[] Info = this.getBrowserInfo(userAgent).split("-");
        if (Info.length > 1 && ((Info[1].equals("7.0")) || (Info[1].equals("8.0")) || (Info[1].equals("9.0")))) {
            isOK = true;
        }
        return isOK;
    }

    @Test
    @DisplayName("Deve detectar Firefox corretamente")
    void deveDetectarFirefoxCorretamente() {
        // Given - User-Agent strings reais do Firefox
        String firefoxUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0";
        
        // When
        boolean isFirefox = isFirefoxDetected(firefoxUserAgent);
        
        // Then
        assertTrue(isFirefox, "Deve detectar Firefox pelo User-Agent");
    }

    @Test
    @DisplayName("Deve detectar Chrome corretamente")
    void deveDetectarChromeCorretamente() {
        // Given - User-Agent strings reais do Chrome
        String chromeUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
        
        // When
        boolean isChrome = isChromeDetected(chromeUserAgent);
        
        // Then
        assertTrue(isChrome, "Deve detectar Chrome pelo User-Agent");
    }

    @Test
    @DisplayName("Deve detectar Safari corretamente")
    void deveDetectarSafariCorretamente() {
        // Given - User-Agent string real do Safari
        String safariUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15";
        
        // When
        boolean isSafari = isSafariDetected(safariUserAgent);
        
        // Then
        assertTrue(isSafari, "Deve detectar Safari pelo User-Agent");
    }

    @Test
    @DisplayName("Deve detectar Opera corretamente")
    void deveDetectarOperaCorretamente() {
        // Given - User-Agent string real do Opera (versão antiga que usa Opera/)
        String operaUserAgent = "Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.18";
        
        // When
        boolean isOpera = isOperaDetected(operaUserAgent);
        
        // Then
        assertTrue(isOpera, "Deve detectar Opera pelo User-Agent");
    }

    @Test
    @DisplayName("Deve detectar Internet Explorer 8.0 como suportado")
    void deveDetectarIE8ComoSuportado() {
        // Given - User-Agent string do IE 8.0
        String ie8UserAgent = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)";
        
        // When
        boolean isMSIE = isMSIEDetected(ie8UserAgent);
        
        // Then
        assertTrue(isMSIE, "Deve detectar IE 8.0 como suportado");
    }

    @Test
    @DisplayName("Deve detectar Internet Explorer 9.0 como suportado")
    void deveDetectarIE9ComoSuportado() {
        // Given - User-Agent string do IE 9.0
        String ie9UserAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";
        
        // When
        boolean isMSIE = isMSIEDetected(ie9UserAgent);
        
        // Then
        assertTrue(isMSIE, "Deve detectar IE 9.0 como suportado");
    }

    @Test
    @DisplayName("Deve rejeitar Internet Explorer 6.0 como não suportado")
    void deveRejeitarIE6ComoNaoSuportado() {
        // Given - User-Agent string do IE 6.0 (não suportado)
        String ie6UserAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)";
        
        // When
        boolean isMSIE = isMSIEDetected(ie6UserAgent);
        
        // Then
        assertFalse(isMSIE, "Deve rejeitar IE 6.0 como não suportado");
    }

    @Test
    @DisplayName("Deve rejeitar Internet Explorer 11.0 como não suportado")
    void deveRejeitarIE11ComoNaoSuportado() {
        // Given - User-Agent string do IE 11.0 (não suportado pela versão do código)
        String ie11UserAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
        
        // When
        boolean isMSIE = isMSIEDetected(ie11UserAgent);
        
        // Then
        assertFalse(isMSIE, "Deve rejeitar IE 11.0 como não suportado (não contém MSIE)");
    }

    @Test
    @DisplayName("Deve tratar User-Agent nulo sem erro")
    void deveTratarUserAgentNuloSemErro() {
        // Given
        String nullUserAgent = null;
        
        // When & Then
        assertFalse(isFirefoxDetected(nullUserAgent), "Não deve detectar Firefox com User-Agent nulo");
        assertFalse(isChromeDetected(nullUserAgent), "Não deve detectar Chrome com User-Agent nulo");
        assertFalse(isSafariDetected(nullUserAgent), "Não deve detectar Safari com User-Agent nulo");
        assertFalse(isOperaDetected(nullUserAgent), "Não deve detectar Opera com User-Agent nulo");
        assertFalse(isMSIEDetected(nullUserAgent), "Não deve detectar IE com User-Agent nulo");
    }

    @Test
    @DisplayName("Deve extrair informações corretas do navegador Firefox")
    void deveExtrairInformacoesCorretasDoFirefox() {
        // Given
        String firefoxUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0";
        
        // When
        String browserInfo = getBrowserInfo(firefoxUserAgent);
        
        // Then
        assertEquals("Firefox-91.0", browserInfo, "Deve extrair nome e versão do Firefox corretamente");
    }

    @Test
    @DisplayName("Deve extrair informações corretas do navegador Chrome")
    void deveExtrairInformacoesCorretasDoChrome() {
        // Given
        String chromeUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
        
        // When
        String browserInfo = getBrowserInfo(chromeUserAgent);
        
        // Then
        assertEquals("Chrome-91.0.4472.124", browserInfo, "Deve extrair nome e versão do Chrome corretamente");
    }

    @Test
    @DisplayName("Deve extrair informações corretas do Internet Explorer")
    void deveExtrairInformacoesCorretasDoIE() {
        // Given
        String ie8UserAgent = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)";
        
        // When
        String browserInfo = getBrowserInfo(ie8UserAgent);
        
        // Then
        assertEquals("Internet Explorer-8.0", browserInfo, "Deve extrair nome e versão do IE corretamente");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15",
        "Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.18",
        "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)",
        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)"
    })
    @DisplayName("Deve identificar pelo menos um navegador suportado para User-Agents válidos")
    void deveIdentificarPeloMenosUmNavegadorSuportado(String userAgent) {
        // When
        boolean isSupported = isFirefoxDetected(userAgent) || 
                             isChromeDetected(userAgent) || 
                             isSafariDetected(userAgent) || 
                             isOperaDetected(userAgent) || 
                             isMSIEDetected(userAgent);
        
        // Then
        assertTrue(isSupported, "Deve identificar pelo menos um navegador suportado para: " + userAgent);
    }

    @Test
    @DisplayName("Deve priorizar Chrome sobre Safari quando ambos estão presentes")
    void devePriorizarChromesobreSafariQuandoAmbosEstaoPresentes() {
        // Given - Chrome User-Agent que também contém Safari
        String chromeUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
        
        // When
        boolean isChrome = isChromeDetected(chromeUserAgent);
        boolean isSafari = isSafariDetected(chromeUserAgent);
        String browserInfo = getBrowserInfo(chromeUserAgent);
        
        // Then
        assertTrue(isChrome, "Deve detectar Chrome");
        assertTrue(isSafari, "Também detecta Safari (pois Chrome contém Safari na string)");
        assertTrue(browserInfo.startsWith("Chrome-"), "Mas deve priorizar Chrome na extração de informações");
    }

    @Test
    @DisplayName("Deve retornar string vazia para navegador desconhecido")
    void deveRetornarStringVaziaParaNavegadorDesconhecido() {
        // Given
        String unknownUserAgent = "UnknownBrowser/1.0";
        
        // When
        String browserInfo = getBrowserInfo(unknownUserAgent);
        
        // Then
        assertEquals("-", browserInfo, "Deve retornar string vazia para navegador desconhecido");
    }

    @Test
    @DisplayName("Deve validar versões específicas do IE suportadas")
    void deveValidarVersoesEspecificasDoIESuportadas() {
        // Given
        String ie7UserAgent = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)";
        String ie8UserAgent = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)";
        String ie9UserAgent = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";
        String ie10UserAgent = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)";
        
        // When & Then
        assertTrue(checkVersionIE(ie7UserAgent), "IE 7.0 deve ser suportado");
        assertTrue(checkVersionIE(ie8UserAgent), "IE 8.0 deve ser suportado");
        assertTrue(checkVersionIE(ie9UserAgent), "IE 9.0 deve ser suportado");
        assertFalse(checkVersionIE(ie10UserAgent), "IE 10.0 não deve ser suportado");
    }

    @Test
    @DisplayName("Deve determinar se navegador é suportado ou deve mostrar página de incompatibilidade")
    void deveDeterminarSeNavegadorESuportadoOuMostrarPaginaDeIncompatibilidade() {
        // Given - Navegadores suportados
        String[] navegadoresSuportados = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)"
        };
        
        // Given - Navegadores não suportados
        String[] navegadoresNaoSuportados = {
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)",
            "UnknownBrowser/1.0",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)"
        };
        
        // When & Then - Navegadores suportados devem redirecionar
        for (String userAgent : navegadoresSuportados) {
            boolean shouldRedirect = isFirefoxDetected(userAgent) || 
                                   isChromeDetected(userAgent) || 
                                   isSafariDetected(userAgent) || 
                                   isOperaDetected(userAgent) || 
                                   isMSIEDetected(userAgent);
            assertTrue(shouldRedirect, "Navegador deve redirecionar para login: " + getBrowserInfo(userAgent));
        }
        
        // When & Then - Navegadores não suportados devem mostrar página de erro
        for (String userAgent : navegadoresNaoSuportados) {
            boolean shouldRedirect = isFirefoxDetected(userAgent) || 
                                   isChromeDetected(userAgent) || 
                                   isSafariDetected(userAgent) || 
                                   isOperaDetected(userAgent) || 
                                   isMSIEDetected(userAgent);
            assertFalse(shouldRedirect, "Navegador não deve redirecionar: " + getBrowserInfo(userAgent));
        }
    }
}

package br.com.mulato.cso.view.beans;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.servlet.ServletContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Testes unitários para o ThemeSwitcherBean
 * Testa a funcionalidade de seleção e troca de temas do PrimeFaces
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ThemeSwitcherBean - Seleção de Temas")
class ThemeSwitcherBeanTest {

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @Mock
    private ServletContext servletContext;

    private ThemeSwitcherBean themeSwitcherBean;

    @BeforeEach
    void setUp() {
        themeSwitcherBean = new ThemeSwitcherBean();
        
        // Setup básico do mock do FacesContext usando lenient() para evitar UnnecessaryStubbing
        lenient().when(facesContext.getExternalContext()).thenReturn(externalContext);
        lenient().when(externalContext.getContext()).thenReturn(servletContext);
    }

    @Test
    @DisplayName("Deve inicializar bean corretamente")
    void deveInicializarBeanCorretamente() {
        // Given/When/Then
        assertNotNull(themeSwitcherBean);
        assertNull(themeSwitcherBean.getTheme()); // Antes do @PostConstruct
    }

    @Test
    @DisplayName("Deve carregar tema padrão do web.xml")
    void deveCarregarTemaPadraoDoWebXml() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            String temaEsperado = "aristo";
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(servletContext.getInitParameter("themeDefault")).thenReturn(temaEsperado);
            
            // When
            themeSwitcherBean.init();
            
            // Then
            assertEquals(temaEsperado, themeSwitcherBean.getTheme());
        }
    }

    @Test
    @DisplayName("Deve usar tema fallback quando não configurado no web.xml")
    void deveUsarTemaFallbackQuandoNaoConfiguradoNoWebXml() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(servletContext.getInitParameter("themeDefault")).thenReturn(null);
            
            // When
            themeSwitcherBean.init();
            
            // Then
            assertEquals("nova-light", themeSwitcherBean.getTheme());
        }
    }

    @Test
    @DisplayName("Deve definir e recuperar tema corretamente")
    void deveDefinirERecuperarTemaCorretamente() {
        // Given
        String temaEsperado = "dark-hive";
        
        // When
        themeSwitcherBean.setTheme(temaEsperado);
        
        // Then
        assertEquals(temaEsperado, themeSwitcherBean.getTheme());
    }

    @Test
    @DisplayName("Deve retornar lista de temas disponíveis")
    void deveRetornarListaDeTemasDisponiveis() {
        // When
        List<ThemeSwitcherBean.Theme> temas = themeSwitcherBean.getThemes();
        
        // Then
        assertNotNull(temas);
        assertFalse(temas.isEmpty());
        assertTrue(temas.size() >= 30); // Deve ter pelo menos 30 temas
        
        // Verifica alguns temas específicos
        assertTrue(temas.stream().anyMatch(t -> "aristo".equals(t.getName())));
        assertTrue(temas.stream().anyMatch(t -> "dark-hive".equals(t.getName())));
        assertTrue(temas.stream().anyMatch(t -> "nova-light".equals(t.getName())));
        assertTrue(temas.stream().anyMatch(t -> "ui-lightness".equals(t.getName())));
    }

    @Test
    @DisplayName("Deve retornar temas como SelectItems para JSF")
    void deveRetornarTemasComoSelectItemsParaJsf() {
        // When
        List<SelectItem> selectItems = themeSwitcherBean.getThemesAsSelectItems();
        
        // Then
        assertNotNull(selectItems);
        assertFalse(selectItems.isEmpty());
        assertTrue(selectItems.size() >= 30);
        
        // Verifica estrutura dos SelectItems
        SelectItem primeiroItem = selectItems.get(0);
        assertNotNull(primeiroItem.getValue());
        assertNotNull(primeiroItem.getLabel());
        
        // Verifica alguns temas específicos
        assertTrue(selectItems.stream().anyMatch(item -> "aristo".equals(item.getValue())));
        assertTrue(selectItems.stream().anyMatch(item -> "Aristo".equals(item.getLabel())));
    }

    @Test
    @DisplayName("Deve retornar label do tema atual")
    void deveRetornarLabelDoTemaAtual() {
        // Given
        themeSwitcherBean.setTheme("aristo");
        
        // When
        String label = themeSwitcherBean.getCurrentThemeLabel();
        
        // Then
        assertEquals("Aristo", label);
    }

    @Test
    @DisplayName("Deve tratar tema inexistente graciosamente")
    void deveTratarTemaInexistenteGraciosamente() {
        // Given
        String temaInexistente = "tema-inexistente";
        themeSwitcherBean.setTheme(temaInexistente);
        
        // When
        String label = themeSwitcherBean.getCurrentThemeLabel();
        
        // Then
        assertEquals(temaInexistente, label); // Retorna o próprio nome se não encontrar
    }

    @Test
    @DisplayName("Deve tratar tema nulo graciosamente")
    void deveTratarTemaNuloGraciosamente() {
        // Given
        themeSwitcherBean.setTheme(null);
        
        // When
        String label = themeSwitcherBean.getCurrentThemeLabel();
        
        // Then
        assertEquals("No theme selected", label);
    }

    @Test
    @DisplayName("Deve executar mudança de tema via Ajax")
    void deveExecutarMudancaDeTemaViaAjax() {
        try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
            // Given
            mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getViewRoot()).thenReturn(mock(jakarta.faces.component.UIViewRoot.class));
            when(facesContext.getViewRoot().getViewId()).thenReturn("/login.xhtml");
            
            themeSwitcherBean.setTheme("dark-hive");
            
            // When/Then - não deve lançar exceção
            assertDoesNotThrow(() -> themeSwitcherBean.onThemeChange());
            
            // Verifica se tentou fazer redirect
            try {
                verify(externalContext, atLeastOnce()).redirect(anyString());
            } catch (Exception e) {
                // IOException pode ser lançada pelo redirect mock
            }
        }
    }

    @Test
    @DisplayName("Deve testar seleção de temas específicos populares")
    void deveTestarSelecaoDeTemasEspecificosPopulares() {
        // Temas mais populares para testar
        String[] temasPopulares = {
            "aristo", "nova-light", "ui-lightness", "dark-hive", 
            "cupertino", "smoothness", "redmond", "sunny"
        };
        
        for (String tema : temasPopulares) {
            // When
            themeSwitcherBean.setTheme(tema);
            
            // Then
            assertEquals(tema, themeSwitcherBean.getTheme());
            assertNotNull(themeSwitcherBean.getCurrentThemeLabel());
            assertFalse(themeSwitcherBean.getCurrentThemeLabel().isEmpty());
            
            // Verifica se o tema existe na lista
            List<ThemeSwitcherBean.Theme> temas = themeSwitcherBean.getThemes();
            assertTrue(temas.stream().anyMatch(t -> tema.equals(t.getName())),
                      "Tema '" + tema + "' deve existir na lista de temas disponíveis");
        }
    }

    @Test
    @DisplayName("Deve validar estrutura interna da classe Theme")
    void deveValidarEstruturaInternaDaClasseTheme() {
        // Given
        String nomeTheme = "test-theme";
        String labelTheme = "Test Theme";
        
        // When
        ThemeSwitcherBean.Theme theme = new ThemeSwitcherBean.Theme(nomeTheme, labelTheme);
        
        // Then
        assertEquals(nomeTheme, theme.getName());
        assertEquals(labelTheme, theme.getLabel());
        assertEquals(labelTheme, theme.toString());
    }

    @Test
    @DisplayName("Deve verificar consistência entre temas e selectItems")
    void deveVerificarConsistenciaEntreTemasESelectItems() {
        // When
        List<ThemeSwitcherBean.Theme> temas = themeSwitcherBean.getThemes();
        List<SelectItem> selectItems = themeSwitcherBean.getThemesAsSelectItems();
        
        // Then
        assertEquals(temas.size(), selectItems.size(), 
                    "Número de temas deve ser igual ao número de SelectItems");
        
        // Verifica se cada tema tem seu SelectItem correspondente
        for (ThemeSwitcherBean.Theme tema : temas) {
            boolean temSelectItemCorrespondente = selectItems.stream()
                .anyMatch(item -> tema.getName().equals(item.getValue()) && 
                                tema.getLabel().equals(item.getLabel()));
            assertTrue(temSelectItemCorrespondente, 
                      "Tema '" + tema.getName() + "' deve ter SelectItem correspondente");
        }
    }

    @Test
    @DisplayName("Deve testar troca sequencial de múltiplos temas")
    void deveTestarTrocaSequencialDeMultiplosTemas() {
        // Given
        String[] sequenciaTemas = {"aristo", "dark-hive", "nova-light", "ui-lightness", "cupertino"};
        
        for (String tema : sequenciaTemas) {
            // When
            themeSwitcherBean.setTheme(tema);
            
            // Then
            assertEquals(tema, themeSwitcherBean.getTheme());
            
            String label = themeSwitcherBean.getCurrentThemeLabel();
            assertNotNull(label);
            assertNotEquals("No theme selected", label);
            assertNotEquals(tema, label); // Label deve ser diferente do nome técnico
        }
    }

    @Test
    @DisplayName("Deve validar que todos os temas têm nomes e labels válidos")
    void deveValidarQueTodosOsTemasTemNomesELabelsValidos() {
        // When
        List<ThemeSwitcherBean.Theme> temas = themeSwitcherBean.getThemes();
        
        // Then
        for (ThemeSwitcherBean.Theme tema : temas) {
            assertNotNull(tema.getName(), "Nome do tema não deve ser nulo");
            assertNotNull(tema.getLabel(), "Label do tema não deve ser nulo");
            assertFalse(tema.getName().trim().isEmpty(), "Nome do tema não deve estar vazio");
            assertFalse(tema.getLabel().trim().isEmpty(), "Label do tema não deve estar vazio");
            
            // Verifica padrão de nomes (lowercase com hífens)
            assertTrue(tema.getName().matches("^[a-z]([a-z\\-])*[a-z]?$"), 
                      "Nome do tema '" + tema.getName() + "' deve seguir padrão lowercase com hífens");
        }
    }
}

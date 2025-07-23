package br.com.mulato.cso.view.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.mulato.cso.view.controller.LoginController;

/**
 * Testes unitários para as permissões de menu do LoginController
 * Valida as regras de negócio para exibição dos menus baseados nos perfis de usuário
 * 
 * @author Test Suite
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes das Permissões de Menu por Perfil")
class MenuNavigationTest {

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
    }

    @Test
    @DisplayName("Deve permitir menu de troca de senha para perfis ADMINISTRATOR, BUSINESS e CUSTOMER")
    void devePermitirMenuTrocaSenhaParaPerfisAutorizados() {
        // ADMINISTRATOR
        setUserProfile("ADMINISTRATOR");
        assertTrue(loginController.getMenuChange(), 
                "ADMINISTRATOR deve ter acesso ao menu de troca de senha");

        // BUSINESS
        setUserProfile("BUSINESS");
        assertTrue(loginController.getMenuChange(), 
                "BUSINESS deve ter acesso ao menu de troca de senha");

        // CUSTOMER
        setUserProfile("CUSTOMER");
        assertTrue(loginController.getMenuChange(), 
                "CUSTOMER deve ter acesso ao menu de troca de senha");
    }

    @Test
    @DisplayName("Deve negar menu de troca de senha para perfil COURIER")
    void deveNegarMenuTrocaSenhaParaPerfilCourier() {
        setUserProfile("COURIER");
        assertFalse(loginController.getMenuChange(), 
                "COURIER não deve ter acesso ao menu de troca de senha");
    }

    @Test
    @DisplayName("Deve permitir menu de usuários apenas para ADMINISTRATOR")
    void devePermitirMenuUsuariosApenasParaAdministrator() {
        // ADMINISTRATOR
        setUserProfile("ADMINISTRATOR");
        assertTrue(loginController.getMenuUsers(), 
                "ADMINISTRATOR deve ter acesso ao menu de usuários");

        // Outros perfis não devem ter acesso
        String[] otherProfiles = {"BUSINESS", "CUSTOMER", "COURIER"};
        for (String profile : otherProfiles) {
            setUserProfile(profile);
            assertFalse(loginController.getMenuUsers(), 
                    profile + " não deve ter acesso ao menu de usuários");
        }
    }

    @Test
    @DisplayName("Deve controlar menu de negócios baseado no perfil")
    void deveControlarMenuNegociosBaseadoNoPerfil() {
        // ADMINISTRATOR deve ver menu completo de negócios
        setUserProfile("ADMINISTRATOR");
        assertTrue(loginController.getMenuBusinesses(), 
                "ADMINISTRATOR deve ter acesso ao menu de negócios");
        assertTrue(loginController.getMenuAllBusinesses(), 
                "ADMINISTRATOR deve poder listar todos os negócios");
        assertFalse(loginController.getMenuViewBusiness(), 
                "ADMINISTRATOR não deve ter view específico de negócio");

        // BUSINESS deve ver apenas seu próprio negócio
        setUserProfile("BUSINESS");
        assertTrue(loginController.getMenuBusinesses(), 
                "BUSINESS deve ter acesso ao menu de negócios");
        assertTrue(loginController.getMenuViewBusiness(), 
                "BUSINESS deve poder visualizar seu negócio");
        assertFalse(loginController.getMenuAllBusinesses(), 
                "BUSINESS não deve listar todos os negócios");

        // Outros perfis não devem ter acesso
        String[] otherProfiles = {"CUSTOMER", "COURIER"};
        for (String profile : otherProfiles) {
            setUserProfile(profile);
            assertFalse(loginController.getMenuBusinesses(), 
                    profile + " não deve ter acesso ao menu de negócios");
        }
    }

    @Test
    @DisplayName("Deve controlar menu de tabelas de preço para BUSINESS e CUSTOMER")
    void deveControlarMenuTabelasPrecoParaBusinessECustomer() {
        // BUSINESS deve ter acesso
        setUserProfile("BUSINESS");
        assertTrue(loginController.getMenuTables(), 
                "BUSINESS deve ter acesso às tabelas de preço");

        // CUSTOMER deve ter acesso
        setUserProfile("CUSTOMER");
        assertTrue(loginController.getMenuTables(), 
                "CUSTOMER deve ter acesso às tabelas de preço");

        // Outros perfis não devem ter acesso
        String[] otherProfiles = {"ADMINISTRATOR", "COURIER"};
        for (String profile : otherProfiles) {
            setUserProfile(profile);
            assertFalse(loginController.getMenuTables(), 
                    profile + " não deve ter acesso às tabelas de preço");
        }
    }

    @Test
    @DisplayName("Deve controlar menu de clientes baseado no perfil")
    void deveControlarMenuClientesBaseadoNoPerfil() {
        // BUSINESS deve ter acesso completo
        setUserProfile("BUSINESS");
        assertTrue(loginController.getMenuCustomers(), 
                "BUSINESS deve ter acesso ao menu de clientes");
        assertTrue(loginController.getMenuAllCustomers(), 
                "BUSINESS deve poder listar todos os clientes");
        assertFalse(loginController.getMenuViewCustomer(), 
                "BUSINESS não deve ter view específico de cliente");

        // CUSTOMER deve ver apenas seus próprios dados
        setUserProfile("CUSTOMER");
        assertTrue(loginController.getMenuCustomers(), 
                "CUSTOMER deve ter acesso ao menu de clientes");
        assertTrue(loginController.getMenuViewCustomer(), 
                "CUSTOMER deve poder visualizar seus dados");
        assertFalse(loginController.getMenuAllCustomers(), 
                "CUSTOMER não deve listar todos os clientes");

        // Outros perfis não devem ter acesso
        String[] otherProfiles = {"ADMINISTRATOR", "COURIER"};
        for (String profile : otherProfiles) {
            setUserProfile(profile);
            assertFalse(loginController.getMenuCustomers(), 
                    profile + " não deve ter acesso ao menu de clientes");
        }
    }

    @Test
    @DisplayName("Deve controlar menu de entregadores baseado no perfil")
    void deveControlarMenuEntregadoresBaseadoNoPerfil() {
        // BUSINESS deve ter acesso completo
        setUserProfile("BUSINESS");
        assertTrue(loginController.getMenuCouriers(), 
                "BUSINESS deve ter acesso ao menu de entregadores");
        assertTrue(loginController.getMenuAllCouriers(), 
                "BUSINESS deve poder listar todos os entregadores");
        assertFalse(loginController.getMenuViewCourier(), 
                "BUSINESS não deve ter view específico de entregador");

        // COURIER deve ver apenas seus próprios dados
        setUserProfile("COURIER");
        assertTrue(loginController.getMenuCouriers(), 
                "COURIER deve ter acesso ao menu de entregadores");
        assertTrue(loginController.getMenuViewCourier(), 
                "COURIER deve poder visualizar seus dados");
        assertFalse(loginController.getMenuAllCouriers(), 
                "COURIER não deve listar todos os entregadores");

        // Outros perfis não devem ter acesso
        String[] otherProfiles = {"ADMINISTRATOR", "CUSTOMER"};
        for (String profile : otherProfiles) {
            setUserProfile(profile);
            assertFalse(loginController.getMenuCouriers(), 
                    profile + " não deve ter acesso ao menu de entregadores");
        }
    }

    @Test
    @DisplayName("Deve permitir menu de entregas para BUSINESS, CUSTOMER e COURIER")
    void devePermitirMenuEntregasParaPerfisOperacionais() {
        String[] allowedProfiles = {"BUSINESS", "CUSTOMER", "COURIER"};
        
        for (String profile : allowedProfiles) {
            setUserProfile(profile);
            assertTrue(loginController.getMenuAllDeliveries(), 
                    profile + " deve ter acesso ao menu de entregas");
        }

        // ADMINISTRATOR não deve ter acesso
        setUserProfile("ADMINISTRATOR");
        assertFalse(loginController.getMenuAllDeliveries(), 
                "ADMINISTRATOR não deve ter acesso ao menu de entregas");
    }

    @Test
    @DisplayName("Deve sempre permitir menu de saída")
    void deveSemprePermitirMenuDeSaida() {
        String[] allProfiles = {"ADMINISTRATOR", "BUSINESS", "CUSTOMER", "COURIER"};
        
        for (String profile : allProfiles) {
            setUserProfile(profile);
            assertTrue(loginController.getMenuExit(), 
                    profile + " deve ter acesso ao menu de saída");
            assertTrue(loginController.getOpenTabsExit(), 
                    profile + " deve poder abrir tabs de saída");
        }
    }

    @Test
    @DisplayName("Deve tratar perfil nulo corretamente")
    void deveTratarPerfilNuloCorretamente() {
        setUserProfile(null);
        
        assertFalse(loginController.getMenuChange(), 
                "Perfil nulo não deve ter acesso ao menu de troca de senha");
        assertFalse(loginController.getMenuUsers(), 
                "Perfil nulo não deve ter acesso ao menu de usuários");
        assertFalse(loginController.getMenuBusinesses(), 
                "Perfil nulo não deve ter acesso ao menu de negócios");
        assertFalse(loginController.getMenuTables(), 
                "Perfil nulo não deve ter acesso às tabelas de preço");
        assertFalse(loginController.getMenuCustomers(), 
                "Perfil nulo não deve ter acesso ao menu de clientes");
        assertFalse(loginController.getMenuCouriers(), 
                "Perfil nulo não deve ter acesso ao menu de entregadores");
        assertFalse(loginController.getMenuAllDeliveries(), 
                "Perfil nulo não deve ter acesso ao menu de entregas");
        
        // Menu de saída deve estar sempre disponível
        assertTrue(loginController.getMenuExit(), 
                "Menu de saída deve estar sempre disponível");
    }

    @Test
    @DisplayName("Deve tratar perfil inválido corretamente")
    void deveTratarPerfilInvalidoCorretamente() {
        setUserProfile("INVALID_PROFILE");
        
        assertFalse(loginController.getMenuChange(), 
                "Perfil inválido não deve ter acesso ao menu de troca de senha");
        assertFalse(loginController.getMenuUsers(), 
                "Perfil inválido não deve ter acesso ao menu de usuários");
        assertFalse(loginController.getMenuBusinesses(), 
                "Perfil inválido não deve ter acesso ao menu de negócios");
        assertFalse(loginController.getMenuTables(), 
                "Perfil inválido não deve ter acesso às tabelas de preço");
        assertFalse(loginController.getMenuCustomers(), 
                "Perfil inválido não deve ter acesso ao menu de clientes");
        assertFalse(loginController.getMenuCouriers(), 
                "Perfil inválido não deve ter acesso ao menu de entregadores");
        assertFalse(loginController.getMenuAllDeliveries(), 
                "Perfil inválido não deve ter acesso ao menu de entregas");
    }

    @Test
    @DisplayName("Deve validar regras específicas para perfil ADMINISTRATOR")
    void deveValidarRegrasEspecificasParaPerfilAdministrator() {
        setUserProfile("ADMINISTRATOR");
        
        // ADMINISTRATOR tem acesso apenas a funcionalidades administrativas
        assertTrue(loginController.getMenuChange(), 
                "ADMINISTRATOR deve poder trocar senha");
        assertTrue(loginController.getMenuUsers(), 
                "ADMINISTRATOR deve gerenciar usuários");
        assertTrue(loginController.getMenuBusinesses(), 
                "ADMINISTRATOR deve ter acesso a negócios");
        assertTrue(loginController.getMenuAllBusinesses(), 
                "ADMINISTRATOR deve listar todos os negócios");
        
        // ADMINISTRATOR não deve ter acesso a funcionalidades operacionais
        assertFalse(loginController.getMenuTables(), 
                "ADMINISTRATOR não deve acessar tabelas de preço");
        assertFalse(loginController.getMenuCustomers(), 
                "ADMINISTRATOR não deve acessar clientes");
        assertFalse(loginController.getMenuCouriers(), 
                "ADMINISTRATOR não deve acessar entregadores");
        assertFalse(loginController.getMenuAllDeliveries(), 
                "ADMINISTRATOR não deve acessar entregas");
    }

    @Test
    @DisplayName("Deve validar regras específicas para perfil BUSINESS")
    void deveValidarRegrasEspecificasParaPerfilBusiness() {
        setUserProfile("BUSINESS");
        
        // BUSINESS tem acesso completo às funcionalidades operacionais
        assertTrue(loginController.getMenuChange(), 
                "BUSINESS deve poder trocar senha");
        assertTrue(loginController.getMenuBusinesses(), 
                "BUSINESS deve ter acesso ao menu de negócios");
        assertTrue(loginController.getMenuViewBusiness(), 
                "BUSINESS deve visualizar seu negócio");
        assertTrue(loginController.getMenuTables(), 
                "BUSINESS deve acessar tabelas de preço");
        assertTrue(loginController.getMenuCustomers(), 
                "BUSINESS deve gerenciar clientes");
        assertTrue(loginController.getMenuAllCustomers(), 
                "BUSINESS deve listar seus clientes");
        assertTrue(loginController.getMenuCouriers(), 
                "BUSINESS deve gerenciar entregadores");
        assertTrue(loginController.getMenuAllCouriers(), 
                "BUSINESS deve listar seus entregadores");
        assertTrue(loginController.getMenuAllDeliveries(), 
                "BUSINESS deve gerenciar entregas");
        
        // BUSINESS não deve ter acesso a funcionalidades administrativas
        assertFalse(loginController.getMenuUsers(), 
                "BUSINESS não deve gerenciar usuários do sistema");
        assertFalse(loginController.getMenuAllBusinesses(), 
                "BUSINESS não deve listar todos os negócios");
    }

    @Test
    @DisplayName("Deve validar regras específicas para perfil CUSTOMER")
    void deveValidarRegrasEspecificasParaPerfilCustomer() {
        setUserProfile("CUSTOMER");
        
        // CUSTOMER tem acesso limitado apenas aos seus dados
        assertTrue(loginController.getMenuChange(), 
                "CUSTOMER deve poder trocar senha");
        assertTrue(loginController.getMenuTables(), 
                "CUSTOMER deve consultar tabelas de preço");
        assertTrue(loginController.getMenuCustomers(), 
                "CUSTOMER deve acessar seus dados");
        assertTrue(loginController.getMenuViewCustomer(), 
                "CUSTOMER deve visualizar seus dados");
        assertTrue(loginController.getMenuAllDeliveries(), 
                "CUSTOMER deve consultar suas entregas");
        
        // CUSTOMER não deve ter acesso a gerenciamento
        assertFalse(loginController.getMenuUsers(), 
                "CUSTOMER não deve gerenciar usuários");
        assertFalse(loginController.getMenuBusinesses(), 
                "CUSTOMER não deve gerenciar negócios");
        assertFalse(loginController.getMenuAllCustomers(), 
                "CUSTOMER não deve listar outros clientes");
        assertFalse(loginController.getMenuCouriers(), 
                "CUSTOMER não deve gerenciar entregadores");
    }

    @Test
    @DisplayName("Deve validar regras específicas para perfil COURIER")
    void deveValidarRegrasEspecificasParaPerfilCourier() {
        setUserProfile("COURIER");
        
        // COURIER tem acesso muito limitado
        assertTrue(loginController.getMenuCouriers(), 
                "COURIER deve acessar menu de entregadores");
        assertTrue(loginController.getMenuViewCourier(), 
                "COURIER deve visualizar seus dados");
        assertTrue(loginController.getMenuAllDeliveries(), 
                "COURIER deve consultar suas entregas");
        
        // COURIER não deve ter acesso a outras funcionalidades
        assertFalse(loginController.getMenuChange(), 
                "COURIER não deve trocar senha pelo sistema");
        assertFalse(loginController.getMenuUsers(), 
                "COURIER não deve gerenciar usuários");
        assertFalse(loginController.getMenuBusinesses(), 
                "COURIER não deve gerenciar negócios");
        assertFalse(loginController.getMenuTables(), 
                "COURIER não deve acessar tabelas de preço");
        assertFalse(loginController.getMenuCustomers(), 
                "COURIER não deve gerenciar clientes");
        assertFalse(loginController.getMenuAllCouriers(), 
                "COURIER não deve listar outros entregadores");
    }

    @Test
    @DisplayName("Deve validar consistência entre getters de menu")
    void deveValidarConsistenciaEntreGettersDeMenu() {
        String[] profiles = {"ADMINISTRATOR", "BUSINESS", "CUSTOMER", "COURIER"};
        
        for (String profile : profiles) {
            setUserProfile(profile);
            
            // Se tem acesso ao menu principal, deve ser consistente com subitens
            if (loginController.getMenuBusinesses()) {
                assertTrue(loginController.getMenuViewBusiness() || loginController.getMenuAllBusinesses(),
                        "Se tem menu businesses, deve ter pelo menos um subitem");
            }
            
            if (loginController.getMenuCustomers()) {
                assertTrue(loginController.getMenuViewCustomer() || loginController.getMenuAllCustomers(),
                        "Se tem menu customers, deve ter pelo menos um subitem");
            }
            
            if (loginController.getMenuCouriers()) {
                assertTrue(loginController.getMenuViewCourier() || loginController.getMenuAllCouriers(),
                        "Se tem menu couriers, deve ter pelo menos um subitem");
            }
        }
    }

    private void setUserProfile(String profile) {
        // Usando reflexão para acessar o campo privado 'profile'
        try {
            java.lang.reflect.Field profileField = LoginController.class.getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(loginController, profile);
        } catch (Exception e) {
            fail("Erro ao configurar perfil para teste: " + e.getMessage());
        }
    }
}

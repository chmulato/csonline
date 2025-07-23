package br.com.mulato.cso.view.page;

import br.com.mulato.cso.service.MenuService;
import br.com.mulato.cso.service.UserService;
import br.com.mulato.cso.service.PermissionService;
import br.com.mulato.cso.model.User;
import br.com.mulato.cso.model.MenuItem;
import br.com.mulato.cso.model.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuPage Tests")
class MenuPageTest {

    @Mock
    private MenuService menuService;

    @Mock
    private UserService userService;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private MenuPage menuPage;

    private User mockUser;
    private List<MenuItem> mockMenuItems;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setRole(Role.ADMIN);

        MenuItem homeItem = new MenuItem("Home", "/home", "fa-home", true);
        MenuItem usersItem = new MenuItem("Usuários", "/users", "fa-users", true);
        MenuItem reportsItem = new MenuItem("Relatórios", "/reports", "fa-chart", false);
        
        mockMenuItems = Arrays.asList(homeItem, usersItem, reportsItem);
    }

    @Test
    @DisplayName("Deve inicializar menu corretamente")
    void shouldInitializeMenuCorrectly() {
        // Given
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(menuService.getMenuItemsForUser(mockUser)).thenReturn(mockMenuItems);

        // When
        menuPage.init();

        // Then
        assertNotNull(menuPage.getMenuItems());
        assertEquals(3, menuPage.getMenuItems().size());
        verify(menuService).getMenuItemsForUser(mockUser);
    }

    @Test
    @DisplayName("Deve filtrar itens de menu baseado em permissões")
    void shouldFilterMenuItemsByPermissions() {
        // Given
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(permissionService.hasPermission(mockUser, "USERS_VIEW")).thenReturn(true);
        when(permissionService.hasPermission(mockUser, "REPORTS_VIEW")).thenReturn(false);
        when(menuService.getMenuItemsForUser(mockUser)).thenReturn(mockMenuItems);

        // When
        menuPage.init();
        List<MenuItem> visibleItems = menuPage.getVisibleMenuItems();

        // Then
        assertEquals(2, visibleItems.size());
        assertTrue(visibleItems.stream().anyMatch(item -> item.getLabel().equals("Home")));
        assertTrue(visibleItems.stream().anyMatch(item -> item.getLabel().equals("Usuários")));
        assertFalse(visibleItems.stream().anyMatch(item -> item.getLabel().equals("Relatórios")));
    }

    @Test
    @DisplayName("Deve navegar para item selecionado")
    void shouldNavigateToSelectedItem() {
        // Given
        MenuItem selectedItem = new MenuItem("Usuários", "/users", "fa-users", true);
        menuPage.setSelectedMenuItem(selectedItem);

        // When
        String result = menuPage.navigate();

        // Then
        assertEquals("/users", result);
        assertEquals(selectedItem, menuPage.getSelectedMenuItem());
    }

    @Test
    @DisplayName("Deve marcar item ativo no menu")
    void shouldMarkActiveMenuItem() {
        // Given
        menuPage.setCurrentPath("/users");
        menuPage.setMenuItems(mockMenuItems);

        // When
        MenuItem activeItem = menuPage.getActiveMenuItem();

        // Then
        assertNotNull(activeItem);
        assertEquals("Usuários", activeItem.getLabel());
        assertEquals("/users", activeItem.getUrl());
    }

    @Test
    @DisplayName("Deve retornar menu vazio para usuário não autenticado")
    void shouldReturnEmptyMenuForUnauthenticatedUser() {
        // Given
        when(userService.getCurrentUser()).thenReturn(null);

        // When
        menuPage.init();

        // Then
        assertTrue(menuPage.getMenuItems().isEmpty());
        verify(menuService, never()).getMenuItemsForUser(any());
    }

    @Test
    @DisplayName("Deve agrupar itens de menu por categoria")
    void shouldGroupMenuItemsByCategory() {
        // Given
        MenuItem adminItem1 = new MenuItem("Usuários", "/users", "fa-users", true);
        adminItem1.setCategory("ADMIN");
        MenuItem adminItem2 = new MenuItem("Configurações", "/config", "fa-cog", true);
        adminItem2.setCategory("ADMIN");
        MenuItem reportItem = new MenuItem("Relatórios", "/reports", "fa-chart", true);
        reportItem.setCategory("REPORTS");

        List<MenuItem> groupedItems = Arrays.asList(adminItem1, adminItem2, reportItem);
        menuPage.setMenuItems(groupedItems);

        // When
        var groupedMenu = menuPage.getGroupedMenuItems();

        // Then
        assertTrue(groupedMenu.containsKey("ADMIN"));
        assertTrue(groupedMenu.containsKey("REPORTS"));
        assertEquals(2, groupedMenu.get("ADMIN").size());
        assertEquals(1, groupedMenu.get("REPORTS").size());
    }

    @Test
    @DisplayName("Deve validar se usuário tem acesso ao menu")
    void shouldValidateUserAccessToMenu() {
        // Given
        MenuItem restrictedItem = new MenuItem("Admin", "/admin", "fa-lock", true);
        restrictedItem.setRequiredRole(Role.ADMIN);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(permissionService.hasRole(mockUser, Role.ADMIN)).thenReturn(true);

        // When
        boolean hasAccess = menuPage.hasAccessToMenuItem(restrictedItem);

        // Then
        assertTrue(hasAccess);
    }

    @Test
    @DisplayName("Deve negar acesso para usuário sem permissão")
    void shouldDenyAccessForUserWithoutPermission() {
        // Given
        User regularUser = new User();
        regularUser.setRole(Role.USER);
        
        MenuItem restrictedItem = new MenuItem("Admin", "/admin", "fa-lock", true);
        restrictedItem.setRequiredRole(Role.ADMIN);

        when(userService.getCurrentUser()).thenReturn(regularUser);
        when(permissionService.hasRole(regularUser, Role.ADMIN)).thenReturn(false);

        // When
        boolean hasAccess = menuPage.hasAccessToMenuItem(restrictedItem);

        // Then
        assertFalse(hasAccess);
    }

    @Test
    @DisplayName("Deve contar itens visíveis no menu")
    void shouldCountVisibleMenuItems() {
        // Given
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(menuService.getMenuItemsForUser(mockUser)).thenReturn(mockMenuItems);
        menuPage.init();

        // When
        int visibleCount = menuPage.getVisibleMenuItemsCount();

        // Then
        assertEquals(2, visibleCount); // Home e Usuários são visíveis
    }

    @Test
    @DisplayName("Deve recarregar menu quando necessário")
    void shouldReloadMenuWhenNeeded() {
        // Given
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(menuService.getMenuItemsForUser(mockUser)).thenReturn(mockMenuItems);

        // When
        menuPage.reloadMenu();

        // Then
        verify(menuService, times(1)).getMenuItemsForUser(mockUser);
        assertNotNull(menuPage.getMenuItems());
    }
}

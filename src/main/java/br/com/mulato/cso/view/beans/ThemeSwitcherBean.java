package br.com.mulato.cso.view.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Unified theme management bean for CSOnline application.
 * Replaces both ThemeBean and provides consistent theme management across all pages.
 * 
 * @author CSOnline Migration Team
 */
@Named("themeSwitcherBean")
@SessionScoped
public class ThemeSwitcherBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = LogManager.getLogger(ThemeSwitcherBean.class);
    
    private String theme;
    
    /**
     * Inner class representing a theme with name and label
     */
    public static class Theme implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String name;
        private String label;
        
        public Theme(String name, String label) {
            this.name = name;
            this.label = label;
        }
        
        public String getName() {
            return name;
        }
        
        public String getLabel() {
            return label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    private static final List<Theme> AVAILABLE_THEMES;
    
    static {
        AVAILABLE_THEMES = new ArrayList<>(31);
        AVAILABLE_THEMES.add(new Theme("aristo", "Aristo"));
        AVAILABLE_THEMES.add(new Theme("black-tie", "Black-Tie"));
        AVAILABLE_THEMES.add(new Theme("blitzer", "Blitzer"));
        AVAILABLE_THEMES.add(new Theme("bluesky", "Bluesky"));
        AVAILABLE_THEMES.add(new Theme("casablanca", "Casablanca"));
        AVAILABLE_THEMES.add(new Theme("cupertino", "Cupertino"));
        AVAILABLE_THEMES.add(new Theme("dark-hive", "Dark-Hive"));
        AVAILABLE_THEMES.add(new Theme("dot-luv", "Dot-Luv"));
        AVAILABLE_THEMES.add(new Theme("eggplant", "Eggplant"));
        AVAILABLE_THEMES.add(new Theme("excite-bike", "Excite-Bike"));
        AVAILABLE_THEMES.add(new Theme("flick", "Flick"));
        AVAILABLE_THEMES.add(new Theme("glass-x", "Glass-X"));
        AVAILABLE_THEMES.add(new Theme("home", "Home"));
        AVAILABLE_THEMES.add(new Theme("hot-sneaks", "Hot-Sneaks"));
        AVAILABLE_THEMES.add(new Theme("humanity", "Humanity"));
        AVAILABLE_THEMES.add(new Theme("le-frog", "Le-Frog"));
        AVAILABLE_THEMES.add(new Theme("midnight", "Midnight"));
        AVAILABLE_THEMES.add(new Theme("mint-choc", "Mint-Choc"));
        AVAILABLE_THEMES.add(new Theme("nova-light", "Nova Light"));
        AVAILABLE_THEMES.add(new Theme("overcast", "Overcast"));
        AVAILABLE_THEMES.add(new Theme("pepper-grinder", "Pepper-Grinder"));
        AVAILABLE_THEMES.add(new Theme("redmond", "Redmond"));
        AVAILABLE_THEMES.add(new Theme("rocket", "Rocket"));
        AVAILABLE_THEMES.add(new Theme("sam", "Sam"));
        AVAILABLE_THEMES.add(new Theme("smoothness", "Smoothness"));
        AVAILABLE_THEMES.add(new Theme("south-street", "South-Street"));
        AVAILABLE_THEMES.add(new Theme("start", "Start"));
        AVAILABLE_THEMES.add(new Theme("sunny", "Sunny"));
        AVAILABLE_THEMES.add(new Theme("swanky-purse", "Swanky-Purse"));
        AVAILABLE_THEMES.add(new Theme("trontastic", "Trontastic"));
        AVAILABLE_THEMES.add(new Theme("ui-darkness", "UI-Darkness"));
        AVAILABLE_THEMES.add(new Theme("ui-lightness", "UI-Lightness"));
        AVAILABLE_THEMES.add(new Theme("vader", "Vader"));
    }
    
    @PostConstruct
    public void init() {
        LOGGER.info("Initializing ThemeSwitcherBean - Unified theme management");
        
        // Get default theme from web.xml
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        
        String themeDefault = servletContext.getInitParameter("themeDefault");
        if (themeDefault != null && !themeDefault.isEmpty()) {
            this.theme = themeDefault;
            LOGGER.info("Default theme loaded from web.xml: {}", themeDefault);
        } else {
            // Fallback to nova-light if no configuration found
            this.theme = "nova-light";
            LOGGER.info("Using fallback theme: nova-light");
        }
        
        LOGGER.info("ThemeSwitcherBean initialized with theme: {}", this.theme);
    }
    
    /**
     * Gets the current selected theme name
     * @return current theme name
     */
    public String getTheme() {
        return theme;
    }
    
    /**
     * Sets the current theme
     * @param theme the theme name to set
     */
    public void setTheme(String theme) {
        LOGGER.info("Theme changing from '{}' to '{}'", this.theme, theme);
        this.theme = theme;
    }
    
    /**
     * Gets the list of all available themes
     * @return List of Theme objects for selectOneMenu
     */
    public List<Theme> getThemes() {
        LOGGER.debug("Returning {} available themes", AVAILABLE_THEMES.size());
        return AVAILABLE_THEMES;
    }
    
    /**
     * Gets the list of all available themes as SelectItems (compatible with JSF selectOneMenu)
     * @return List of SelectItem objects for selectOneMenu
     */
    public List<jakarta.faces.model.SelectItem> getThemesAsSelectItems() {
        LOGGER.debug("Returning {} available themes as SelectItems", AVAILABLE_THEMES.size());
        return AVAILABLE_THEMES.stream()
            .map(t -> new jakarta.faces.model.SelectItem(t.getName(), t.getLabel()))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Gets the current theme label for display
     * @return the display label of the current theme
     */
    public String getCurrentThemeLabel() {
        if (theme == null) {
            return "No theme selected";
        }
        
        return AVAILABLE_THEMES.stream()
            .filter(t -> t.getName().equals(theme))
            .map(Theme::getLabel)
            .findFirst()
            .orElse(theme);
    }
}

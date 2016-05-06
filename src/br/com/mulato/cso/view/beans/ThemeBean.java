package br.com.mulato.cso.view.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.utils.InitProperties;

@ManagedBean(name = "themeMB")
@SessionScoped
public class ThemeBean
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = Logger.getLogger(ThemeBean.class);

	private String theme;

	private static final List<SelectItem> THEMES;

	static
	{
		THEMES = new ArrayList<>(31);
		THEMES.add(new SelectItem("aristo", "Aristo"));
		THEMES.add(new SelectItem("black-tie", "Black-Tie"));
		THEMES.add(new SelectItem("blitzer", "Blitzer"));
		THEMES.add(new SelectItem("bluesky", "Bluesky"));
		THEMES.add(new SelectItem("casablanca", "Casablanca"));
		THEMES.add(new SelectItem("cupertino", "Cupertino"));
		THEMES.add(new SelectItem("dark-hive", "Dark-Hive"));
		THEMES.add(new SelectItem("dot-luv", "Dot-Luv"));
		THEMES.add(new SelectItem("eggplant", "Eggplant"));
		THEMES.add(new SelectItem("excite-bike", "Excite-Bike"));
		THEMES.add(new SelectItem("flick", "Flick"));
		THEMES.add(new SelectItem("glass-x", "Glass-X"));
		THEMES.add(new SelectItem("home", "Home"));
		THEMES.add(new SelectItem("hot-sneaks", "Hot-Sneaks"));
		THEMES.add(new SelectItem("humanity", "Humanity"));
		THEMES.add(new SelectItem("le-frog", "Le-Frog"));
		THEMES.add(new SelectItem("midnight", "Midnight"));
		THEMES.add(new SelectItem("mint-choc", "Mint-Choc"));
		THEMES.add(new SelectItem("overcast", "Overcast"));
		THEMES.add(new SelectItem("pepper-grinder", "Pepper-Grinder"));
		THEMES.add(new SelectItem("redmond", "Redmond"));
		THEMES.add(new SelectItem("rocket", "Rocket"));
		THEMES.add(new SelectItem("sam", "Sam"));
		THEMES.add(new SelectItem("smoothness", "Smoothness"));
		THEMES.add(new SelectItem("south-street", "South-Street"));
		THEMES.add(new SelectItem("start", "Start"));
		THEMES.add(new SelectItem("sunny", "Sunny"));
		THEMES.add(new SelectItem("swanky-purse", "Swanky-Purse"));
		THEMES.add(new SelectItem("trontastic", "Trontastic"));
		THEMES.add(new SelectItem("ui-darkness", "UI-Darkness"));
		THEMES.add(new SelectItem("ui-lightness", "UI-Lightness"));
		THEMES.add(new SelectItem("vader", "Vader"));
	}

	public String getTheme ()
	{
		return theme;
	}

	public void setTheme (final String theme)
	{
		LOGGER.info("THEME CHANGING: " + theme);
		this.theme = theme;
	}

	public ThemeBean ()
	{
		super();
		LOGGER.info("Definição da aparência da aplicação!");
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		final ServletContext servletContext = (ServletContext)facesContext.getExternalContext().getContext();
		try
		{
			if (servletContext.getInitParameter("themeDefault") != null)
			{
				theme = servletContext.getInitParameter("themeDefault");
				if (InitProperties.getThemeApplication() != null)
				{
					if (!InitProperties.getThemeApplication().equals(""))
					{
						theme = InitProperties.getThemeApplication();
					}
				}
			}
			LOGGER.info("THEME: " + theme);
		}
		catch (final ParameterException e)
		{
			LOGGER.error("Erro ao definir thema! " + e.getMessage());
		}
	}

	public List<SelectItem> getThemesList ()
	{
		return THEMES;
	}
}

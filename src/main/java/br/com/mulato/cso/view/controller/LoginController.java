package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.model.UserVO;
import br.com.mulato.cso.view.beans.FacesMessages;
import br.com.mulato.cso.view.beans.ThemeBean;

public class LoginController extends AbstractController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(LoginController.class);

	private BusinessVO businessVO;

	private String profile;

	private String timeSystem;

	private String username;

	private String password;

	private boolean logged;

	private boolean master;

	private Integer id;

	private Integer userIdLogged;

	private String numberSession;

	public LoginController ()
	{
		super();
	}

	public String logar ()
	{
		return goToPage("login");
	}

	public String reset (final ActionEvent event)
	{
		logged = false;
		username = null;
		password = null;
		return goToPage("login");
	}

	public String login ()
	{

		String path = "login";

		LoginVO login = null;
		logged = false;
		timeSystem = null;
		userIdLogged = null;

		try
		{

			if (username == null)
			{
				throw new WebException("Informe login do usuário!");
			}

			if (password == null)
			{
				throw new WebException("Informe senha do usuário!");
			}

			if (username.equals(""))
			{
				throw new WebException("Informe login do usuário!");
			}

			if (password.equals(""))
			{
				throw new WebException("Informe senha do usuário!");
			}

			username = username.trim();
			password = password.trim();

			login = new LoginVO();

			login.setLogin(username);
			login.setPassword(password);

			final Boolean authenticate = FactoryService.getInstancia().getLoginService().authenticate(login);

			if ((authenticate != null) && (authenticate.booleanValue() == false))
			{
				master = true;
			}

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final ContadorController contadorController = app.evaluateExpressionGet(context, "#{contadorMB}", ContadorController.class);

			if (contadorController != null)
			{
				contadorController.maisUm(4);
			}

			logged = true;

			// Cleaning the password
			password = null;
			login.setPassword(password);

			final UserVO user = FactoryService.getInstancia().getAdminService().findByLogin(login);

			if (user == null)
			{
				throw new WebException("Usuário não encontrado!");
			}

			if (user.getId() == null)
			{
				throw new WebException("Usuário não encontrado!");
			}

			if (user.getId().intValue() <= 0)
			{
				throw new WebException("Usuário não encontrado!");
			}

			if ((user.getRole() == null) || (user.getRole().equals("")))
			{
				throw new WebException("Perfil não encontrado!");
			}

			profile = user.getRole().toUpperCase().trim();

			if (profile.equals("ADMINISTRATOR"))
			{

				userIdLogged = user.getId();

				path = "users";

				profile = "ADMINISTRATOR";

			}
			else if (profile.equals("BUSINESS"))
			{

				userIdLogged = user.getId();

				final BusinessVO vo = FactoryService.getInstancia().getBusinessService().find(user.getId());

				if (vo != null)
				{

					businessVO = vo;
					id = user.getId();

				}
				else
				{

					throw new WebException("Negócio não encontrado!");

				}

				path = "resume";

				profile = "BUSINESS";

			}
			else if (profile.equals("CUSTOMER"))
			{

				userIdLogged = user.getId();

				final BusinessVO vo = FactoryService.getInstancia().getBusinessService().findBusinessByCustomerId(user.getId());

				if (vo != null)
				{

					businessVO = vo;
					id = user.getId();

				}
				else
				{

					throw new WebException("Negócio do cliente não encontrado!");

				}

				path = "resume";

				profile = "CUSTOMER";

			}
			else if (profile.equals("COURIER"))
			{

				userIdLogged = user.getId();

				path = "resume";

				final BusinessVO vo = FactoryService.getInstancia().getBusinessService().findBusinessByCourierId(user.getId());

				if (vo != null)
				{

					businessVO = vo;
					id = user.getId();

				}
				else
				{

					throw new WebException("Negócio do entregador não encontrado!");

				}

				profile = "COURIER";

			}

			LOGGER.info("Usuário encontrado: " + user);

			final FacesContext facesContext = FacesContext.getCurrentInstance();
			final ServletContext servletContext = (ServletContext)facesContext.getExternalContext().getContext();

			final String timeout = servletContext.getInitParameter("timeout");

			// setando o tempo de inatividade do sistema em 15 minuto
			getRequest().getSession().setMaxInactiveInterval(new Integer(timeout).intValue());

			numberSession = getRequest().getSession().getId();

			final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			timeSystem = fmt.format(new Date());

		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}

		return goToBackPage(path);
	}

	public String logout ()
	{
		logged = false;
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return goToBackPage("login");
	}

	public String changeTheme (final ValueChangeEvent event)
	{
		final String theme = (String)event.getNewValue();
		if (theme != null)
		{
			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final ThemeBean themeBean = app.evaluateExpressionGet(context, "#{themeMB}", ThemeBean.class);
			if (themeBean != null)
			{
				themeBean.setTheme(theme);
				return goToPage("theme");
			}
			else
			{
				return goToPage("login");
			}
		}
		else
		{
			return goToPage("login");
		}
	}

	public boolean getMenuChange ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if ((profile.equals("ADMINISTRATOR")) || (profile.equals("BUSINESS")) || (profile.equals("CUSTOMER")))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuUsers ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if (profile.equals("ADMINISTRATOR"))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuViewBusiness ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if (profile.equals("BUSINESS"))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuAllBusinesses ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if (profile.equals("ADMINISTRATOR"))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuBusinesses ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if ((profile.equals("ADMINISTRATOR")) || (profile.equals("BUSINESS")))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuTables ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if ((profile.equals("BUSINESS")) || (profile.equals("CUSTOMER")))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuViewCustomer ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if (profile.equals("CUSTOMER"))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuCustomers ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if ((profile.equals("BUSINESS")) || (profile.equals("CUSTOMER")))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuAllCustomers ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if (profile.equals("BUSINESS"))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuViewCourier ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if (profile.equals("COURIER"))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuCouriers ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if ((profile.equals("BUSINESS")) || (profile.equals("COURIER")))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuAllCouriers ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if (profile.equals("BUSINESS"))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuViewDelivery ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if (profile.equals("BUSINESS"))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuAllDeliveries ()
	{
		boolean cond = false;
		if (profile != null)
		{
			if ((profile.equals("BUSINESS")) || (profile.equals("CUSTOMER")) || (profile.equals("COURIER")))
			{
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuExit ()
	{
		final boolean cond = true;
		return cond;
	}

	public boolean getOpenTabsExit ()
	{
		boolean openTabs = false;
		if (getMenuExit())
		{
			openTabs = true;
		}
		return openTabs;
	}

	public BusinessVO getBusinessVO ()
	{
		return businessVO;
	}

	public String getProfile ()
	{
		return profile;
	}

	public String getTimeSystem ()
	{
		return timeSystem;
	}

	public void setUsername (final String username)
	{
		this.username = username;
	}

	public String getUsername ()
	{
		return username;
	}

	public void setPassword (final String password)
	{
		this.password = password;
	}

	public String getPassword ()
	{
		return password;
	}

	public String getNumerSession ()
	{
		return numberSession;
	}

	public boolean isLogged ()
	{
		return logged;
	}

	public Integer getId ()
	{
		return id;
	}

	public void setId (final Integer id)
	{
		this.id = id;
	}

	public boolean isMaster ()
	{
		return master;
	}

	public void setMaster (final boolean master)
	{
		this.master = master;
	}

	public Integer getUserIdLogged ()
	{
		return userIdLogged;
	}
}

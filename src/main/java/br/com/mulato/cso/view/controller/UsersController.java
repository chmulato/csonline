package br.com.mulato.cso.view.controller;

import java.util.List;

import javax.el.ELException;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.UserVO;
import br.com.mulato.cso.view.beans.FacesMessages;

public class UsersController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger(UsersController.class);

	private List<UserVO> results;

	private String parameter;

	private boolean no_items;

	private void loadSession ()
	{
		String msg = "Carregando controle da página de usuários ...";
		LOGGER.info(msg);
		try
		{
			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);
			if (loginController.isLogged())
			{
				LOGGER.info("Sessão carregada! ... Login: " + loginController.getUsername());
				if (loginController.getProfile().equals("ADMINISTRATOR"))
				{
					results = FactoryService.getInstancia().getAdminService().listAllUsers();
					if ((results != null) && (results.size() > 0))
					{
						setResults(results);
					}
					else
					{
						setNo_items(true);
					}
				}
				else
				{
					msg = "Perfil do usuário não encontrado.";
					LOGGER.error(msg);
					throw new WebException(msg);
				}
			}
			else
			{
				msg = "Sessão não carregada! Logar novamente.";
				LOGGER.error(msg);
				throw new WebException(msg);
			}
		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public UsersController ()
	{
		super();
		loadSession();
	}

	public String edit ()
	{
		try
		{
			parameter = super.getParameter("id");
			if ((parameter != null) && (!parameter.equals("")))
			{
				final FacesContext context = FacesContext.getCurrentInstance();
				final Application app = context.getApplication();
				final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);
				if (loginController != null)
				{
					loginController.setId(Integer.parseInt(parameter));
					return goToPage("user");
				}
			}
		}
		catch (NumberFormatException | WebException | ELException e)
		{
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
		return goToPage("users");
	}

	public String listAll ()
	{
		return goToPage("users");
	}

	public List<UserVO> getResults ()
	{
		return results;
	}

	public void setResults (final List<UserVO> results)
	{
		this.results = results;
	}

	public String getParameter ()
	{
		return parameter;
	}

	public void setParameter (final String parameter)
	{
		this.parameter = parameter;
	}

	public boolean isNo_items ()
	{
		return no_items;
	}

	public void setNo_items (final boolean no_items)
	{
		this.no_items = no_items;
	}
}

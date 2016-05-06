package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.util.List;
import javax.el.ELException;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.view.beans.FacesMessages;

@ManagedBean(name = "businessesMB")
@RequestScoped
public class BusinessesController
    extends AbstractController
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(BusinessesController.class);

	private List<BusinessVO> results;

	private String parameter;

	private boolean admin_profile;

	private boolean no_items;

	private void addItem ()
	{

		final FacesContext context = FacesContext.getCurrentInstance();
		final Application app = context.getApplication();
		final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);

		try
		{

			if (loginController.isLogged() == true)
			{
				loginController.setId(null);
			}
			else
			{
				throw new WebException("Sessão não carregada! Logar novamente.");
			}

		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}

	}

	private void loadSession ()
	{

		String profile;

		LOGGER.info("Carregando controle da página de negócios ...");

		try
		{

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);

			if (loginController.isLogged())
			{

				LOGGER.info("Sessão carregada! ... Login: " + loginController.getUsername());

				profile = loginController.getProfile();

				if (profile.equals("ADMINISTRATOR"))
				{

					admin_profile = true;

					results = FactoryService.getInstancia().getBusinessService().listAllBusiness();

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
					throw new WebException("Perfil do usuário não encontrado.");
				}
			}
			else
			{
				throw new WebException("Sessão não carregada! Logar novamente.");
			}

		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public BusinessesController ()
	{
		super();
		loadSession();
	}

	public String add ()
	{
		addItem();
		return goToPage("business");
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
					return goToPage("business");
				}
			}
		}
		catch (NumberFormatException | WebException | ELException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
		return goToPage("businesses");
	}

	public String delete ()
	{
		try
		{
			parameter = super.getParameter("id");
			if ((parameter != null) && (!parameter.equals("")))
			{
				final Integer id = Integer.parseInt(parameter);
				if (id != null)
				{
					FactoryService.getInstancia().getBusinessService().delete(id);
					FacesMessages.mensInfo("Negócio deletado com sucesso!");
				}
			}
			else
			{
				throw new WebException("Parâmetro vazio! Informe id negócio.");
			}
		}
		catch (final NumberFormatException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final Exception e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
		return goToPage("businesses");
	}

	public String listAll ()
	{
		return goToPage("businesses");
	}

	public List<BusinessVO> getResults ()
	{
		return results;
	}

	public void setResults (final List<BusinessVO> results)
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

	public boolean isAdmin_profile ()
	{
		return admin_profile;
	}

	public void setAdmin_profile (final boolean admin_profile)
	{
		this.admin_profile = admin_profile;
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

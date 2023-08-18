package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.view.beans.FacesMessages;

@ManagedBean(name = "couriersMB")
@RequestScoped
public class CouriersController
    extends AbstractController
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CouriersController.class);

	private List<CourierVO> results;

	private String parameter;

	private boolean business_profile;

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
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
	}

	private void loadSession ()
	{
		String profile;
		LOGGER.info("Carregando controle da página de entregadores ...");

		try
		{

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);

			if (loginController.isLogged())
			{
				LOGGER.info("Sessão carregada! ... Login: " + loginController.getUsername());
				profile = loginController.getProfile();
				if (profile.equals("BUSINESS"))
				{
					business_profile = true;
					final BusinessVO business = loginController.getBusinessVO();
					results = FactoryService.getInstancia().getCourierService().listAllCourierBusiness(business);
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
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public CouriersController ()
	{
		super();
		loadSession();
	}

	public String add ()
	{
		addItem();
		return goToPage("courier");
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
					return goToPage("courier");
				}
			}
		}
		catch (final NumberFormatException e)
		{
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final WebException e)
		{
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
		return goToPage("couriers");
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
					FactoryService.getInstancia().getCourierService().delete(id);
					FacesMessages.mensInfo("Entregador deletado com sucesso!");
				}
			}
			else
			{
				throw new WebException("Parâmetro vazio! Informe id entregador.");
			}
		}
		catch (final NumberFormatException e)
		{
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final WebException e)
		{
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
		return goToPage("couriers");
	}

	public String listAll ()
	{
		return goToPage("couriers");
	}

	public List<CourierVO> getResults ()
	{
		return results;
	}

	public void setResults (final List<CourierVO> results)
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

	public boolean isBusiness_profile ()
	{
		return business_profile;
	}

	public void setBusiness_profile (final boolean business_profile)
	{
		this.business_profile = business_profile;
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

package br.com.mulato.cso.view.controller;

import java.util.List;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.view.beans.FacesMessages;

public class CustomersController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger(CustomersController.class);

	private List<CustomerVO> results;

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
				throw new WebException("Sess„o n„o carregada! Logar novamente.");
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

		LOGGER.info("Carregando controle da p·gina de clientes do negÛcio ...");

		try
		{

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);

			if (loginController.isLogged())
			{

				LOGGER.info("Sess„o carregada! ... Login: " + loginController.getUsername());

				profile = loginController.getProfile();

				if (profile.equals("BUSINESS"))
				{

					business_profile = true;

					final BusinessVO business = loginController.getBusinessVO();

					results = FactoryService.getInstancia().getCustomerService().listAllCustomerBusiness(business);

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

					throw new WebException("Perfil do usu·rio n„o encontrado.");

				}

			}
			else
			{

				throw new WebException("Sess„o n„o carregada! Logar novamente.");

			}

		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public CustomersController ()
	{
		super();
		loadSession();
	}

	public String add ()
	{
		addItem();
		return goToPage("customer");
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
					return goToPage("customer");
				}

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

		return goToPage("customers");
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

					FactoryService.getInstancia().getCustomerService().delete(id);

					FacesMessages.mensInfo("Cliente deletado com sucesso!");

				}

			}
			else
			{
				throw new WebException("Par√¢metro vazio! Informe id cliente.");
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

		return goToPage("customers");
	}

	public String listAll ()
	{
		return goToPage("customers");
	}

	public List<CustomerVO> getResults ()
	{
		return results;
	}

	public void setResults (final List<CustomerVO> results)
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

package br.com.mulato.cso.view.controller;

import java.util.List;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.PriceVO;
import br.com.mulato.cso.view.beans.FacesMessages;

public class PricesController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger(PricesController.class);

	private List<PriceVO> results;

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
			FacesMessages.mensErro(e.getMessage());
		}

	}

	private void loadSession ()
	{

		String profile;

		LOGGER.info("Carregando controle da página de entregas em aberto ...");

		try
		{

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);

			if (loginController.isLogged())
			{

				LOGGER.info("Sessão carregada! ... Login: " + loginController.getUsername());

				if ((loginController.getUserIdLogged() == null) || (loginController.getUserIdLogged().intValue() <= 0))
				{

					throw new WebException("Id do usuário não encontrado.");

				}

				profile = loginController.getProfile();

				if (profile.equals("BUSINESS"))
				{

					business_profile = true;

					final Integer idBusiness = loginController.getUserIdLogged();

					results = FactoryService.getInstancia().getBusinessService().listAllPriceBusiness(idBusiness);

				}
				else
				{

					throw new WebException("Perfil do usuário não encontrado.");

				}

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

				throw new WebException("Sessão não carregada! Logar novamente.");

			}

		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public PricesController ()
	{
		super();
		loadSession();
	}

	public String add ()
	{

		String page = "";

		if (business_profile)
		{

			page = "pricetable";

		}

		addItem();

		return goToPage(page);

	}

	public String edit ()
	{

		String page = "pricetables";

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

					if (business_profile)
					{

						page = "pricetable";

					}

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

		return goToPage(page);
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

					FactoryService.getInstancia().getDeliveryService().delete(id);

					FacesMessages.mensInfo("Valor deletado com sucesso!");

				}

			}
			else
			{
				throw new WebException("Parâmetro vazio! Informe id da preço.");
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

		return goToPage("pricetables");
	}

	public String listAll ()
	{
		return goToPage("pricetables");
	}

	public List<PriceVO> getResults ()
	{
		return results;
	}

	public void setResults (final List<PriceVO> results)
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

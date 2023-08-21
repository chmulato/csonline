package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.view.beans.FacesMessages;

public class DeliveriesCompletedController extends AbstractController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(DeliveriesCompletedController.class);

	private List<DeliveryVO> results;

	private String parameter;

	private boolean business_profile;

	private boolean customer_profile;

	private boolean courier_profile;

	private boolean no_items;

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

					results = FactoryService.getInstancia().getDeliveryService().listAllDeliveryBusinessCompleted(idBusiness);

				}
				else if (profile.equals("CUSTOMER"))
				{

					customer_profile = true;

					final Integer idCustomer = loginController.getUserIdLogged();

					results = FactoryService.getInstancia().getDeliveryService().listAllDeliveryCustomerCompleted(idCustomer);

				}
				else if (profile.equals("COURIER"))
				{

					courier_profile = true;

					final Integer idCourier = loginController.getUserIdLogged();

					results = FactoryService.getInstancia().getDeliveryService().listAllDeliveryCourierCompleted(idCourier);

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

	public DeliveriesCompletedController ()
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
					return goToPage("delivery_view");

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

		return goToPage("deliveries_completed");
	}

	public String listAll ()
	{
		return goToPage("deliveries_completed");
	}

	public List<DeliveryVO> getResults ()
	{
		return results;
	}

	public void setResults (final List<DeliveryVO> results)
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

	public boolean isCustomer_profile ()
	{
		return customer_profile;
	}

	public void setCustomer_profile (final boolean customer_profile)
	{
		this.customer_profile = customer_profile;
	}

	public boolean isCourier_profile ()
	{
		return courier_profile;
	}

	public void setCourier_profile (final boolean courier_profile)
	{
		this.courier_profile = courier_profile;
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

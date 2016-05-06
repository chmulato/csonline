package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.model.SmsVO;
import br.com.mulato.cso.view.beans.FacesMessages;

@ManagedBean(name = "smsMB")
@RequestScoped
public class SMSController
    extends AbstractController
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(SMSController.class);

	private List<SmsVO> results;

	private String parameter;

	private boolean business_profile;

	private boolean no_items;

	private String label;

	private CourierVO courier;

	private DeliveryVO delivery;

	private void loadSession ()
	{
		String profile;
		LOGGER.info("Carregando controle da página de mensagens ...");
		try
		{

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);
			if (loginController.isLogged())
			{
				LOGGER.info("Sessão carregada! ... Login: " + loginController.getUsername());
				if ((loginController.getUserIdLogged() == null) || (loginController.getUserIdLogged() <= 0))
				{
					throw new WebException("Id do usuário não encontrado.");
				}
				if ((loginController.getId() == null) || (loginController.getId() <= 0))
				{
					throw new WebException("Id do entregador não encontrado.");
				}
				profile = loginController.getProfile();
				if (profile.equals("BUSINESS"))
				{
					business_profile = true;
					final Integer idCourier = loginController.getId();
					courier = FactoryService.getInstancia().getCourierService().find(idCourier);
					if (courier != null)
					{
						label = courier.getName() + " - Celular No " + courier.getMobile();
						results = FactoryService.getInstancia().getDeliveryService().listAllCourierSMS(courier);
					}
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

	public SMSController ()
	{
		super();
		loadSession();
	}

	public void editEvent (final AjaxBehaviorEvent event)
	{
		final FacesMessage msg = new FacesMessage("Mensagem atualizada. Clique em salvar!");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void cancelEvent (final AjaxBehaviorEvent event)
	{
		final FacesMessage msg = new FacesMessage("Atualização cancelada!");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public String cancel ()
	{
		return goToPage("deliveries");
	}

	public String save ()
	{
		int count = 0;
		boolean saveOK = false;
		String path = "deliveries";
		if ((getResults() != null) && (getResults().size() > 0))
		{
			try
			{
				for (final SmsVO sms : getResults())
				{
					if (sms.getDelivery() != null)
					{
						if (sms.getDelivery().getId() > 0)
						{
							FactoryService.getInstancia().getDeliveryService().saveSMSDelivery(sms);
							count = count + 1;
							saveOK = true;
						}
					}
				}
				if (saveOK)
				{
					if (count > 0)
					{
						FacesMessages.mensInfo("Mensagem salva com sucesso!");
					}
					else if (count > 1)
					{
						FacesMessages.mensInfo("Total de " + count + " mensagens salvas com sucesso!");
					}
				}
			}
			catch (final WebException e)
			{
				path = "messages";
				FacesMessages.mensErro(e.getMessage());
			}
			catch (final Exception e)
			{
				path = "messages";
				FacesMessages.mensErro("Falha na inserção no banco de dados!");
			}
		}
		return goToBackPage(path);
	}

	/**
	 * Buscar todos os entregas em aberto do entregador
	 * 
	 * @return
	 * @throws WebException
	 */
	public Map<String, Object> getDeliveries () throws WebException
	{
		final Map<String, Object> itemMap = new LinkedHashMap<>();
		if ((courier != null) && (courier.getId() != null))
		{
			final List<DeliveryVO> listDeliveries = FactoryService.getInstancia().getDeliveryService().listAllDeliveryCourierNotCompleted(
			    courier.getId());
			if ((listDeliveries != null) && (listDeliveries.size() > 0))
			{
				for (final DeliveryVO delivery : listDeliveries)
				{
					// label, value
					itemMap.put(delivery.getId() + "- " + delivery.getDestination(), delivery);
				}
			}
		}
		return itemMap;
	}

	public List<SmsVO> getResults ()
	{
		return results;
	}

	public void setResults (final List<SmsVO> results)
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

	public String getLabel ()
	{
		return label;
	}

	public void setLabel (final String label)
	{
		this.label = label;
	}

	public DeliveryVO getDelivery ()
	{
		return delivery;
	}

	public void setDelivery (final DeliveryVO delivery)
	{
		this.delivery = delivery;
	}
}

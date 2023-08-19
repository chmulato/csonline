package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.model.SmsVO;
import br.com.mulato.cso.view.beans.FacesMessages;

@ManagedBean(name = "deliveryMB")
@RequestScoped
public class DeliveryController
    extends AbstractController
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(DeliveryController.class);

	private BusinessVO businessVO;

	private String label;

	private Integer id;

	private Integer idBusiness;

	private String business_name;

	private Integer idCustomer;

	private String customer_name;

	private Integer idCourier;

	private String courier_name;

	private Date datetime;

	private String start;

	private String destination;

	private String contact;

	private String description;

	private BigDecimal volume;

	private BigDecimal weight;

	private BigDecimal km;

	private BigDecimal additionalCost;

	private BigDecimal cost;

	private String txtSms;

	private boolean completed;

	private boolean received;

	private boolean insert;

	private boolean send_mail;

	private boolean send_sms;

	private void loadSession ()
	{

		String profile;

		boolean isLogged = false;

		LOGGER.info("Carregando controle da página de entregas do negócio ...");

		try
		{

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);

			isLogged = loginController.isLogged();

			if (isLogged)
			{

				LOGGER.info("Sessão carregada! ... Login: " + loginController.getUsername());

				profile = loginController.getProfile();

				if ((loginController.getUserIdLogged() == null) || (loginController.getUserIdLogged().intValue() <= 0))
				{

					throw new WebException("Id do usuário logado não encontrado.");

				}

				if ((loginController.getBusinessVO() == null) || (loginController.getBusinessVO().getId() == null) ||
				    (loginController.getBusinessVO().getId().intValue() <= 0))
				{

					throw new WebException("Negócio da sessão não encontrado.");

				}
				else
				{

					businessVO = loginController.getBusinessVO();

				}

				if (profile.equals("BUSINESS"))
				{

					// Edit Delivery

					label = "Editar";

					if (loginController.getId() != null)
					{

						final Integer id = loginController.getId();

						final DeliveryVO delivery = FactoryService.getInstancia().getDeliveryService().find(id);

						if (delivery != null)
						{

							setId(delivery.getId());

							if ((delivery.getBusiness() != null) && (delivery.getBusiness().getId() != null) &&
							    (delivery.getBusiness().getName() != null))
							{
								setIdBusiness(delivery.getBusiness().getId());
								setBusiness_name(delivery.getBusiness().getName());
							}

							if ((delivery.getCustomer() != null) && (delivery.getCustomer().getId() != null) &&
							    (delivery.getCustomer().getName() != null))
							{
								setIdCustomer(delivery.getCustomer().getId());
								setCustomer_name(delivery.getCustomer().getName());
							}

							if ((delivery.getCourier() != null) && (delivery.getCourier().getId() != null) &&
							    (delivery.getCourier().getName() != null))
							{
								setIdCourier(delivery.getCourier().getId());
								setCourier_name(delivery.getCourier().getName());
							}

							setDatetime(delivery.getDatetime());
							setStart(delivery.getStart());
							setDestination(delivery.getDestination());
							setContact(delivery.getContact());
							setDescription(delivery.getDescription());
							setVolume(delivery.getVolume());
							setWeight(delivery.getWeight());
							setKm(delivery.getKm());
							setAdditionalCost(delivery.getAdditionalCost());
							setCost(delivery.getCost());
							setCompleted(delivery.getCompleted());
							setReceived(delivery.getReceived());

						}

					}
					else
					{
						// Add New Delivery

						label = "Incluir";

						insert = true;

						setIdBusiness(businessVO.getId());
						setBusiness_name(businessVO.getName());

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

	public String send ()
	{
		send_mail = true;
		return save();
	}

	public String sendSms ()
	{

		boolean error = false;

		final String path = "delivery";

		send_sms = true;

		try
		{

			if (getTxtSms() == null)
			{
				throw new WebException("Escreva a mensagem SMS!");
			}

			if (getTxtSms().equals(""))
			{
				throw new WebException("Escreva a mensagem SMS!");
			}

		}
		catch (final WebException e)
		{
			error = true;
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final Exception e)
		{
			error = true;
			FacesMessages.mensErro("Escreva a mensagem SMS!");
		}

		if (error)
		{
			send_sms = false;
			return goToBackPage(path);
		}
		else
		{
			return save();
		}

	}

	public String save ()
	{

		String path = "delivery";

		boolean insert = false;

		int business_id = 0;
		int customer_id = 0;
		int courier_id = 0;

		try
		{

			if ((getId() == null) || getId().equals(new Integer(0)))
			{
				insert = true;
			}

			if ((getIdBusiness() == null) || getIdBusiness().equals(new Integer(0)))
			{
				throw new WebException("Informe id negócio!");
			}

			if ((getIdCustomer() == null) || getIdCustomer().equals(new Integer(0)))
			{
				throw new WebException("Selecione cliente!");
			}

			if ((getIdCourier() == null) || getIdCourier().equals(new Integer(0)))
			{
				throw new WebException("Selecione entregador!");
			}

			if (getStart() == null)
			{
				throw new WebException("Informe endereço de iní­cio da corrida!");
			}

			if (getStart().equals(""))
			{
				throw new WebException("Informe endereço de iní­cio da corrida!");
			}

			if (getDestination() == null)
			{
				throw new WebException("Informe endereço de destino!");
			}

			if (getDestination().equals(""))
			{
				throw new WebException("Informe endereço de destino!");
			}

			if (getContact() == null)
			{
				throw new WebException("Informe o contato!");
			}

			if (getContact().equals(""))
			{
				throw new WebException("Informe o contato!");
			}

			if (getDescription() == null)
			{
				throw new WebException("Informe descriçío!");
			}

			if (getDescription().equals(""))
			{
				throw new WebException("Informe descrição!");
			}

			if (getKm() == null)
			{
				throw new WebException("Informe distância em quilômetros!");
			}

			if (getKm().equals(new BigDecimal(0)))
			{
				throw new WebException("Informe distância em quilômetros!");
			}

			if (getCost() == null)
			{
				throw new WebException("Informe valor da corrida!");
			}

			if (getCost().equals(new BigDecimal(0)))
			{
				throw new WebException("Informe valor da corrida!");
			}

			if (getCompleted() == true)
			{
				if (getReceived() == false)
				{
					throw new WebException("Primeiramente, inicie a corrida da entrega!");
				}
			}

			final DeliveryVO delivery = new DeliveryVO();

			if (insert)
			{
				delivery.setId(null);
			}
			else
			{
				delivery.setId(getId());
			}

			business_id = Integer.parseInt(getIdBusiness().toString());
			customer_id = Integer.parseInt(getIdCustomer().toString());
			courier_id = Integer.parseInt(getIdCourier().toString());

			final BusinessVO business = new BusinessVO();
			business.setId(business_id);
			delivery.setBusiness(business);

			final CustomerVO customer = new CustomerVO();
			customer.setId(customer_id);
			delivery.setCustomer(customer);

			final CourierVO courier = new CourierVO();
			courier.setId(courier_id);
			delivery.setCourier(courier);

			delivery.setStart(getStart());
			delivery.setDestination(getDestination());
			delivery.setContact(getContact());
			delivery.setDescription(getDescription());
			delivery.setVolume(getVolume());
			delivery.setWeight(getWeight());
			delivery.setKm(getKm());
			delivery.setAdditionalCost(getAdditionalCost());
			delivery.setCost(getCost());

			if ((send_mail) || (send_sms))
			{
				delivery.setReceived(true);
				delivery.setCompleted(false);
			}
			else
			{
				delivery.setReceived(getReceived());
				delivery.setCompleted(getCompleted());
			}

			if (insert)
			{
				delivery.setCompleted(false);
			}

			if (send_sms)
			{
				final SmsVO sms = new SmsVO();
				sms.setMessage(getTxtSms());
				sms.setType('S');
				delivery.setSendSMS(sms);
			}

			FactoryService.getInstancia().getDeliveryService().saveDeliveryByBusiness(delivery, send_mail, send_sms);

			if (send_mail)
			{
				path = "deliveries";
				FacesMessages.mensInfo("Email enviado com sucesso!");
			}
			else
			{
				if (send_sms)
				{
					path = "deliveries";
					FacesMessages.mensInfo("SMS enviado com sucesso!");
				}
				else
				{
					FacesMessages.mensInfo("Entrega salva com sucesso!");
				}
			}

		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final Exception e)
		{
			FacesMessages.mensErro("Falha na inserção no banco de dados!");
		}

		return goToBackPage(path);

	}

	public DeliveryController ()
	{
		super();
		loadSession();
	}

	/**
	 * Buscar todos os entregadores do negócio
	 * 
	 * @return
	 * @throws WebException
	 */
	public Map<String, Object> getCouriers () throws WebException
	{

		final Map<String, Object> itemMap = new LinkedHashMap<String, Object>();

		if ((businessVO != null) && (businessVO.getId() != null))
		{

			final List<CourierVO> listCouriers = FactoryService.getInstancia().getCourierService().listAllCourierBusiness(businessVO);

			for (final CourierVO courier : listCouriers)
			{

				// label, value
				itemMap.put(courier.getName(), courier.getId());
			}

		}

		return itemMap;

	}

	/**
	 * Buscar todos os clientes do negócio
	 * 
	 * @return
	 * @throws WebException
	 */
	public Map<String, Object> getCustomers () throws WebException
	{

		final Map<String, Object> itemMap = new LinkedHashMap<String, Object>();

		if ((businessVO != null) && (businessVO.getId() != null))
		{

			final List<CustomerVO> listCustomers = FactoryService.getInstancia().getCustomerService().listAllCustomerBusiness(businessVO);

			for (final CustomerVO customer : listCustomers)
			{

				// label, value
				itemMap.put(customer.getName(), customer.getId());
			}

		}

		return itemMap;

	}

	public String cancel ()
	{
		return goToPage("deliveries");
	}

	public String getLabel ()
	{
		return label;
	}

	public void setLabel (final String label)
	{
		this.label = label;
	}

	public Integer getId ()
	{
		return id;
	}

	public void setId (final Integer id)
	{
		this.id = id;
	}

	public Integer getIdBusiness ()
	{
		return idBusiness;
	}

	public void setIdBusiness (final Integer idBusiness)
	{
		this.idBusiness = idBusiness;
	}

	public String getBusiness_name ()
	{
		return business_name;
	}

	public void setBusiness_name (final String business_name)
	{
		this.business_name = business_name;
	}

	public Integer getIdCustomer ()
	{
		return idCustomer;
	}

	public void setIdCustomer (final Integer idCustomer)
	{
		this.idCustomer = idCustomer;
	}

	public String getCustomer_name ()
	{
		return customer_name;
	}

	public void setCustomer_name (final String customer_name)
	{
		this.customer_name = customer_name;
	}

	public Integer getIdCourier ()
	{
		return idCourier;
	}

	public void setIdCourier (final Integer idCourier)
	{
		this.idCourier = idCourier;
	}

	public String getCourier_name ()
	{
		return courier_name;
	}

	public void setCourier_name (final String courier_name)
	{
		this.courier_name = courier_name;
	}

	public Date getDatetime ()
	{
		return datetime;
	}

	public void setDatetime (final Date datetime)
	{
		this.datetime = datetime;
	}

	public String getStart ()
	{
		return start;
	}

	public void setStart (final String start)
	{
		this.start = start;
	}

	public String getDestination ()
	{
		return destination;
	}

	public void setDestination (final String destination)
	{
		this.destination = destination;
	}

	public String getContact ()
	{
		return contact;
	}

	public void setContact (final String contact)
	{
		this.contact = contact;
	}

	public String getDescription ()
	{
		return description;
	}

	public void setDescription (final String description)
	{
		this.description = description;
	}

	public BigDecimal getVolume ()
	{
		return volume;
	}

	public void setVolume (final BigDecimal volume)
	{
		this.volume = volume;
	}

	public BigDecimal getWeight ()
	{
		return weight;
	}

	public void setWeight (final BigDecimal weight)
	{
		this.weight = weight;
	}

	public BigDecimal getKm ()
	{
		return km;
	}

	public void setKm (final BigDecimal km)
	{
		this.km = km;
	}

	public BigDecimal getAdditionalCost ()
	{
		return additionalCost;
	}

	public void setAdditionalCost (final BigDecimal additionalCost)
	{
		this.additionalCost = additionalCost;
	}

	public BigDecimal getCost ()
	{
		return cost;
	}

	public void setCost (final BigDecimal cost)
	{
		this.cost = cost;
	}

	public String getTxtSms ()
	{

		boolean nonEmpty = false;

		String result = "";
		String txtCustomerValue = "   ";
		String txtContactValue = "   ";
		String txtStartValue = "   ";
		String txtDestinationValue = "   ";
		String txtDescriptionValue = "   ";
		String txtKmValue = "   ";

		try
		{

			if (getIdCustomer() != null)
			{
				if ((getCustomers() != null) && (getCustomers().size() > 0))
				{
					final Map<String, Object> map = getCustomers();
					for (final Entry<String, Object> entry : map.entrySet())
					{
						if (entry.getValue().equals(getIdCustomer()))
						{
							txtCustomerValue = entry.getKey();
							nonEmpty = true;
							break;
						}
					}
				}
			}

		}
		catch (final WebException e)
		{
			txtCustomerValue = "Não Encontrado!";
		}

		if (getContact() != null)
		{
			txtContactValue = getContact();
			nonEmpty = true;
		}

		if (getStart() != null)
		{
			txtStartValue = getStart();
			nonEmpty = true;
		}

		if (getDestination() != null)
		{
			txtDestinationValue = getDestination();
			nonEmpty = true;
		}

		if (getDescription() != null)
		{
			txtDescriptionValue = getDescription();
			nonEmpty = true;
		}

		if (getKm() != null)
		{
			txtKmValue = km.setScale(2, RoundingMode.HALF_DOWN).toString();
			txtKmValue = txtKmValue.replace(".", ",");
			nonEmpty = true;
		}

		if (nonEmpty)
		{
			result = " Cliente: " + txtCustomerValue + " Contato: " + txtContactValue + " Base: " + txtStartValue + " Dest: " + txtDestinationValue +
			    " Obs: " + txtDescriptionValue + " Km: " + txtKmValue;
		}

		txtSms = result;

		return txtSms;
	}

	public void setTxtSms (final String txtSms)
	{
		this.txtSms = txtSms;
	}

	public boolean getCompleted ()
	{
		return completed;
	}

	public void setCompleted (final boolean completed)
	{
		this.completed = completed;
	}

	public boolean getReceived ()
	{
		return received;
	}

	public void setReceived (final boolean received)
	{
		this.received = received;
	}

	public boolean isInsert ()
	{
		return insert;
	}

	public void setInsert (final boolean insert)
	{
		this.insert = insert;
	}
}

package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.view.beans.FacesMessages;

public class DeliveryViewController extends AbstractController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(DeliveryViewController.class);

	private BusinessVO businessVO;

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

	private boolean completed;

	private boolean received;

	private void loadSession ()
	{

		String profile;

		boolean isLogged = false;

		LOGGER.info("Carregando controle da p�gina de entregas do neg�cio ...");

		try
		{

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);

			isLogged = loginController.isLogged();

			if (isLogged)
			{

				LOGGER.info("Sess�o carregada! ... Login: " + loginController.getUsername());

				profile = loginController.getProfile();

				if ((loginController.getUserIdLogged() == null) || (loginController.getUserIdLogged().intValue() <= 0))
				{

					throw new WebException("Id do usu�rio logado n�o encontrado.");

				}

				if ((loginController.getBusinessVO() == null) || (loginController.getBusinessVO().getId() == null) ||
				    (loginController.getBusinessVO().getId().intValue() <= 0))
				{

					throw new WebException("Neg�cio da sess�o n�o encontrado.");

				}
				else
				{

					businessVO = loginController.getBusinessVO();

				}

				if ((profile.equals("BUSINESS")) || (profile.equals("CUSTOMER")) || (profile.equals("COURIER")))
				{

					// Edit Delivery

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

				}
				else
				{

					throw new WebException("Perfil do usu�rio n�o encontrado.");

				}

			}
			else
			{

				throw new WebException("Sess�o n�o carregada! Logar novamente.");

			}

		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public DeliveryViewController ()
	{
		super();
		loadSession();
	}

	/**
	 * Buscar todos os entregadores do neg�cio
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
	 * Buscar todos os clientes do neg�cio
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
		return goToPage("deliveries_completed");
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
}

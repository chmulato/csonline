package br.com.mulato.cso.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DeliveryVO
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private Integer id;

	private BusinessVO business;

	private CourierVO courier;

	private CustomerVO customer;

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

	private boolean received;

	private boolean completed;

	private List<SmsVO> listSMS;

	private SmsVO sendSMS;

	private PriceVO price;

	public DeliveryVO ()
	{
	}

	public Integer getId ()
	{
		return id;
	}

	public void setId (final Integer id)
	{
		this.id = id;
	}

	public BusinessVO getBusiness ()
	{
		return business;
	}

	public void setBusiness (final BusinessVO business)
	{
		this.business = business;
	}

	public CourierVO getCourier ()
	{
		return courier;
	}

	public void setCourier (final CourierVO courier)
	{
		this.courier = courier;
	}

	public CustomerVO getCustomer ()
	{
		return customer;
	}

	public void setCustomer (final CustomerVO customer)
	{
		this.customer = customer;
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

	public boolean getReceived ()
	{
		return received;
	}

	public void setReceived (final boolean received)
	{
		this.received = received;
	}

	public boolean getCompleted ()
	{
		return completed;
	}

	public void setCompleted (final boolean completed)
	{
		this.completed = completed;
	}

	public List<SmsVO> getListSMS ()
	{
		return listSMS;
	}

	public void setListSMS (final List<SmsVO> listSMS)
	{
		this.listSMS = listSMS;
	}

	public SmsVO getSendSMS ()
	{
		return sendSMS;
	}

	public void setSendSMS (final SmsVO sendSMS)
	{
		this.sendSMS = sendSMS;
	}

	public PriceVO getPrice ()
	{
		return price;
	}

	public void setPrice (final PriceVO price)
	{
		this.price = price;
	}
}

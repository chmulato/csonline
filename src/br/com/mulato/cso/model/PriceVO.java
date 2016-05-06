package br.com.mulato.cso.model;

import java.math.BigDecimal;

public class PriceVO
{

	private Integer id;

	private BusinessVO business;

	private String table;

	private String vehicle;

	private String local;

	private BigDecimal price;

	public PriceVO ()
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

	public String getTable ()
	{
		return table;
	}

	public void setTable (final String table)
	{
		this.table = table;
	}

	public String getVehicle ()
	{
		return vehicle;
	}

	public void setVehicle (final String vehicle)
	{
		this.vehicle = vehicle;
	}

	public String getLocal ()
	{
		return local;
	}

	public void setLocal (final String local)
	{
		this.local = local;
	}

	public BigDecimal getPrice ()
	{
		return price;
	}

	public void setPrice (final BigDecimal price)
	{
		this.price = price;
	}
}

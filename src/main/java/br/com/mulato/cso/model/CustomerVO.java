package br.com.mulato.cso.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CustomerVO
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String role;

	private String name;

	private LoginVO login;

	private String email;

	private String email2;

	private String address;

	private String mobile;

	private BigDecimal factor_customer;

	private String price_table;

	private BusinessVO business;

	private List<DeliveryVO> deliveries;

	private List<PriceVO> prices;

	public CustomerVO ()
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

	public String getRole ()
	{
		return role;
	}

	public void setRole (final String role)
	{
		this.role = role;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (final String name)
	{
		this.name = name;
	}

	public LoginVO getLogin ()
	{
		return login;
	}

	public void setLogin (final LoginVO login)
	{
		this.login = login;
	}

	public String getEmail ()
	{
		return email;
	}

	public void setEmail (final String email)
	{
		this.email = email;
	}

	public String getEmail2 ()
	{
		return email2;
	}

	public void setEmail2 (final String email2)
	{
		this.email2 = email2;
	}

	public String getAddress ()
	{
		return address;
	}

	public void setAddress (final String address)
	{
		this.address = address;
	}

	public String getMobile ()
	{
		return mobile;
	}

	public void setMobile (final String mobile)
	{
		this.mobile = mobile;
	}

	public BigDecimal getFactor_customer ()
	{
		return factor_customer;
	}

	public void setFactor_customer (final BigDecimal factor_customer)
	{
		this.factor_customer = factor_customer;
	}

	public String getPrice_table ()
	{
		return price_table;
	}

	public void setPrice_table (final String price_table)
	{
		this.price_table = price_table;
	}

	public BusinessVO getBusiness ()
	{
		return business;
	}

	public void setBusiness (final BusinessVO business)
	{
		this.business = business;
	}

	public List<DeliveryVO> getDeliveries ()
	{
		return deliveries;
	}

	public void setDeliveries (final List<DeliveryVO> deliveries)
	{
		this.deliveries = deliveries;
	}

	public List<PriceVO> getPrices ()
	{
		return prices;
	}

	public void setPrices (final List<PriceVO> prices)
	{
		this.prices = prices;
	}
}

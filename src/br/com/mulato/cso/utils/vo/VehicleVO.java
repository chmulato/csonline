package br.com.mulato.cso.utils.vo;

import java.io.Serializable;

public class VehicleVO
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String vehicle;

	public Integer getId ()
	{
		return id;
	}

	public void setId (final Integer id)
	{
		this.id = id;
	}

	public String getVehicle ()
	{
		return vehicle;
	}

	public void setVehicle (final String vehicle)
	{
		this.vehicle = vehicle;
	}
}

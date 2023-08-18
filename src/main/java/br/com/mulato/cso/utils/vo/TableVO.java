package br.com.mulato.cso.utils.vo;

import java.io.Serializable;

public class TableVO
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String name;

	public Integer getId ()
	{
		return id;
	}

	public void setId (final Integer id)
	{
		this.id = id;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (final String name)
	{
		this.name = name;
	}
}

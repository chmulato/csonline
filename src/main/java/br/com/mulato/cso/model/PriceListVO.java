package br.com.mulato.cso.model;

import java.util.List;

public class PriceListVO
{

	private String table;

	private List<PriceVO> list;

	public PriceListVO ()
	{
	}

	public String getTable ()
	{
		return table;
	}

	public void setTable (final String table)
	{
		this.table = table;
	}

	public List<PriceVO> getList ()
	{
		return list;
	}

	public void setList (final List<PriceVO> list)
	{
		this.list = list;
	}
}

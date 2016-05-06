package br.com.mulato.cso.utils.vo;

import java.io.Serializable;

/**
 * @author Christian Mulato
 */
public class DocsVO
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private Long quantidade;

	private String data;

	public DocsVO ()
	{
	}

	public Long getQuantidade ()
	{
		return quantidade;
	}

	public void setQuantidade (final Long quantidade)
	{
		this.quantidade = quantidade;
	}

	public String getData ()
	{
		return data;
	}

	public void setData (final String data)
	{
		this.data = data;
	}
}

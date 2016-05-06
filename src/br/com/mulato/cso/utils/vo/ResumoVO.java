package br.com.mulato.cso.utils.vo;

import java.io.Serializable;

/**
 * @author Christian Mulato
 */
public class ResumoVO
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private Integer num;

	private Double valor01;

	private Double valor02;

	public ResumoVO ()
	{
		super();
	}

	public Integer getNum ()
	{
		return num;
	}

	public void setNum (final Integer num)
	{
		this.num = num;
	}

	public Double getValor01 ()
	{
		return valor01;
	}

	public void setValor01 (final Double valor01)
	{
		this.valor01 = valor01;
	}

	public Double getValor02 ()
	{
		return valor02;
	}

	public void setValor02 (final Double valor02)
	{
		this.valor02 = valor02;
	}
}

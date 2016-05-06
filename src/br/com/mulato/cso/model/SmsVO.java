package br.com.mulato.cso.model;

import java.util.Date;

public class SmsVO
{

	private Integer id;

	private Integer idDelivery;

	private DeliveryVO delivery;

	private String to;

	private String from;

	private short piece;

	private char type;

	private Date datetime;

	private String message;

	public SmsVO ()
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

	public Integer getIdDelivery ()
	{
		return idDelivery;
	}

	public void setIdDelivery (final Integer idDelivery)
	{
		this.idDelivery = idDelivery;
	}

	public DeliveryVO getDelivery ()
	{
		return delivery;
	}

	public void setDelivery (final DeliveryVO delivery)
	{
		this.delivery = delivery;
	}

	public String getTo ()
	{
		return to;
	}

	public void setTo (final String to)
	{
		this.to = to;
	}

	public String getFrom ()
	{
		return from;
	}

	public void setFrom (final String from)
	{
		this.from = from;
	}

	public short getPiece ()
	{
		return piece;
	}

	public void setPiece (final short piece)
	{
		this.piece = piece;
	}

	public char getType ()
	{
		return type;
	}

	public void setType (final char type)
	{
		this.type = type;
	}

	public Date getDatetime ()
	{
		return datetime;
	}

	public void setDatetime (final Date datetime)
	{
		this.datetime = datetime;
	}

	public String getMessage ()
	{
		return message;
	}

	public void setMessage (final String message)
	{
		this.message = message;
	}
}

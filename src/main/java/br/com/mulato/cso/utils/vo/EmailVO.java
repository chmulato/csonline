package br.com.mulato.cso.utils.vo;

import java.io.Serializable;
import java.util.Date;

public class EmailVO
    implements Comparable<EmailVO>, Serializable
{

	private static final long serialVersionUID = 1L;

	private String from;

	private Date data;

	private String subject;

	private String content;

	public String getFrom ()
	{
		return from;
	}

	public void setFrom (final String from)
	{
		this.from = from;
	}

	public Date getData ()
	{
		return data;
	}

	public void setData (final Date data)
	{
		this.data = data;
	}

	public String getSubject ()
	{
		return subject;
	}

	public void setSubject (final String subject)
	{
		this.subject = subject;
	}

	public String getContent ()
	{
		return content;
	}

	public void setContent (final String content)
	{
		this.content = content;
	}

	/**
	 * Compare a given EmailVO with this object.
	 * If 'data' of this object is
	 * greater than the 'data' of received object,
	 * then this object is greater than the other.
	 * 
	 * @param emailVO
	 */
	@Override
	public int compareTo (final EmailVO emailVO)
	{
		int result = 0;
		if (data != null)
		{
			if (emailVO.getData() != null)
			{
				if (this.data.getTime() > emailVO.getData().getTime())
				{
					result = 1;
				}
			}
		}
		return result;
	}

}

package br.com.mulato.cso.exception;

public class WebException
    extends Exception
{

	private static final long serialVersionUID = 1L;
	private String message;

	public WebException (final String message)
	{
		this.message = message;
	}

	@Override
	public String getMessage ()
	{
		return message;
	}

	public void setMessage (final String message)
	{
		this.message = message;
	}
}

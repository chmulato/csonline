package br.com.mulato.cso.exception;

public class DAOException
    extends WebException
{

	private static final long serialVersionUID = 1L;
	private String message;

	public DAOException (final String message)
	{
		super(message);
		this.message = message;
	}

	@Override
	public String getMessage ()
	{
		return message;
	}

	@Override
	public void setMessage (final String message)
	{
		this.message = message;
	}
}

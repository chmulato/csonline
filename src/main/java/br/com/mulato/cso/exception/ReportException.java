package br.com.mulato.cso.exception;

public class ReportException
    extends Exception
{

	private static final long serialVersionUID = 1L;
	private String message;

	public ReportException (final String message)
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

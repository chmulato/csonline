package br.com.mulato.cso.model;

import java.io.Serializable;

public class LoginVO
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private String login;

	private String password;

	private String repeat;

	private String newPassword;

	private String newRepeat;

	private String email;

	public LoginVO ()
	{
	}

	public LoginVO (final String login, final String password)
	{
		this.login = login;
		this.password = password;
	}

	public String getLogin ()
	{
		return login;
	}

	public void setLogin (final String login)
	{
		this.login = login;
	}

	public String getPassword ()
	{
		return password;
	}

	public void setPassword (final String password)
	{
		this.password = password;
	}

	public String getRepeat ()
	{
		return repeat;
	}

	public void setRepeat (final String repeat)
	{
		this.repeat = repeat;
	}

	public String getNewPassword ()
	{
		return newPassword;
	}

	public void setNewPassword (final String newPassword)
	{
		this.newPassword = newPassword;
	}

	public String getNewRepeat ()
	{
		return newRepeat;
	}

	public void setNewRepeat (final String newRepeat)
	{
		this.newRepeat = newRepeat;
	}

	public String getEmail ()
	{
		return email;
	}

	public void setEmail (final String email)
	{
		this.email = email;
	}
}

package br.com.mulato.cso.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author Christian Mulato
 */
class AuthenticationEmail
    extends Authenticator
{

	public String username = null;
	public String password = null;

	public AuthenticationEmail (final String user, final String pwd)
	{
		username = user;
		password = pwd;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication ()
	{
		return new PasswordAuthentication(username, password);
	}
}

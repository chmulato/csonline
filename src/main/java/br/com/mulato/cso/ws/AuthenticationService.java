package br.com.mulato.cso.ws;

import java.io.IOException;
import java.util.StringTokenizer;
import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.model.LoginVO;

public class AuthenticationService
{

	private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class);

	public boolean authenticate (final String authCredentials)
	{

		if (null == authCredentials)
		{
			LOGGER.error("Modelo de autenticaÃ§Ã£o invÃ¡lido! Objeto authCredentials=" + authCredentials);
			return false;
		}

		// header value format will be "Basic encodedstring" for Basic
		// authentication. Example "Basic YWRtaW46YWRtaW4="
		final String encodedUserPassword = authCredentials.replaceFirst("Basic" + " ", "");
		String usernameAndPassword = null;
		try
		{
			final byte[] decodedBytes = DatatypeConverter.parseBase64Binary(encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
		}
		catch (final IOException e)
		{
			LOGGER.error("Error: " + e.getMessage());
		}
		final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();
		boolean authenticationStatus = false;
		if ((username != null) && (password != null))
		{
			if ((!username.equals("")) && (!password.equals("")))
			{
				final LoginVO login = new LoginVO(username, password);
				try
				{
					final Boolean authenticate = FactoryService.getInstancia().getLoginService().authenticate(login);
					if (authenticate != null)
					{
						authenticationStatus = authenticate;
						if (authenticationStatus)
						{
							LOGGER.info("Autenticação do usuário com sucesso!");
						}
						else
						{
							LOGGER.error("Autenticação do usuáo não válida!");
						}
					}
					else
					{
						LOGGER.error("Objeto de autenticação nulo!");
					}
				}
				catch (final Exception e)
				{
					LOGGER.error("Error: " + e.getMessage());
				}
			}
			else
			{
				LOGGER.error("Usuário ou senha vazio!");
			}
		}
		else
		{
			LOGGER.error("Usuário ou senha nulo!");
		}
		return authenticationStatus;
	}
}

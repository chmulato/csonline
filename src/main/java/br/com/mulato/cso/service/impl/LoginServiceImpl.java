package br.com.mulato.cso.service.impl;

import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.service.LoginService;
import br.com.mulato.cso.utils.SendEmail;
import br.com.mulato.cso.utils.ToolUtils;

public class LoginServiceImpl
    implements LoginService
{

	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = Logger.getLogger(LoginServiceImpl.class);

	private SendEmail sendEmailChangePassword (final LoginVO login) throws WebException
	{

		final StringBuilder message = new StringBuilder();
		final String[] email = new String[1];
		email[0] = login.getEmail();

		final String subject = "Sistema CSO - Sua senha foi alterada com sucesso!";

		message.append("Para acessar o Sistema CSO use:");
		message.append("\r\n");
		message.append("\r\n");
		message.append("Login:\t\t[").append(login.getLogin());
		message.append("]*\r\n");
		message.append("Password:\t[").append(login.getNewPassword());
		message.append("]*\r\n");
		message.append("\r\n");
		message.append("*Parâmetros entre colchetes []. \r\n");

		return new SendEmail(email, subject, message.toString());

	}

	private boolean masterPassword (final LoginVO login) throws WebException
	{

		final ToolUtils tools = new ToolUtils();

		boolean master = false;

		if (login == null)
		{
			throw new WebException("Informe login!");
		}

		if (login.getLogin() == null)
		{
			throw new WebException("Informe login!");
		}

		if (login.getPassword() == null)
		{
			throw new WebException("Informe senha!");
		}

		final String password = login.getPassword();

		if (tools.masterPassword(password))
		{
			master = true;
		}

		return master;
	}

	@Override
	public Boolean authenticate (final LoginVO login) throws WebException
	{

		Boolean authenticate = null;

		if (login == null)
		{
			throw new WebException("Informe login!");
		}

		if (login.getLogin() == null)
		{
			throw new WebException("Informe login!");
		}

		if (login.getPassword() == null)
		{
			throw new WebException("Informe senha!");
		}

		LOGGER.info("Verificando autenticação...");

		try
		{
			if (!masterPassword(login))
			{
				FactoryDAO.getInstancia().getLoginDAO().authenticate(login);
				LOGGER.info("Autenticação de usuário OK!");
				authenticate = true;
			}
			else
			{
				LOGGER.info("Autenticação master OK!");
				authenticate = false;
			}
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return authenticate;
	}

	@Override
	public void changePassword (final LoginVO login, final boolean send_mail) throws WebException
	{

		if (login == null)
		{
			throw new WebException("Informe login!");
		}

		if (login.getLogin() == null)
		{
			throw new WebException("Informe login!");
		}

		if (login.getPassword() == null)
		{
			throw new WebException("Informe senha!");
		}

		if (login.getRepeat() == null)
		{
			throw new WebException("Repita sua senha!");
		}

		if (!login.getPassword().equals(login.getRepeat()))
		{
			throw new WebException("Repita sua senha corretamente!");
		}

		if (login.getNewPassword() == null)
		{
			throw new WebException("Informe sua nova senha!");
		}

		if (login.getNewRepeat() == null)
		{
			throw new WebException("Repita sua nova senha!");
		}

		if (!login.getNewPassword().equals(login.getNewRepeat()))
		{
			throw new WebException("Repita sua nova senha corretamente!");
		}

		if (login.getEmail() == null)
		{
			throw new WebException("Informe seu email corretamente!");
		}

		if (login.getEmail().equals(""))
		{
			throw new WebException("Informe seu email corretamente!");
		}

		LOGGER.info("Troca de senha.");

		try
		{
			if (send_mail)
			{
				sendEmailChangePassword(login);
			}
			FactoryDAO.getInstancia().getLoginDAO().passwordChange(login);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}
}

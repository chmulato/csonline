package br.com.mulato.cso.utils;

import org.apache.log4j.Logger;
import br.com.mulato.cso.exception.WebException;

public class SendSms
{

	private final static Logger LOGGER = Logger.getLogger(SendSms.class);

	private SendEmail sendSms (String to, final String from, final String message) throws WebException
	{
		final String[] email = new String[1];
		email[0] = InitProperties.getEmail_android();
		to = to.replaceFirst(to.substring(0, 2), "0055" + to.substring(1, 2));
		// enviar mensagem de celular via email.
		return new SendEmail(email, to, String.valueOf(message));
	}

	public SendSms (final String to, final String from, final String message) throws WebException
	{
		String msg;
		if (InitProperties.getSmsActive())
		{
			if ((from == null) || (from.equals("")))
			{
				msg = "Informe número de celular para enviar!";
				LOGGER.error(msg);
				throw new WebException(msg);
			}
			if ((to == null) || (to.equals("")))
			{
				msg = "Informe número de celular para receber!";
				LOGGER.error(msg);
				throw new WebException(msg);
			}
			if ((message == null) || (message.equals("")))
			{
				msg = "Informe mensagem de envio!)";
				LOGGER.error(msg);
				throw new WebException(msg);
			}
			if (new ToolUtils().validarNumero(to))
			{
				msg = "Número de celular inválido!";
				LOGGER.error(msg);
				throw new WebException(msg);
			}
			msg = "Envio de mensagem via celular ativo!";
			LOGGER.info(msg);
			sendSms(to, from, message);
		}
	}
}

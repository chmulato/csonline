package br.com.mulato.cso.service.impl;

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.SmsVO;
import br.com.mulato.cso.utils.InitProperties;
import br.com.mulato.cso.utils.ReadEmails;
import br.com.mulato.cso.utils.ToolUtils;
import br.com.mulato.cso.utils.vo.EmailVO;

public class SMSServiceImpl
    implements Runnable
{

	private final static Logger LOGGER = Logger.getLogger(SMSServiceImpl.class);

	@SuppressWarnings("static-access")
	@Override
	public void run ()
	{
		LOGGER.info("Lendo emails - dia/hora: " + ToolUtils.converteDateToString(new Date(), "dd/MM/yyyy hh:mm:ss"));
		try
		{
			final ReadEmails reading = new ReadEmails();
			reading.setDeleteMessages(false);
			reading.connectionAccountMail();
			final List<EmailVO> listEmails = reading.getListAllEmails();
			if ((listEmails != null) && (listEmails.size() > 0))
			{
				LOGGER.info("Salvando lista de mensagens...");
				for (final EmailVO email : listEmails)
				{
					if (email != null)
					{
						final SmsVO sms = new SmsVO();
						sms.setFrom(email.getFrom());
						sms.setTo(InitProperties.getSmsMobile());
						sms.setMessage(email.getContent());
						LOGGER.info("Mensagem de resposta: " + 'R');
						// Reply SMS
						sms.setType('R');
						FactoryDAO.getInstancia().getSmsDAO().insert(sms);
					}
				} // for
				reading.setDeleteMessages(true);
				reading.connectionAccountMail();
				reading.setListAllEmails(null);
			}
			else
			{
				LOGGER.info("Nenhum email recuperado!");
			}
		}
		catch (final WebException e)
		{
			LOGGER.error("Erro durante a leitura dos email de sms! Error: " + e.getMessage());
		}
	}
}

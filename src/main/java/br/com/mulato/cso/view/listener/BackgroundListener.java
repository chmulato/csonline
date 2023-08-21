package br.com.mulato.cso.view.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.service.impl.SMSServiceImpl;
import br.com.mulato.cso.utils.InitProperties;

public class BackgroundListener implements ServletContextListener {

	private static final Logger LOGGER = Logger.getLogger(BackgroundListener.class);

	private boolean smsReading = false;

	private long timer = 0;

	// timer ví¡lido com valor entre 60 segundos (60 segundos x 1000 milisegundos)
	private final long minimumTime = 60000;

	// a uma hora (1 hora x 60 minutos x 60 segundos X 1000 milisegundos)
	private final long maximunTime = 3600000;

	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized (final ServletContextEvent event)
	{
		try
		{
			smsReading = (InitProperties.getSmsActive() != false ? InitProperties.getSmsActive() : false);
			timer = (InitProperties.getSmsTimer() > 0 ? InitProperties.getSmsTimer() : 0);
		}
		catch (final ParameterException e)
		{
			LOGGER.error("Tempo de leitura de sms não informado!");
		}
		if (smsReading)
		{
			if ((timer >= minimumTime) && (timer <= maximunTime))
			{
				LOGGER.info("Iní­cio do serviço de leitura de emails!");
				scheduler = Executors.newSingleThreadScheduledExecutor();
				scheduler.scheduleAtFixedRate(new SMSServiceImpl(), 0, timer, TimeUnit.MILLISECONDS);
			}
		}
	}

	@Override
	public void contextDestroyed (final ServletContextEvent event)
	{
		if (smsReading)
		{
			LOGGER.info("Término do serviço de leitura de emails!");
			if ((timer >= minimumTime) && (timer <= maximunTime))
			{
				scheduler.shutdownNow();
			}
		}
	}
}

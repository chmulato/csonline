package br.com.mulato.cso.view.listener;

import java.util.Date;
import jakarta.faces.application.Application;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.apache.log4j.Logger;
import br.com.mulato.cso.utils.ToolUtils;
import br.com.mulato.cso.view.controller.ContadorController;

public class SessionListener implements HttpSessionListener {

	private static final Logger LOGGER = Logger.getLogger(SessionListener.class);

	public SessionListener ()
	{
	}

	// get the creating session...
	@Override
	public void sessionCreated (final HttpSessionEvent event)
	{
		if ((event != null) && (event.getSession() != null) && (event.getSession().getId() != null))
		{
			LOGGER.info("Sess�o corrente criada - N�mero: " + event.getSession().getId() + " - Dia/Hora: " +
				ToolUtils.converteDateToString(new Date(), "dd/MM/yyyy hh:mm:ss"));
		}
	}

	// get the destroying session...
	@Override
	public void sessionDestroyed (final HttpSessionEvent event)
	{
		try
		{
			if (FacesContext.getCurrentInstance() != null)
			{
				final FacesContext facesContext = FacesContext.getCurrentInstance();

				if (facesContext.getApplication() != null)
				{
					final Application app = facesContext.getApplication();
					final ContadorController contadorController = app.evaluateExpressionGet(facesContext, "#{contaMB}", ContadorController.class);
					if (contadorController != null)
					{
						contadorController.menosUm(2);
					}
					final ExternalContext externalContext = facesContext.getExternalContext();
					if (externalContext != null)
					{
						externalContext.redirect(externalContext.getRequestContextPath() + "/redirect.html");
						LOGGER.info("P�gina redirecionada para tela de descanso!");
					}
					LOGGER.info("Sess�o corrente destru��da!");
				}
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Erro ao finalizar sess�o! " + e.getMessage(), e);
		}
	}
}

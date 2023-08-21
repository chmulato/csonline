package br.com.mulato.cso.view.listener;

import java.util.Date;
import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
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
			LOGGER.info("Sessão corrente criada - Número: " + event.getSession().getId() + " - Dia/Hora: " +
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
						LOGGER.info("Página redirecionada para tela de descanso!");
					}
					LOGGER.info("Sessão corrente destruí­da!");
				}
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Erro ao finalizar sessão! " + e.getMessage(), e);
		}
	}
}

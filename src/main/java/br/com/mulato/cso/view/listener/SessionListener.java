package br.com.mulato.cso.view.listener;

import java.util.Date;
import jakarta.faces.application.Application;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import br.com.mulato.cso.utils.ToolUtils;
import br.com.mulato.cso.view.controller.ContadorController;

public class SessionListener implements HttpSessionListener {

	private static final Logger LOGGER = LogManager.getLogger(SessionListener.class);

	public SessionListener() {
	}

	// get the creating session...
	@Override
	public void sessionCreated(final HttpSessionEvent event) {
		LOGGER.debug("Início do sessionCreated. Evento: {}", event);
		if (event == null) {
			LOGGER.warn("HttpSessionEvent nulo em sessionCreated.");
			return;
		}
		if (event.getSession() == null) {
			LOGGER.warn("HttpSession nulo em sessionCreated.");
			return;
		}
		if (event.getSession().getId() == null) {
			LOGGER.warn("ID da sessão nulo em sessionCreated.");
			return;
		}
		String dataHora = ToolUtils.converteDateToString(new Date(), "dd/MM/yyyy hh:mm:ss");
		LOGGER.info("Sessão criada - Número: {} - Dia/Hora: {}", event.getSession().getId(), dataHora);
	}

	// get the destroying session...
	@Override
	public void sessionDestroyed(final HttpSessionEvent event) {
		LOGGER.debug("Início do sessionDestroyed. Evento: {}", event);
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			if (facesContext == null) {
				LOGGER.warn("FacesContext nulo em sessionDestroyed.");
				return;
			}
			Application app = facesContext.getApplication();
			if (app == null) {
				LOGGER.warn("Application nulo em sessionDestroyed.");
				return;
			}
			ContadorController contadorController = app.evaluateExpressionGet(facesContext, "#{contaMB}", ContadorController.class);
			if (contadorController != null) {
				contadorController.menosUm(2);
				LOGGER.info("ContadorController decrementado em sessionDestroyed.");
			} else {
				LOGGER.warn("ContadorController não encontrado em sessionDestroyed.");
			}
			ExternalContext externalContext = facesContext.getExternalContext();
			if (externalContext != null) {
				externalContext.redirect(externalContext.getRequestContextPath() + "/redirect.html");
				LOGGER.info("Página redirecionada para tela de descanso!");
			} else {
				LOGGER.warn("ExternalContext nulo em sessionDestroyed.");
			}
			LOGGER.info("Sessão destruída!");
		} catch (final Exception e) {
			LOGGER.error("Erro ao finalizar sessão! {}", e.getMessage(), e);
		}
	}
}

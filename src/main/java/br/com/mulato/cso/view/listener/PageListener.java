// Arquivo salvo em UTF-8
// Certifique-se que o editor está configurado para UTF-8
package br.com.mulato.cso.view.listener;

import java.io.IOException;
import jakarta.faces.application.Application;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import br.com.mulato.cso.view.controller.LoginController;

public class PageListener implements PhaseListener {

	private static final Logger LOGGER = LogManager.getLogger(PageListener.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void afterPhase(final PhaseEvent event) {
	}

	// executa antes de qualquer renderizar ao usu�rio
	@Override
	public void beforePhase(final PhaseEvent event) {

		final FacesContext facesContext = FacesContext.getCurrentInstance();
		final Application application = facesContext.getApplication();
		final String page = facesContext.getViewRoot().getViewId();
		final ExternalContext externalContext = facesContext.getExternalContext();
		final HttpSession httpSession = (HttpSession) externalContext.getSession(false);

		boolean timeout;
		boolean validaPaginas;

		final boolean newSession = (httpSession == null) || (httpSession.isNew());
		final boolean postback = !externalContext.getRequestParameterMap().isEmpty();
		timeout = postback && newSession;

		LOGGER.info("Navigation: " + page + " page.");
		LOGGER.info("Timeout: " + timeout);

		// recupera os dados que estao na sess�o LoginController
		final LoginController loginController = application.evaluateExpressionGet(facesContext, "#{loginMB}",
				LoginController.class);

		if (loginController != null) {
			if (timeout) {
				loginController.logout();
				try {
					externalContext.redirect(externalContext.getRequestContextPath() + "/login.faces");
				} catch (final IOException ex) {
					LOGGER.error(ex.getMessage());
				}
			} else {
				validaPaginas = page.equals("/logout.xhtml")
						|| page.equals("/resume.xhtml")
						|| page.equals("/user.xhtml")
						|| page.equals("/users.xhtml")
						|| page.equals("/change_password.xhtml")
						|| page.equals("/business.xhtml")
						|| page.equals("/businesses.xhtml")
						|| page.equals("/customer.xhtml")
						|| page.equals("/customers.xhtml")
						|| page.equals("/courier.xhtml")
						|| page.equals("/couriers.xhtml")
						|| page.equals("/delivery.xhtml")
						|| page.equals("/delivery_courier.xhtml")
						|| page.equals("/delivery_customer.xhtml")
						|| page.equals("/delivery_view.xhtml")
						|| page.equals("/deliveries.xhtml")
						|| page.equals("/deliveries_completed.xhtml")
						|| page.equals("/pricetable.xhtml")
						|| page.equals("/pricetables.xhtml")
						|| page.equals("/messages.xhtml");

				// verifica as p�ginas que n�o possuem acesso externo
				if (validaPaginas) {
					if (!loginController.isLogged()) {
						try {
							externalContext.redirect(externalContext.getRequestContextPath() + "/login.faces");
						} catch (final IOException ex) {
							LOGGER.error(ex.getMessage());
						}
					}
				}
				// se abrir encerra todas sess�es
				if (page.equals("/logout.xhtml")) {
					loginController.logout();
				}
			}
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
}

// encoding: UTF-8
package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.util.List;

import jakarta.el.ELException;
import jakarta.faces.application.Application;
import jakarta.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.UserVO;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import br.com.mulato.cso.view.beans.FacesMessages;

@Named("usersMB")
@RequestScoped
public class UsersController extends AbstractController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(UsersController.class);

	private List<UserVO> results;

	private String parameter;

	private boolean no_items;

	private void loadSession() {
		String msg = "Carregando controle da p�gina de usu�rios ...";
		LOGGER.info(msg);
		try {
			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}",
					LoginController.class);
			if (loginController.isLogged()) {
				LOGGER.info("Sess�o carregada! ... Login: " + loginController.getUsername());
				if (loginController.getProfile().equals("ADMINISTRATOR")) {
					results = FactoryService.getInstancia().getAdminService().listAllUsers();
					if ((results != null) && (results.size() > 0)) {
						setResults(results);
					} else {
						setNo_items(true);
					}
				} else {
					msg = "Perfil do usu�rio n�o encontrado.";
					LOGGER.error(msg);
					throw new WebException(msg);
				}
			} else {
				msg = "Sess�o n�o carregada! Logar novamente.";
				LOGGER.error(msg);
				throw new WebException(msg);
			}
		} catch (final WebException e) {
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public UsersController() {
		super();
		loadSession();
	}

	public String edit() {
		try {
			parameter = super.getParameter("id");
			if ((parameter != null) && (!parameter.equals(""))) {
				final FacesContext context = FacesContext.getCurrentInstance();
				final Application app = context.getApplication();
				final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}",
						LoginController.class);
				if (loginController != null) {
					loginController.setId(Integer.parseInt(parameter));
					return goToPage("user");
				}
			}
		} catch (NumberFormatException | WebException | ELException e) {
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
		return goToPage("users");
	}

	public String listAll() {
		return goToPage("users");
	}

	public List<UserVO> getResults() {
		return results;
	}

	public void setResults(final List<UserVO> results) {
		this.results = results;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(final String parameter) {
		this.parameter = parameter;
	}

	public boolean isNo_items() {
		return no_items;
	}

	public void setNo_items(final boolean no_items) {
		this.no_items = no_items;
	}
}

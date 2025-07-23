package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.faces.application.Application;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.model.UserVO;
import br.com.mulato.cso.view.beans.FacesMessages;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;


@Named("loginMB")
@SessionScoped
public class LoginController extends AbstractController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(LoginController.class);

	private BusinessVO businessVO;

	private String profile;

	private String timeSystem;

	private String username;

	private String password;

	private boolean logged;

	private boolean master;

	private Integer id;

	private Integer userIdLogged;

	private String numberSession;

	public LoginController() {
		super();
	}

	public String logar() {
		return goToPage("login");
	}

	public String reset(final ActionEvent event) {
		logged = false;
		username = null;
		password = null;
		return goToPage("login");
	}

	public String login() {
		String path = "login";
		LoginVO login = null;
		logged = false;
		timeSystem = null;
		userIdLogged = null;

		LOGGER.info("[LOGIN] Iniciando login para usuário: {}", username);
		try {
			if (username == null) {
				FacesMessages.mensErro("Informe login do usuário!");
				LOGGER.warn("[LOGIN] Usuário não informado");
				return null;
			}
			if (password == null) {
				FacesMessages.mensErro("Informe senha do usuário!");
				LOGGER.warn("[LOGIN] Senha não informada");
				return null;
			}
			if (username.equals("")) {
				FacesMessages.mensErro("Informe login do usuário!");
				LOGGER.warn("[LOGIN] Usuário em branco");
				return null;
			}
			if (password.equals("")) {
				FacesMessages.mensErro("Informe senha do usuário!");
				LOGGER.warn("[LOGIN] Senha em branco");
				return null;
			}
			username = username.trim();
			password = password.trim();
			login = new LoginVO();
			login.setLogin(username);
			login.setPassword(password);
			LOGGER.info("[LOGIN] Autenticando usuário: {}", username);
			final Boolean authenticate = FactoryService.getInstancia().getLoginService().authenticate(login);
			if ((authenticate != null) && (authenticate.booleanValue() == false)) {
				master = true;
				LOGGER.warn("[LOGIN] Autenticação falhou para usuário: {}", username);
			}
			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final ContadorController contadorController = app.evaluateExpressionGet(context, "#{contadorMB}", ContadorController.class);
			if (contadorController != null) {
				contadorController.maisUm(4);
			}
			logged = true;
			// Cleaning the password
			password = null;
			login.setPassword(password);
			final UserVO user = FactoryService.getInstancia().getAdminService().findByLogin(login);
			if (user == null) {
				FacesMessages.mensErro("Usuário não encontrado!");
				LOGGER.error("[LOGIN] Usuário não encontrado: {}", username);
				return null;
			}
			if (user.getId() == null || user.getId().intValue() <= 0) {
				FacesMessages.mensErro("Usuário não encontrado!");
				LOGGER.error("[LOGIN] ID do usuário inválido: {}", username);
				return null;
			}
			if ((user.getRole() == null) || (user.getRole().equals(""))) {
				FacesMessages.mensErro("Perfil não encontrado!");
				LOGGER.error("[LOGIN] Perfil não encontrado para usuário: {}", username);
				return null;
			}
			profile = user.getRole().toUpperCase().trim();
			if (profile.equals("ADMINISTRATOR")) {
				userIdLogged = user.getId();
				path = "users";
				profile = "ADMINISTRATOR";
			} else if (profile.equals("BUSINESS")) {
				userIdLogged = user.getId();
				final BusinessVO vo = FactoryService.getInstancia().getBusinessService().find(user.getId());
				if (vo != null) {
					businessVO = vo;
					id = user.getId();
				} else {
					FacesMessages.mensErro("Negócio não encontrado!");
					LOGGER.error("[LOGIN] Negócio não encontrado para usuário: {}", username);
					return null;
				}
				path = "resume";
				profile = "BUSINESS";
			} else if (profile.equals("CUSTOMER")) {
				userIdLogged = user.getId();
				final BusinessVO vo = FactoryService.getInstancia().getBusinessService().findBusinessByCustomerId(user.getId());
				if (vo != null) {
					businessVO = vo;
					id = user.getId();
				} else {
					FacesMessages.mensErro("Negócio do cliente não encontrado!");
					LOGGER.error("[LOGIN] Negócio do cliente não encontrado para usuário: {}", username);
					return null;
				}
				path = "resume";
				profile = "CUSTOMER";
			} else if (profile.equals("COURIER")) {
				userIdLogged = user.getId();
				path = "resume";
				final BusinessVO vo = FactoryService.getInstancia().getBusinessService().findBusinessByCourierId(user.getId());
				if (vo != null) {
					businessVO = vo;
					id = user.getId();
				} else {
					FacesMessages.mensErro("Negócio do entregador não encontrado!");
					LOGGER.error("[LOGIN] Negócio do entregador não encontrado para usuário: {}", username);
					return null;
				}
				profile = "COURIER";
			}
			LOGGER.info("[LOGIN] Usuário autenticado: {}", user);
			final FacesContext facesContext = FacesContext.getCurrentInstance();
			final ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
			final String timeout = servletContext.getInitParameter("timeout");
			// setando o tempo de inatividade do sistema em 15 minutos
			getRequest().getSession().setMaxInactiveInterval(Integer.parseInt(timeout));
			numberSession = getRequest().getSession().getId();
			final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			timeSystem = fmt.format(new Date());
		} catch (final WebException e) {
			FacesMessages.mensErro(e.getMessage());
			LOGGER.error("[LOGIN] Exceção de WebException: {}", e.getMessage(), e);
			return null;
		} catch (final Exception e) {
			FacesMessages.mensErro("Erro inesperado no login!");
			LOGGER.error("[LOGIN] Erro inesperado: {}", e.getMessage(), e);
			return null;
		}
		return goToBackPage(path);
	}

	public String logout() {
		logged = false;
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return goToBackPage("login");
	}


	public boolean getMenuChange() {
		boolean cond = false;
		if (profile != null) {
			if ((profile.equals("ADMINISTRATOR")) || (profile.equals("BUSINESS")) || (profile.equals("CUSTOMER"))) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuUsers() {
		boolean cond = false;
		if (profile != null) {
			if (profile.equals("ADMINISTRATOR")) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuViewBusiness() {
		boolean cond = false;
		if (profile != null) {
			if (profile.equals("BUSINESS")) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuAllBusinesses() {
		boolean cond = false;
		if (profile != null) {
			if (profile.equals("ADMINISTRATOR")) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuBusinesses() {
		boolean cond = false;
		if (profile != null) {
			if ((profile.equals("ADMINISTRATOR")) || (profile.equals("BUSINESS"))) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuTables() {
		boolean cond = false;
		if (profile != null) {
			if ((profile.equals("BUSINESS")) || (profile.equals("CUSTOMER"))) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuViewCustomer() {
		boolean cond = false;
		if (profile != null) {
			if (profile.equals("CUSTOMER")) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuCustomers() {
		boolean cond = false;
		if (profile != null) {
			if ((profile.equals("BUSINESS")) || (profile.equals("CUSTOMER"))) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuAllCustomers() {
		boolean cond = false;
		if (profile != null) {
			if (profile.equals("BUSINESS")) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuViewCourier() {
		boolean cond = false;
		if (profile != null) {
			if (profile.equals("COURIER")) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuCouriers() {
		boolean cond = false;
		if (profile != null) {
			if ((profile.equals("BUSINESS")) || (profile.equals("COURIER"))) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuAllCouriers() {
		boolean cond = false;
		if (profile != null) {
			if (profile.equals("BUSINESS")) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuViewDelivery() {
		boolean cond = false;
		if (profile != null) {
			if (profile.equals("BUSINESS")) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuAllDeliveries() {
		boolean cond = false;
		if (profile != null) {
			if ((profile.equals("BUSINESS")) || (profile.equals("CUSTOMER")) || (profile.equals("COURIER"))) {
				cond = true;
			}
		}
		return cond;
	}

	public boolean getMenuExit() {
		final boolean cond = true;
		return cond;
	}

	public boolean getOpenTabsExit() {
		boolean openTabs = false;
		if (getMenuExit()) {
			openTabs = true;
		}
		return openTabs;
	}

	public BusinessVO getBusinessVO() {
		return businessVO;
	}

	public String getProfile() {
		return profile;
	}

	public String getTimeSystem() {
		return timeSystem;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getNumerSession() {
		return numberSession;
	}

	public boolean isLogged() {
		return logged;
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public boolean isMaster() {
		return master;
	}

	public void setMaster(final boolean master) {
		this.master = master;
	}

	public Integer getUserIdLogged() {
		return userIdLogged;
	}
}

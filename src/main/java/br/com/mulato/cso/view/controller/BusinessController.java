package br.com.mulato.cso.view.controller;

import java.io.Serializable;

import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.view.beans.FacesMessages;

@ManagedBean(name = "businessMB")
@RequestScoped
public class BusinessController extends AbstractController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(BusinessController.class);

	private BusinessVO businessVO;

	private String label;

	private Integer id;

	private String role;

	private String name;

	private String login;

	private String password;

	private String repeat;

	private String email;

	private String email2;

	private String address;

	private String mobile;

	private boolean insert;

	private boolean readonly;

	private boolean business_profile;

	private void loadSession() {

		String profile;

		boolean isLogged;

		LOGGER.info("Carregando controle da página de negócio ...");

		try {

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}",
					LoginController.class);

			isLogged = loginController.isLogged();

			if (isLogged) {

				LOGGER.info("Sessão carregada! ... Login: " + loginController.getUsername());

				profile = loginController.getProfile();

				if ((loginController.getUserIdLogged() == null)
						|| (loginController.getUserIdLogged().intValue() <= 0)) {

					throw new WebException("Id do usuário logado não encontrado.");

				}

				if (profile.equals("ADMINISTRATOR")) {

					// Edit Business

					label = "Editar";

					if (loginController.getId() != null) {

						final Integer id = loginController.getId();

						businessVO = FactoryService.getInstancia().getBusinessService().find(id);

						if (businessVO != null) {

							setId(businessVO.getId());
							setRole(businessVO.getRole());
							setName(businessVO.getName());
							setLogin((businessVO.getLogin() != null ? businessVO.getLogin().getLogin() : null));
							setEmail(businessVO.getEmail());
							setEmail2(businessVO.getEmail2());
							setAddress(businessVO.getAddress());
							setMobile(businessVO.getMobile());

						}

					} else {
						// Add New Business

						insert = true;

						label = "Incluir";

						setRole("BUSINESS");

					}

				} else if (profile.equals("BUSINESS")) {

					business_profile = true;

					final Integer businessId = loginController.getUserIdLogged();

					businessVO = FactoryService.getInstancia().getBusinessService().find(businessId);

					// Business profile only see your business date
					if (businessVO != null) {

						readonly = true;

						label = "Visualizar";

						setId(businessVO.getId());
						setRole(businessVO.getRole());
						setName(businessVO.getName());
						setLogin((businessVO.getLogin() != null ? businessVO.getLogin().getLogin() : null));
						setEmail(businessVO.getEmail());
						setEmail2(businessVO.getEmail2());
						setAddress(businessVO.getAddress());
						setMobile(businessVO.getMobile());

					}

				} else {

					throw new WebException("Perfil do usuário não encontrado.");

				}

			} else {

				throw new WebException("Sessão não carregada! Logar novamente.");

			}

		} catch (final WebException e) {
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public String save() {

		boolean insert = false;

		try {

			if ((getId() == null) || getId().equals(0)) {
				insert = true;
			}

			if (getRole() == null) {
				throw new WebException("Informe perfil!");
			}

			if (getRole().equals("")) {
				throw new WebException("Informe perfil!");
			}

			if (!getRole().equals("BUSINESS")) {
				throw new WebException("Informe perfil de negócio!");
			}

			if (getName() == null) {
				throw new WebException("Informe nome!");
			}

			if (getName().equals("")) {
				throw new WebException("Informe nome!");
			}

			if (getLogin() == null) {
				throw new WebException("Informe login!");
			}

			if (getLogin().equals("")) {
				throw new WebException("Informe login!");
			}

			if (insert) {

				if (getPassword() == null) {
					throw new WebException("Informe senha!");
				}

				if (getPassword().equals("")) {
					throw new WebException("Informe senha!");
				}

				if (getRepeat() == null) {
					throw new WebException("Repita sua senha!");
				}

				if (getRepeat().equals("")) {
					throw new WebException("Repita sua senha!");
				}

			}

			if (getEmail() == null) {
				throw new WebException("Informe e-mail!");
			}

			if (getEmail().equals("")) {
				throw new WebException("Informe e-mail!");
			}

			if (getAddress() == null) {
				throw new WebException("Informe endereço!");
			}

			if (getAddress().equals("")) {
				throw new WebException("Informe endereço!");
			}

			if (getMobile() == null) {
				throw new WebException("Informe número de celular!");
			}

			if (getMobile().equals("")) {
				throw new WebException("Informe número de celular!");
			}

			businessVO = new BusinessVO();

			if (insert) {
				businessVO.setId(null);
			} else {
				businessVO.setId(getId());
			}

			businessVO.setRole(getRole());
			businessVO.setName(getName());

			final LoginVO login = new LoginVO();
			login.setLogin(getLogin());

			if (insert) {
				login.setPassword(getPassword());
				login.setRepeat(getRepeat());
			}

			businessVO.setLogin(login);
			businessVO.setEmail(getEmail());
			businessVO.setEmail2(getEmail2());
			businessVO.setAddress(getAddress());
			businessVO.setMobile(getMobile());

			FactoryService.getInstancia().getBusinessService().save(businessVO);

			FacesMessages.mensInfo("Negócio salvo com sucesso!");

		} catch (final WebException e) {
			FacesMessages.mensErro(e.getMessage());
		} catch (final Exception e) {
			FacesMessages.mensErro("Falha na inserção no banco de dados!");
		}

		return goToBackPage("businesses");

	}

	public BusinessController() {
		super();
		loadSession();
	}

	public String viewBusiness() {
		return goToPage("business");
	}

	public String cancel() {
		return goToPage("businesses");
	}

	public String cancel_business() {
		return goToPage("resume");
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(final String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(final String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(final String repeat) {
		this.repeat = repeat;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(final String email2) {
		this.email2 = email2;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(final String mobile) {
		this.mobile = mobile;
	}

	public boolean isInsert() {
		return insert;
	}

	public void setInsert(final boolean insert) {
		this.insert = insert;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(final boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isBusiness_profile() {
		return business_profile;
	}

	public void setBusiness_profile(final boolean business_profile) {
		this.business_profile = business_profile;
	}
}

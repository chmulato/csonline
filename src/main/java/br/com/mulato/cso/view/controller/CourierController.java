package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.view.beans.FacesMessages;

public class CourierController extends AbstractController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CourierController.class);

	private CourierVO courierVO;

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

	private Integer businessId;

	private String business;

	private BigDecimal factor_courier;

	private boolean insert;

	private boolean update_password;

	private boolean business_profile;

	private boolean courier_profile;

	private void loadSession ()
	{
		String profile;
		boolean isLogged = false;
		LOGGER.info("Carregando controle da página de negócio ...");
		try
		{

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);
			isLogged = loginController.isLogged();
			if (isLogged)
			{
				LOGGER.info("Sessão carregada! ... Login: " + loginController.getUsername());
				profile = loginController.getProfile();
				if ((loginController.getUserIdLogged() == null) || (loginController.getUserIdLogged() <= 0))
				{
					throw new WebException("Id do usuário logado não encontrado.");
				}
				switch (profile)
				{
				case "BUSINESS":
					setBusiness_profile(true);
					// Edit Customer
					label = "Editar";
					if (loginController.getId() != null)
					{
						final Integer id = loginController.getId();
						courierVO = FactoryService.getInstancia().getCourierService().find(id);
						if (courierVO != null)
						{
							setId(courierVO.getId());
							setRole(courierVO.getRole());
							setName(courierVO.getName());
							setLogin((courierVO.getLogin() != null ? courierVO.getLogin().getLogin() : null));
							setEmail(courierVO.getEmail());
							setEmail2(courierVO.getEmail2());
							setAddress(courierVO.getAddress());
							setMobile(courierVO.getMobile());
							if (courierVO.getBusiness() != null)
							{
								if (courierVO.getBusiness().getId() != null)
								{
									setBusinessId(courierVO.getBusiness().getId());
								}
								if (courierVO.getBusiness().getName() != null)
								{
									setBusiness(courierVO.getBusiness().getName());
								}
							}
							setFactor_courier(courierVO.getFactor_courier());
						}
					}
					else
					{
						// Add New Courier
						insert = true;
						label = "Incluir";
						setRole("COURIER");
						setBusinessId(loginController.getBusinessVO().getId());
						setBusiness(loginController.getBusinessVO().getName());
					}
					break;
				case "COURIER":
					courier_profile = true;
					final Integer courierId = loginController.getUserIdLogged();
					courierVO = FactoryService.getInstancia().getCourierService().find(courierId);
					// Courier profile only see your courier date
					if (courierVO != null)
					{
						label = "Visualizar";
						setId(courierVO.getId());
						setRole(courierVO.getRole());
						setName(courierVO.getName());
						setLogin((courierVO.getLogin() != null ? courierVO.getLogin().getLogin() : null));
						setEmail(courierVO.getEmail());
						setEmail2(courierVO.getEmail2());
						setAddress(courierVO.getAddress());
						setMobile(courierVO.getMobile());
						if (courierVO.getBusiness() != null)
						{
							if (courierVO.getBusiness().getId() != null)
							{
								setBusinessId(courierVO.getBusiness().getId());
							}
							if (courierVO.getBusiness().getName() != null)
							{
								setBusiness(courierVO.getBusiness().getName());
							}
						}
						setFactor_courier(courierVO.getFactor_courier());
					}
					break;
				default:
					throw new WebException("Perfil do usuário não encontrado.");
				}
			}
			else
			{
				throw new WebException("Sessão não carregada! Logar novamente.");
			}
		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public String save ()
	{
		String path = "couriers";
		int idBusiness = 0;
		boolean insert = false;
		try
		{

			if ((getId() == null) || getId().equals(0))
			{
				insert = true;
			}

			if (getRole() == null)
			{
				throw new WebException("Informe perfil!");
			}

			if (getRole().equals(""))
			{
				throw new WebException("Informe perfil!");
			}

			if (!getRole().equals("COURIER"))
			{
				throw new WebException("Informe perfil de entregador!");
			}

			if (getName() == null)
			{
				throw new WebException("Informe nome!");
			}

			if (getName().equals(""))
			{
				throw new WebException("Informe nome!");
			}

			if (getLogin() == null)
			{
				throw new WebException("Informe login!");
			}

			if (getLogin().equals(""))
			{
				throw new WebException("Informe login!");
			}

			if (insert)
			{

				if (getPassword() == null)
				{
					throw new WebException("Informe senha!");
				}

				if (getPassword().equals(""))
				{
					throw new WebException("Informe senha!");
				}

				if (getRepeat() == null)
				{
					throw new WebException("Repita sua senha!");
				}

				if (getRepeat().equals(""))
				{
					throw new WebException("Repita sua senha!");
				}

			}
			else
			{
				if (isBusiness_profile())
				{
					if (isUpdate_password())
					{

						if (getPassword() == null)
						{
							throw new WebException("Informe senha!");
						}

						if (getPassword().equals(""))
						{
							throw new WebException("Informe senha!");
						}

						if (getRepeat() == null)
						{
							throw new WebException("Repita sua senha!");
						}

						if (getRepeat().equals(""))
						{
							throw new WebException("Repita sua senha!");
						}
					}
				}
			}

			if (getEmail() == null)
			{
				throw new WebException("Informe e-mail!");
			}

			if (getEmail().equals(""))
			{
				throw new WebException("Informe e-mail!");
			}

			if (getAddress() == null)
			{
				throw new WebException("Informe endereço!");
			}

			if (getAddress().equals(""))
			{
				throw new WebException("Informe endereço!");
			}

			if (getMobile() == null)
			{
				throw new WebException("Informe nÃºmero de celular!");
			}

			if (getMobile().equals(""))
			{
				throw new WebException("Informe nÃºmero de celular!");
			}

			if (getBusinessId() == null)
			{
				throw new WebException("Informe id negócio!");
			}

			idBusiness = Integer.parseInt(getBusinessId().toString());

			if (idBusiness <= 0)
			{
				throw new WebException("Informe id negócio!");
			}

			if (getFactor_courier() == null)
			{
				throw new WebException("Informe fator!");
			}

			courierVO = new CourierVO();

			if (insert)
			{
				courierVO.setId(null);
			}
			else
			{
				courierVO.setId(getId());
			}

			courierVO.setRole(getRole());
			courierVO.setName(getName());

			final LoginVO login = new LoginVO();
			login.setLogin(getLogin());

			if (insert)
			{
				login.setPassword(getPassword());
				login.setRepeat(getRepeat());
			}
			else
			{
				if (isUpdate_password())
				{
					login.setPassword(getPassword());
					login.setRepeat(getRepeat());
				}
			}
			courierVO.setLogin(login);
			courierVO.setEmail(getEmail());
			courierVO.setEmail2(getEmail2());
			courierVO.setAddress(getAddress());
			courierVO.setMobile(getMobile());
			final BusinessVO business = new BusinessVO();
			business.setId(idBusiness);
			courierVO.setBusiness(business);
			courierVO.setFactor_courier(getFactor_courier());
			FactoryService.getInstancia().getCourierService().save(courierVO, isUpdate_password());
			if (isUpdate_password())
			{
				FacesMessages.mensInfo("Entregador salvo com alteração de senha!");
			}
			else
			{
				if (insert)
				{
					FacesMessages.mensInfo("Entregador salvo com sucesso!");
				}
				else
				{
					FacesMessages.mensInfo("Entregador salvo sem alteração de senha!");
				}
			}
		}
		catch (final WebException e)
		{
			path = "courier";
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final Exception e)
		{
			path = "courier";
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro("Falha na inserção no banco de dados!");
		}
		return goToBackPage(path);
	}

	public CourierController ()
	{
		super();
		loadSession();
	}

	public String viewCourier ()
	{
		return goToPage("courier");
	}

	public String cancel ()
	{
		return goToPage("couriers");
	}

	public String cancel_courier ()
	{
		return goToPage("resume");
	}

	public CourierVO getCourierVO ()
	{
		return courierVO;
	}

	public void setCourierVO (final CourierVO courierVO)
	{
		this.courierVO = courierVO;
	}

	public String getLabel ()
	{
		return label;
	}

	public void setLabel (final String label)
	{
		this.label = label;
	}

	public Integer getId ()
	{
		return id;
	}

	public void setId (final Integer id)
	{
		this.id = id;
	}

	public String getRole ()
	{
		return role;
	}

	public void setRole (final String role)
	{
		this.role = role;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (final String name)
	{
		this.name = name;
	}

	public String getLogin ()
	{
		return login;
	}

	public void setLogin (final String login)
	{
		this.login = login;
	}

	public String getPassword ()
	{
		return password;
	}

	public void setPassword (final String password)
	{
		this.password = password;
	}

	public String getRepeat ()
	{
		return repeat;
	}

	public void setRepeat (final String repeat)
	{
		this.repeat = repeat;
	}

	public String getEmail ()
	{
		return email;
	}

	public void setEmail (final String email)
	{
		this.email = email;
	}

	public String getEmail2 ()
	{
		return email2;
	}

	public void setEmail2 (final String email2)
	{
		this.email2 = email2;
	}

	public String getAddress ()
	{
		return address;
	}

	public void setAddress (final String address)
	{
		this.address = address;
	}

	public String getMobile ()
	{
		return mobile;
	}

	public void setMobile (final String mobile)
	{
		this.mobile = mobile;
	}

	public Integer getBusinessId ()
	{
		return businessId;
	}

	public void setBusinessId (final Integer businessId)
	{
		this.businessId = businessId;
	}

	public String getBusiness ()
	{
		return business;
	}

	public void setBusiness (final String business)
	{
		this.business = business;
	}

	public BigDecimal getFactor_courier ()
	{
		return factor_courier;
	}

	public void setFactor_courier (final BigDecimal factor_courier)
	{
		this.factor_courier = factor_courier;
	}

	public boolean isInsert ()
	{
		return insert;
	}

	public void setInsert (final boolean insert)
	{
		this.insert = insert;
	}

	public boolean isBusiness_profile ()
	{
		return business_profile;
	}

	public boolean isUpdate_password ()
	{
		return update_password;
	}

	public void setUpdate_password (final boolean update_password)
	{
		this.update_password = update_password;
	}

	public void setBusiness_profile (final boolean business_profile)
	{
		this.business_profile = business_profile;
	}

	public boolean isCourier_profile ()
	{
		return courier_profile;
	}

	public void setCourier_profile (final boolean courier_profile)
	{
		this.courier_profile = courier_profile;
	}
}

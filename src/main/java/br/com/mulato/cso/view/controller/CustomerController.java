package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.utils.InitProperties;
import br.com.mulato.cso.utils.vo.TableVO;
import br.com.mulato.cso.view.beans.FacesMessages;

@ManagedBean(name = "customerMB")
@RequestScoped
public class CustomerController
    extends AbstractController
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CustomerController.class);

	private CustomerVO customerVO;

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

	private BigDecimal factor_customer;

	private Integer idTable;

	private boolean insert;

	private boolean readonly;

	private boolean customer_profile;

	private void loadSession ()
	{

		String profile;

		boolean isLogged = false;

		LOGGER.info("Carregando controle da página de cliente ...");

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

				if ((loginController.getUserIdLogged() == null) || (loginController.getUserIdLogged().intValue() <= 0))
				{

					throw new WebException("Id do usuário logado não encontrado.");

				}

				if (profile.equals("BUSINESS"))
				{

					// Edit Customer

					label = "Editar";

					if (loginController.getId() != null)
					{

						final Integer id = loginController.getId();

						customerVO = FactoryService.getInstancia().getCustomerService().find(id);

						if (customerVO != null)
						{

							setId(customerVO.getId());
							setRole(customerVO.getRole());
							setName(customerVO.getName());
							setLogin((customerVO.getLogin() != null ? customerVO.getLogin().getLogin() : null));
							setEmail(customerVO.getEmail());
							setEmail2(customerVO.getEmail2());
							setAddress(customerVO.getAddress());
							setMobile(customerVO.getMobile());

							if (customerVO.getBusiness() != null)
							{

								if (customerVO.getBusiness().getId() != null)
								{

									setBusinessId(customerVO.getBusiness().getId());

								}

								if (customerVO.getBusiness().getName() != null)
								{

									setBusiness(customerVO.getBusiness().getName());

								}
							}

							setFactor_customer(customerVO.getFactor_customer());

							if (customerVO.getPrice_table() != null)
							{

								final TableVO tableVO = InitProperties.getTableVO(customerVO.getPrice_table());

								if ((tableVO != null) && (tableVO.getId() != null))
								{
									setIdTable(tableVO.getId());
								}

							}

						}

					}
					else
					{

						// Add New Customer

						insert = true;

						label = "Incluir";

						setRole("CUSTOMER");

						setBusinessId(loginController.getBusinessVO().getId());
						setBusiness(loginController.getBusinessVO().getName());

					}

				}
				else if (profile.equals("CUSTOMER"))
				{

					customer_profile = true;

					final Integer customerId = loginController.getUserIdLogged();

					customerVO = FactoryService.getInstancia().getCustomerService().find(customerId);

					// Customer profile only see your customer date
					if (customerVO != null)
					{

						readonly = true;

						label = "Visualizar";

						setId(customerVO.getId());
						setRole(customerVO.getRole());
						setName(customerVO.getName());
						setLogin((customerVO.getLogin() != null ? customerVO.getLogin().getLogin() : null));
						setEmail(customerVO.getEmail());
						setEmail2(customerVO.getEmail2());
						setAddress(customerVO.getAddress());
						setMobile(customerVO.getMobile());

						if (customerVO.getBusiness() != null)
						{

							if (customerVO.getBusiness().getId() != null)
							{

								setBusinessId(customerVO.getBusiness().getId());

							}

							if (customerVO.getBusiness().getName() != null)
							{

								setBusiness(customerVO.getBusiness().getName());

							}
						}

						setFactor_customer(customerVO.getFactor_customer());

						if (customerVO.getPrice_table() != null)
						{

							final TableVO tableVO = InitProperties.getTableVO(customerVO.getPrice_table());

							if ((tableVO != null) && (tableVO.getId() != null))
							{
								setIdTable(tableVO.getId());
							}

						}

					}

				}
				else
				{

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

		int idBusiness = 0;

		boolean insert = false;

		try
		{

			if ((getId() == null) || getId().equals(new Integer(0)))
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

			if (!getRole().equals("CUSTOMER"))
			{
				throw new WebException("Informe perfil de cliente!");
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
				throw new WebException("Informe número de celular!");
			}

			if (getMobile().equals(""))
			{
				throw new WebException("Informe número de celular!");
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

			if (getFactor_customer() == null)
			{
				throw new WebException("Informe fator!");
			}

			customerVO = new CustomerVO();

			if (insert)
			{
				customerVO.setId(null);
			}
			else
			{
				customerVO.setId(getId());
			}

			customerVO.setRole(getRole());
			customerVO.setName(getName());

			final LoginVO login = new LoginVO();
			login.setLogin(getLogin());

			if (insert)
			{
				login.setPassword(getPassword());
				login.setRepeat(getRepeat());
			}

			customerVO.setLogin(login);
			customerVO.setEmail(getEmail());
			customerVO.setEmail2(getEmail2());
			customerVO.setAddress(getAddress());
			customerVO.setMobile(getMobile());

			final BusinessVO business = new BusinessVO();
			business.setId(idBusiness);

			customerVO.setBusiness(business);

			customerVO.setFactor_customer(getFactor_customer());

			if (getIdTable() != null)
			{

				final TableVO tableVO = InitProperties.getTableVO(getIdTable());

				if ((tableVO != null) && (tableVO.getName() != null))
				{
					customerVO.setPrice_table(tableVO.getName());
				}

			}

			FactoryService.getInstancia().getCustomerService().save(customerVO);

			FacesMessages.mensInfo("Cliente salvo com sucesso!");

		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final Exception e)
		{
			FacesMessages.mensErro("Falha na inserção no banco de dados!");
		}

		return goToBackPage("customers");

	}

	public CustomerController ()
	{
		super();
		loadSession();
	}

	/**
	 * Buscar todos os tipos de tabelas
	 * 
	 * @return
	 * @throws WebException
	 */
	public Map<String, Object> getTables () throws WebException
	{
		final Map<String, Object> itemMap = new LinkedHashMap<String, Object>();
		final List<TableVO> tables = InitProperties.getListPriceTable();
		if ((tables != null) && (tables.size() > 0))
		{
			for (final TableVO table : tables)
			{
				itemMap.put(table.getName(), table.getId());
			}
		}
		return itemMap;
	}

	public String viewCustomer ()
	{
		return goToPage("customer");
	}

	public String cancel ()
	{
		return goToPage("customers");
	}

	public String cancel_customer ()
	{
		return goToPage("resume");
	}

	public CustomerVO getCustomerVO ()
	{
		return customerVO;
	}

	public void setCustomerVO (final CustomerVO customerVO)
	{
		this.customerVO = customerVO;
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

	public BigDecimal getFactor_customer ()
	{
		return factor_customer;
	}

	public void setFactor_customer (final BigDecimal factor_customer)
	{
		this.factor_customer = factor_customer;
	}

	public Integer getIdTable ()
	{
		return idTable;
	}

	public void setIdTable (final Integer idTable)
	{
		this.idTable = idTable;
	}

	public boolean isInsert ()
	{
		return insert;
	}

	public void setInsert (final boolean insert)
	{
		this.insert = insert;
	}

	public boolean isReadonly ()
	{
		return readonly;
	}

	public void setReadonly (final boolean readonly)
	{
		this.readonly = readonly;
	}

	public boolean isCustomer_profile ()
	{
		return customer_profile;
	}

	public void setCustomer_profile (final boolean customer_profile)
	{
		this.customer_profile = customer_profile;
	}
}

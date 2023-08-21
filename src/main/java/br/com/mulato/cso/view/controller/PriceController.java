package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.PriceVO;
import br.com.mulato.cso.utils.InitProperties;
import br.com.mulato.cso.utils.vo.TableVO;
import br.com.mulato.cso.utils.vo.VehicleVO;
import br.com.mulato.cso.view.beans.FacesMessages;

public class PriceController extends AbstractController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(PriceController.class);

	private BusinessVO businessVO;

	private String label;

	private Integer id;

	private Integer idBusiness;

	private String business;

	private Integer idTable;

	private Integer idVehicle;

	private String local;

	private BigDecimal price;

	private boolean insert;

	private void loadSession ()
	{

		String profile;

		boolean isLogged = false;

		LOGGER.info("Carregando controle da página de entregas do negócio ...");

		try
		{

			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);

			isLogged = loginController.isLogged();

			if (isLogged)
			{

				LOGGER.info("Sessío carregada! ... Login: " + loginController.getUsername());

				profile = loginController.getProfile();

				if ((loginController.getUserIdLogged() == null) || (loginController.getUserIdLogged().intValue() <= 0))
				{

					throw new WebException("Id do usuário logado não encontrado.");

				}

				if ((loginController.getBusinessVO() == null) || (loginController.getBusinessVO().getId() == null) ||
				    (loginController.getBusinessVO().getId().intValue() <= 0))
				{

					throw new WebException("Negócio da sessão não encontrado.");

				}
				else
				{

					businessVO = loginController.getBusinessVO();

				}

				if (profile.equals("BUSINESS"))
				{

					// Edit Price

					label = "Editar";

					if (loginController.getId() != null)
					{

						final Integer id = loginController.getId();

						final PriceVO price = FactoryService.getInstancia().getPriceService().find(id);

						if (price != null)
						{

							setId(price.getId());

							if ((price.getBusiness() != null) && (price.getBusiness().getId() != null) && (price.getBusiness().getName() != null))
							{
								setIdBusiness(price.getBusiness().getId());
								setBusiness(price.getBusiness().getName());

							}

							if (price.getTable() != null)
							{

								final TableVO tableVO = InitProperties.getTableVO(price.getTable());

								if ((tableVO != null) && (tableVO.getId() != null))
								{
									setIdTable(tableVO.getId());
								}

							}

							if (price.getVehicle() != null)
							{

								final VehicleVO vehicleVO = InitProperties.getVehicleVO(price.getVehicle());

								if ((vehicleVO != null) && (vehicleVO.getId() != null))
								{
									setIdVehicle(vehicleVO.getId());
								}

							}

							setLocal(price.getLocal());
							setPrice(price.getPrice());

						}

					}
					else
					{
						// Add New Price

						label = "Incluir";

						insert = true;

						setIdBusiness(businessVO.getId());
						setBusiness(businessVO.getName());

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

		String path = "pricetable";

		boolean insert = false;

		int business_id = 0;

		try
		{

			if ((getId() == null) || getId().equals(new Integer(0)))
			{
				insert = true;
			}

			if ((getIdBusiness() == null) || getIdBusiness().equals(new Integer(0)))
			{
				throw new WebException("Informe id negócio!");
			}

			if (getLocal() == null)
			{
				throw new WebException("Informe endereço de destino!");
			}

			if (getLocal().equals(""))
			{
				throw new WebException("Informe endereço de destino!");
			}

			if ((getIdTable() == null) || getIdTable().equals(new Integer(0)))
			{
				throw new WebException("Informe nome da tabela!");
			}

			if ((getIdVehicle() == null) || getIdVehicle().equals(new Integer(0)))
			{
				throw new WebException("Informe veí­culo!");
			}

			if (getPrice() == null)
			{
				throw new WebException("Informe valor da corrida!");
			}

			if (getPrice().equals(new BigDecimal(0)))
			{
				throw new WebException("Informe valor da corrida!");
			}

			final PriceVO price = new PriceVO();

			if (insert)
			{
				price.setId(null);
			}
			else
			{
				price.setId(getId());
			}

			business_id = Integer.parseInt(getIdBusiness().toString());

			final BusinessVO business = new BusinessVO();
			business.setId(business_id);
			price.setBusiness(business);

			if (getIdTable() != null)
			{

				final TableVO tb = InitProperties.getTableVO(getIdTable());

				if ((tb != null) && (tb.getName() != null))
				{
					price.setTable(tb.getName());
				}
			}

			if (getIdVehicle() != null)
			{

				final VehicleVO vc = InitProperties.getVehicleVO(getIdVehicle());

				if ((vc != null) && (vc.getVehicle() != null))
				{
					price.setVehicle(vc.getVehicle());
				}
			}

			price.setLocal(getLocal());
			price.setPrice(getPrice());

			FactoryService.getInstancia().getPriceService().save(price);

			FacesMessages.mensInfo("Custo da corrida salvo com sucesso!");

			path = "pricetables";

		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final Exception e)
		{
			FacesMessages.mensErro("Falha na inserção no banco de dados!");
		}

		return goToBackPage(path);

	}

	public PriceController ()
	{
		super();
		loadSession();
	}

	/**
	 * Buscar todas as tabelas
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

	/**
	 * Buscar todos os veí­culos
	 * 
	 * @return
	 * @throws WebException
	 */
	public Map<String, Object> getVehicles () throws WebException
	{
		final Map<String, Object> itemMap = new LinkedHashMap<String, Object>();
		final List<VehicleVO> vehicles = InitProperties.getListVehicle();
		if ((vehicles != null) && (vehicles.size() > 0))
		{
			for (final VehicleVO vehicle : vehicles)
			{
				itemMap.put(vehicle.getVehicle(), vehicle.getId());
			}
		}
		return itemMap;
	}

	public String cancel ()
	{
		return goToPage("pricetables");
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

	public Integer getIdBusiness ()
	{
		return idBusiness;
	}

	public void setIdBusiness (final Integer idBusiness)
	{
		this.idBusiness = idBusiness;
	}

	public String getBusiness ()
	{
		return business;
	}

	public void setBusiness (final String business)
	{
		this.business = business;
	}

	public Integer getIdTable ()
	{
		return idTable;
	}

	public void setIdTable (final Integer idTable)
	{
		this.idTable = idTable;
	}

	public Integer getIdVehicle ()
	{
		return idVehicle;
	}

	public void setIdVehicle (final Integer idVehicle)
	{
		this.idVehicle = idVehicle;
	}

	public String getLocal ()
	{
		return local;
	}

	public void setLocal (final String local)
	{
		this.local = local;
	}

	public BigDecimal getPrice ()
	{
		return price;
	}

	public void setPrice (final BigDecimal price)
	{
		this.price = price;
	}

	public boolean isInsert ()
	{
		return insert;
	}

	public void setInsert (final boolean insert)
	{
		this.insert = insert;
	}
}

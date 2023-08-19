package br.com.mulato.cso.service.impl;

import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.PriceListVO;
import br.com.mulato.cso.model.PriceVO;
import br.com.mulato.cso.service.BusinessService;

public class BusinessServiceImpl
    implements BusinessService
{

	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = Logger.getLogger(BusinessServiceImpl.class);

	@Override
	public List<BusinessVO> listAllBusiness () throws WebException
	{
		List<BusinessVO> list = null;
		LOGGER.info("Listar todos os neg�cios.");
		try
		{
			list = FactoryDAO.getInstancia().getBusinessDAO().listAll();
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public void save (final BusinessVO business) throws WebException
	{
		if (business == null)
		{
			throw new WebException("Informe neg�cio.");
		}

		if (business.getName() == null)
		{
			throw new WebException("Informe nome do neg�cio!");
		}

		if (business.getRole() == null)
		{
			throw new WebException("Informe o perfil de neg�cio!");
		}

		if (!business.getRole().equals("BUSINESS"))
		{
			throw new WebException("Informe o perfil de neg�cio!");
		}

		if (business.getLogin() == null)
		{
			throw new WebException("Informe login do neg�cio!");
		}

		if (business.getLogin().getLogin() == null)
		{
			throw new WebException("Informe login do neg�cio!");
		}

		if (business.getId() == null)
		{
			if (business.getLogin().getPassword() == null)
			{
				throw new WebException("Informe senha do neg�cio!");
			}
			if (business.getLogin().getRepeat() == null)
			{
				throw new WebException("Repita senha do neg�cio!");
			}
			if (!business.getLogin().getPassword().equals(business.getLogin().getRepeat()))
			{
				throw new WebException("Repita senha corretamente!");
			}
		}

		if (business.getEmail() == null)
		{
			throw new WebException("Informe email do neg�cio!");
		}

		try
		{
			if (business.getId() == null)
			{
				LOGGER.info("Salvar neg�cio.");
				FactoryDAO.getInstancia().getBusinessDAO().insert(business);
			}
			else
			{
				LOGGER.info("Atualizar neg�cio.");
				FactoryDAO.getInstancia().getBusinessDAO().update(business);
			}
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}

	@Override
	public BusinessVO find (final Integer idBusiness) throws WebException
	{
		BusinessVO business = null;
		if ((idBusiness == null) || (idBusiness <= 0))
		{
			throw new WebException("Informe id neg�cio.");
		}
		LOGGER.info("Buscar neg�cio.");
		try
		{
			business = FactoryDAO.getInstancia().getBusinessDAO().find(idBusiness);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return business;
	}

	@Override
	public void delete (final Integer idBusiness) throws WebException
	{
		if ((idBusiness == null) || (idBusiness <= 0))
		{
			throw new WebException("Informe id neg�cio.");
		}
		LOGGER.info("Deletar neg�cio.");
		try
		{
			FactoryDAO.getInstancia().getBusinessDAO().delete(idBusiness);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}

	@Override
	public BusinessVO findBusinessByCustomerId (final Integer idCustomer) throws WebException
	{
		BusinessVO business = null;
		if (idCustomer == null)
		{
			throw new WebException("Informe id cliente.");
		}
		if (idCustomer <= 0)
		{
			throw new WebException("Informe id cliente.");
		}
		try
		{
			final CustomerVO customer = new CustomerVO();
			customer.setId(idCustomer);
			business = FactoryDAO.getInstancia().getBusinessDAO().findCustomerBusiness(customer);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return business;
	}

	@Override
	public BusinessVO findBusinessByCourierId (final Integer idCourier) throws WebException
	{
		BusinessVO business = null;
		if (idCourier == null)
		{
			throw new WebException("Informe id entregador.");
		}
		if (idCourier <= 0)
		{
			throw new WebException("Informe id entregador.");
		}
		try
		{
			final CourierVO courier = new CourierVO();
			courier.setId(idCourier);
			business = FactoryDAO.getInstancia().getBusinessDAO().findCourierBusiness(courier);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return business;
	}

	@Override
	public List<PriceListVO> listPriceListBusiness (final Integer ibBusiness) throws WebException
	{
		List<PriceListVO> list = null;
		if (ibBusiness == null)
		{
			throw new WebException("Informe id neg�cio.");
		}
		if (ibBusiness <= 0)
		{
			throw new WebException("Informe id neg�cio.");
		}
		LOGGER.info("Pesquisar listas de pre�os do neg�cio.");
		try
		{
			list = FactoryDAO.getInstancia().getPriceDAO().listAllPriceListBusiness(ibBusiness);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public List<PriceVO> listAllPriceBusiness (final Integer idBusiness) throws WebException
	{
		List<PriceVO> list = null;
		if (idBusiness == null)
		{
			throw new WebException("Informe id neg�cio.");
		}
		if (idBusiness <= 0)
		{
			throw new WebException("Informe id neg�cio.");
		}
		LOGGER.info("Pesquisar tabela de pre�os do neg�cio.");
		try
		{
			list = FactoryDAO.getInstancia().getPriceDAO().listAllPriceBusiness(idBusiness);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public List<PriceVO> listAllPriceBusinessByTable (final Integer idBusiness, final String table) throws WebException
	{
		List<PriceVO> list = null;
		if (idBusiness == null)
		{
			throw new WebException("Informe id neg�cio.");
		}
		if (idBusiness <= 0)
		{
			throw new WebException("Informe id neg�cio.");
		}
		if (table == null)
		{
			throw new WebException("Informe nome da tabela.");
		}
		if (table.equals(""))
		{
			throw new WebException("Informe nome da tabela.");
		}
		LOGGER.info("Pesquisar tabela de pre�os do neg�cio.");
		try
		{
			list = FactoryDAO.getInstancia().getPriceDAO().listAllPriceBusinessByTable(idBusiness, table);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public void deleteBusinessValues (final Integer idBusiness) throws WebException
	{
		if ((idBusiness == null) || (idBusiness <= 0))
		{
			throw new WebException("Informe id neg�cio.");
		}
		LOGGER.info("Deletar todas as listas de pre�os do neg�cio.");
		try
		{
			FactoryDAO.getInstancia().getPriceDAO().deleteBusinessValues(idBusiness);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}
}

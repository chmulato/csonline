package br.com.mulato.cso.service.impl;

import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.PriceVO;
import br.com.mulato.cso.service.PriceService;

public class PriceServiceImpl
    implements PriceService
{

	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = Logger.getLogger(PriceServiceImpl.class);

	@Override
	public PriceVO find (final Integer idPrice) throws WebException
	{
		PriceVO price = null;
		if ((idPrice == null) || (idPrice <= 0))
		{
			throw new WebException("Informe id preço.");
		}
		LOGGER.info("Buscar preço.");
		try
		{
			price = FactoryDAO.getInstancia().getPriceDAO().find(idPrice);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return price;
	}

	@Override
	public void save (final PriceVO price) throws WebException
	{

		if (price == null)
		{
			throw new WebException("Informe preço!");
		}

		if (price.getBusiness() == null)
		{
			throw new WebException("Informe o negócio!");
		}

		if (price.getBusiness().getId() == null)
		{
			throw new WebException("Informe o negócio!");
		}

		if (price.getBusiness().getId() <= 0)
		{
			throw new WebException("Informe o negócio!");
		}

		if (price.getTable() == null)
		{
			throw new WebException("Informe tabela de preço!");
		}

		if (price.getTable().equals(""))
		{
			throw new WebException("Informe tabela de preço!");
		}

		if (price.getVehicle() == null)
		{
			throw new WebException("Informe tipo de transporte!");
		}

		if (price.getVehicle().equals(""))
		{
			throw new WebException("Informe tipo de transporte!");
		}

		if (price.getLocal() == null)
		{
			throw new WebException("Informe local!");
		}

		if (price.getLocal().equals(""))
		{
			throw new WebException("Informe local!");
		}

		try
		{
			if (price.getId() == null)
			{
				LOGGER.info("Salvar preço.");
				FactoryDAO.getInstancia().getPriceDAO().insert(price);
			}
			else
			{
				LOGGER.info("Atualizar preço.");
				FactoryDAO.getInstancia().getPriceDAO().update(price);
			}
		}
		catch (final WebException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}

	@Override
	public void delete (final Integer idPrice) throws WebException
	{
		if ((idPrice == null) || (idPrice <= 0))
		{
			throw new WebException("Informe id preço.");
		}
		LOGGER.info("Deletar preço.");
		try
		{
			FactoryDAO.getInstancia().getPriceDAO().delete(idPrice);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}
}

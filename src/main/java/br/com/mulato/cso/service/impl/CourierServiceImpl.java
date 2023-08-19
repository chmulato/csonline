package br.com.mulato.cso.service.impl;

import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.service.CourierService;

public class CourierServiceImpl implements CourierService {

	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = Logger.getLogger(CourierServiceImpl.class);

	@Override
	public List<CourierVO> listAllCourierBusiness (final BusinessVO business) throws WebException
	{

		List<CourierVO> list = null;

		if (business == null)
		{
			throw new WebException("Informe negócio.");
		}

		if (business.getId() == null)
		{
			throw new WebException("Informe id negócio!");
		}

		if (business.getId() <= 0)
		{
			throw new WebException("Informe id negócio!");
		}

		LOGGER.info("Listar todos os entregadores do negócio.");

		try
		{
			list = FactoryDAO.getInstancia().getCourierDAO().listAllCourierBusiness(business);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public CourierVO find (final Integer idCourier) throws WebException
	{

		CourierVO courier = null;

		if ((idCourier == null) || (idCourier <= 0))
		{
			throw new WebException("Informe id entregador.");
		}

		LOGGER.info("Buscar entregador.");

		try
		{
			courier = FactoryDAO.getInstancia().getCourierDAO().find(idCourier, true);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return courier;
	}

	@Override
	public void save (final CourierVO courier, final boolean update_password) throws WebException
	{

		if (courier == null)
		{
			throw new DAOException("Informe entregador!");
		}

		if (courier.getName() == null)
		{
			throw new DAOException("Informe nome do entregador!");
		}

		if (courier.getRole() == null)
		{
			throw new DAOException("Informe o perfil de entregador!");
		}

		if (!courier.getRole().equals("COURIER"))
		{
			throw new DAOException("Informe o perfil de entregador!");
		}

		if (courier.getLogin() == null)
		{
			throw new DAOException("Informe login do entregador!");
		}

		if (courier.getLogin().getLogin() == null)
		{
			throw new DAOException("Informe login do entregador!");
		}

		if (courier.getId() == null)
		{

			if (courier.getLogin().getPassword() == null)
			{
				throw new WebException("Informe senha do entregador!");
			}

			if (courier.getLogin().getRepeat() == null)
			{
				throw new WebException("Repita senha do entregador!");
			}

			if (!courier.getLogin().getPassword().equals(courier.getLogin().getRepeat()))
			{
				throw new WebException("Repita senha corretamente!");
			}

		}
		else
		{
			if (update_password)
			{
				if (courier.getLogin().getPassword() == null)
				{
					throw new WebException("Informe senha do entregador!");
				}
				if (courier.getLogin().getRepeat() == null)
				{
					throw new WebException("Repita senha do entregador!");
				}
				if (!courier.getLogin().getPassword().equals(courier.getLogin().getRepeat()))
				{
					throw new WebException("Repita senha corretamente!");
				}
			}
		}

		if (courier.getEmail() == null)
		{
			throw new DAOException("Informe email do entregador!");
		}

		if (courier.getFactor_courier() == null)
		{
			throw new DAOException("Informe fator do entregador!");
		}

		if (courier.getBusiness() == null)
		{
			throw new DAOException("Informe negócio do entregador!");
		}

		if (courier.getBusiness().getId() == null)
		{
			throw new DAOException("Informe negócio do entregador!");
		}

		if (courier.getBusiness().getId() <= 0)
		{
			throw new DAOException("Informe negócio do entregador!");
		}

		try
		{
			if (courier.getId() == null)
			{
				LOGGER.info("Salvar entregador.");
				FactoryDAO.getInstancia().getCourierDAO().insert(courier);
			}
			else
			{
				LOGGER.info("Atualizar entregador.");
				FactoryDAO.getInstancia().getCourierDAO().update(courier, update_password);
			}
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}

	@Override
	public void delete (final Integer idCourier) throws WebException
	{

		if ((idCourier == null) || (idCourier <= 0))
		{
			throw new WebException("Informe id entregador.");
		}

		LOGGER.info("Deletar entregador.");

		try
		{
			FactoryDAO.getInstancia().getCourierDAO().delete(idCourier);
		}
		catch (final DAOException e)
		{
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
	}
}

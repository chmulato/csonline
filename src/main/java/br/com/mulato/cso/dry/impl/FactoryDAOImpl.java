package br.com.mulato.cso.dry.impl;

import br.com.mulato.cso.dao.BusinessDAO;
import br.com.mulato.cso.dao.CourierDAO;
import br.com.mulato.cso.dao.CustomerDAO;
import br.com.mulato.cso.dao.DeliveryDAO;
import br.com.mulato.cso.dao.LoginDAO;
import br.com.mulato.cso.dao.PriceDAO;
import br.com.mulato.cso.dao.SmsDAO;
import br.com.mulato.cso.dao.UserDAO;
import br.com.mulato.cso.dao.impl.BusinessDAOImpl;
import br.com.mulato.cso.dao.impl.CourierDAOImpl;
import br.com.mulato.cso.dao.impl.CustomerDAOImpl;
import br.com.mulato.cso.dao.impl.DeliveryDAOImpl;
import br.com.mulato.cso.dao.impl.LoginDAOImpl;
import br.com.mulato.cso.dao.impl.PriceDAOImpl;
import br.com.mulato.cso.dao.impl.SmsDAOImpl;
import br.com.mulato.cso.dao.impl.UserDAOImpl;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;

public class FactoryDAOImpl
    extends FactoryDAO
{
	
	@Override
	public LoginDAO getLoginDAO () throws DAOException
	{
		LoginDAO loginDAO = new LoginDAOImpl();
		
		if (getTransaction() == Boolean.TRUE)
		{
			loginDAO.setTransaction_active(TRANSACTION_ENABLE);
		} 
		
		if (getTransaction() == Boolean.FALSE)
		{
			loginDAO.setTransaction_active(TRANSACTION_DISABLE);
		} 
		
		return loginDAO;
	}

	@Override
	public UserDAO getUserDAO () throws DAOException
	{
		UserDAO userDAO = new UserDAOImpl();
		
		if (getTransaction() == Boolean.TRUE)
		{
			userDAO.setTransaction_active(TRANSACTION_ENABLE);
		}
		
		if (getTransaction() == Boolean.FALSE)
		{
			userDAO.setTransaction_active(TRANSACTION_DISABLE);
		}
		
		return userDAO;
	}

	@Override
	public BusinessDAO getBusinessDAO () throws DAOException
	{
		BusinessDAO businessDAO = new BusinessDAOImpl();
		
		if (getTransaction() == Boolean.TRUE)
		{
			businessDAO.setTransaction_active(TRANSACTION_ENABLE);
		}
		
		if (getTransaction() == Boolean.FALSE)
		{
			businessDAO.setTransaction_active(TRANSACTION_DISABLE);
		}
		
		return businessDAO;
	}

	@Override
	public CustomerDAO getCustomerDAO () throws DAOException
	{
		CustomerDAO customerDAO = new CustomerDAOImpl();
		
		if (getTransaction() == Boolean.TRUE)
		{
			customerDAO.setTransaction_active(TRANSACTION_ENABLE);
		}
		
		if (getTransaction() == Boolean.FALSE)
		{
			customerDAO.setTransaction_active(TRANSACTION_DISABLE);
		}
		
		return customerDAO;
	}

	@Override
	public CourierDAO getCourierDAO () throws DAOException
	{
		CourierDAO courierDAO  = new CourierDAOImpl();
		
		if (getTransaction() == Boolean.TRUE)
		{
			courierDAO.setTransaction_active(TRANSACTION_ENABLE);
		}
		
		if (getTransaction() == Boolean.FALSE)
		{
			courierDAO.setTransaction_active(TRANSACTION_DISABLE);
		}
		
		return courierDAO;
	}

	@Override
	public DeliveryDAO getDeliveryDAO () throws DAOException
	{
		DeliveryDAO deliveryDAO = new DeliveryDAOImpl();
		
		if (getTransaction() == Boolean.TRUE)
		{
			deliveryDAO.setTransaction_active(TRANSACTION_ENABLE);
		}
		
		if (getTransaction() == Boolean.FALSE)
		{
			deliveryDAO.setTransaction_active(TRANSACTION_DISABLE);
		}
		
		return deliveryDAO;
	}

	@Override
	public SmsDAO getSmsDAO () throws DAOException
	{
		SmsDAO smsDAO = new SmsDAOImpl();
		
		if (getTransaction() == Boolean.TRUE)
		{
			smsDAO.setTransaction_active(TRANSACTION_ENABLE);
		}
		
		if (getTransaction() == Boolean.FALSE)
		{
			smsDAO.setTransaction_active(TRANSACTION_DISABLE);
		}
		
		return smsDAO;
	}

	@Override
	public PriceDAO getPriceDAO () throws DAOException
	{
		PriceDAO priceDAO = new PriceDAOImpl();
		
		if (getTransaction() == Boolean.TRUE)
		{
			priceDAO.setTransaction_active(TRANSACTION_ENABLE);
		}
		
		if (getTransaction() == Boolean.FALSE)
		{
			priceDAO.setTransaction_active(TRANSACTION_DISABLE);
		}
		
		return priceDAO;
	}
}

package br.com.mulato.cso.dry;

import org.apache.log4j.Logger;
import br.com.mulato.cso.dao.BusinessDAO;
import br.com.mulato.cso.dao.CourierDAO;
import br.com.mulato.cso.dao.CustomerDAO;
import br.com.mulato.cso.dao.DeliveryDAO;
import br.com.mulato.cso.dao.LoginDAO;
import br.com.mulato.cso.dao.PriceDAO;
import br.com.mulato.cso.dao.SmsDAO;
import br.com.mulato.cso.dao.UserDAO;
import br.com.mulato.cso.dry.impl.FactoryDAOImpl;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.utils.InitProperties;

/**
 * @author Christian Mulato
 * @date October/05th/2013
 */
// Classe abstrata DAO Factory
public abstract class FactoryDAO
{
	
	private final static Logger LOGGER = Logger.getLogger(FactoryDAO.class);
	
	private static FactoryDAO instancia;
	
	protected static final boolean TRANSACTION_ENABLE = true;
	
	protected static final boolean TRANSACTION_DISABLE = false;
	
	private static boolean transaction_active = false;

	public abstract LoginDAO getLoginDAO () throws DAOException;

	public abstract UserDAO getUserDAO () throws DAOException;

	public abstract BusinessDAO getBusinessDAO () throws DAOException;

	public abstract CustomerDAO getCustomerDAO () throws DAOException;

	public abstract CourierDAO getCourierDAO () throws DAOException;

	public abstract DeliveryDAO getDeliveryDAO () throws DAOException;

	public abstract PriceDAO getPriceDAO () throws DAOException;

	public abstract SmsDAO getSmsDAO () throws DAOException;

	public static FactoryDAO getInstancia ()
	{
		try
		{
			if (InitProperties.getSingletonDAO())
			{
				if (instancia == null)
				{
					LOGGER.info("Criação da instância FactoryDAOImpl.");
					instancia = new FactoryDAOImpl();
				}
				return instancia;
			}
			else
			{
				LOGGER.info("Criação da classe FactoryDAOImpl.");
				return new FactoryDAOImpl();
			}
		}
		catch (final ParameterException e)
		{
			LOGGER.error("Erro ao definir tipo de instanciamento da classe. " + e.getMessage());
		}
		return new FactoryDAOImpl();
	}
	
	protected static boolean getTransaction() throws DAOException
	{
		return transaction_active;
	}
	
	public static boolean onTransaction() throws DAOException
	{
		transaction_active = TRANSACTION_ENABLE;
		LOGGER.info("Acesso ao banco de dados - Transacao ativada ...");
		return transaction_active;
	}
	
	public static boolean offTransaction() throws DAOException
	{
		transaction_active = TRANSACTION_DISABLE;
		LOGGER.info("Acesso ao banco de dados - Transacao desativa ...");
		return transaction_active;
	}

}

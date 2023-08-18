package br.com.mulato.cso.dry;

import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.impl.FactoryServiceImpl;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.service.AdminService;
import br.com.mulato.cso.service.BusinessService;
import br.com.mulato.cso.service.CourierService;
import br.com.mulato.cso.service.CustomerService;
import br.com.mulato.cso.service.DeliveryService;
import br.com.mulato.cso.service.LoginService;
import br.com.mulato.cso.service.PriceService;
import br.com.mulato.cso.utils.InitProperties;

/**
 * @author Christian Mulato
 * @date October/10th/2013
 */
// Classe abstrata Factory Service
public abstract class FactoryService
{

	private final static Logger LOGGER = Logger.getLogger(FactoryService.class);

	private static FactoryService instancia;

	public abstract AdminService getAdminService () throws WebException;

	public abstract BusinessService getBusinessService () throws WebException;

	public abstract CourierService getCourierService () throws WebException;

	public abstract CustomerService getCustomerService () throws WebException;

	public abstract DeliveryService getDeliveryService () throws WebException;

	public abstract PriceService getPriceService () throws WebException;

	public abstract LoginService getLoginService () throws WebException;

	public static FactoryService getInstancia ()
	{
		try
		{
			if (InitProperties.getSingletonService())
			{
				if (instancia == null)
				{
					LOGGER.info("Criação da instância FactoryServiceImpl.");
					instancia = new FactoryServiceImpl();
				}
				return instancia;
			}
			else
			{
				LOGGER.info("Criação da classe FactoryServiceImpl.");
				return new FactoryServiceImpl();
			}
		}
		catch (final ParameterException e)
		{
			LOGGER.error("Erro ao definir tipo de instanciamento da classe. " + e.getMessage());
		}
		return new FactoryServiceImpl();
	}
}

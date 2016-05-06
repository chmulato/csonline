package br.com.mulato.cso.dry.impl;

import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.service.AdminService;
import br.com.mulato.cso.service.BusinessService;
import br.com.mulato.cso.service.CourierService;
import br.com.mulato.cso.service.CustomerService;
import br.com.mulato.cso.service.DeliveryService;
import br.com.mulato.cso.service.LoginService;
import br.com.mulato.cso.service.PriceService;
import br.com.mulato.cso.service.impl.AdminServiceImpl;
import br.com.mulato.cso.service.impl.BusinessServiceImpl;
import br.com.mulato.cso.service.impl.CourierServiceImpl;
import br.com.mulato.cso.service.impl.CustomerServiceImpl;
import br.com.mulato.cso.service.impl.DeliveryServiceImpl;
import br.com.mulato.cso.service.impl.LoginServiceImpl;
import br.com.mulato.cso.service.impl.PriceServiceImpl;

public class FactoryServiceImpl
    extends FactoryService
{

	@Override
	public AdminService getAdminService () throws WebException
	{
		return new AdminServiceImpl();
	}

	@Override
	public BusinessService getBusinessService () throws WebException
	{
		return new BusinessServiceImpl();
	}

	@Override
	public CourierService getCourierService () throws WebException
	{
		return new CourierServiceImpl();
	}

	@Override
	public CustomerService getCustomerService () throws WebException
	{
		return new CustomerServiceImpl();
	}

	@Override
	public DeliveryService getDeliveryService () throws WebException
	{
		return new DeliveryServiceImpl();
	}

	@Override
	public PriceService getPriceService () throws WebException
	{
		return new PriceServiceImpl();
	}

	@Override
	public LoginService getLoginService () throws WebException
	{
		return new LoginServiceImpl();
	}
}

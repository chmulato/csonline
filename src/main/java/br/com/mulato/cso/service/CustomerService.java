package br.com.mulato.cso.service;

import java.io.Serializable;
import java.util.List;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.PriceVO;

public interface CustomerService
    extends Serializable
{

	public List<CustomerVO> listAllCustomerBusiness (BusinessVO business) throws WebException;

	public List<PriceVO> listAllPriceCustomer (String nameTable) throws WebException;

	public void save (CustomerVO customer) throws WebException;

	public CustomerVO find (Integer idCustomer) throws WebException;

	public void delete (Integer idCustomer) throws WebException;

}

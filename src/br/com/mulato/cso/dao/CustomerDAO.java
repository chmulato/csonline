
package br.com.mulato.cso.dao;

import java.util.List;

import br.com.mulato.cso.dry.InterfaceSQL;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CustomerVO;

public interface CustomerDAO extends InterfaceSQL
{

	public CustomerVO findDeliveryCustomer (Integer idCustomer) throws DAOException;

	public CustomerVO find (Integer id, boolean full) throws DAOException;

	public void insert (CustomerVO customer) throws DAOException;

	public void update (CustomerVO customer) throws DAOException;

	public void delete (Integer id) throws DAOException;

	public List<CustomerVO> listAllCustomerBusiness (BusinessVO business) throws DAOException;

}

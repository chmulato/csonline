
package br.com.mulato.cso.dao;

import java.util.List;

import br.com.mulato.cso.dry.InterfaceSQL;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.DeliveryVO;

public interface BusinessDAO extends InterfaceSQL
{

	public BusinessVO findCustomerBusiness (CustomerVO customer) throws DAOException;

	public BusinessVO findCourierBusiness (CourierVO courier) throws DAOException;

	public BusinessVO findDeliveryBusiness (DeliveryVO delivery) throws DAOException;

	public BusinessVO find (Integer id) throws DAOException;

	public void insert (BusinessVO business) throws DAOException;

	public void update (BusinessVO business) throws DAOException;

	public void delete (Integer id) throws DAOException;

	public List<BusinessVO> listAll () throws DAOException;

}

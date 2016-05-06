package br.com.mulato.cso.dao;

import java.util.List;

import br.com.mulato.cso.dry.InterfaceSQL;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.DeliveryVO;

public interface DeliveryDAO extends InterfaceSQL
{

	public void delete (Integer id) throws DAOException;

	public DeliveryVO find (Integer id) throws DAOException;

	public int insert (DeliveryVO delivery) throws DAOException;

	public List<DeliveryVO> listAllDeliveryBusinessCompleted (BusinessVO business, boolean completed) throws DAOException;

	public List<DeliveryVO> listAllDeliveryCourierCompleted (CourierVO courier, boolean completed) throws DAOException;

	public List<DeliveryVO> listAllDeliveryCustomerCompleted (CustomerVO customer, boolean completed) throws DAOException;

	public List<DeliveryVO> listAllDeliveriesOpen () throws DAOException;

	public void update (DeliveryVO delivery) throws DAOException;

}

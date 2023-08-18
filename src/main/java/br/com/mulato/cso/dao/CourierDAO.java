package br.com.mulato.cso.dao;

import java.util.List;

import br.com.mulato.cso.dry.InterfaceSQL;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;

public interface CourierDAO extends InterfaceSQL
{

	public CourierVO findDeliveryCourier (Integer idCourier) throws DAOException;

	public CourierVO find (Integer id, boolean full) throws DAOException;

	public void insert (CourierVO courier) throws DAOException;

	public void update (CourierVO courier, boolean update_password) throws DAOException;

	public void delete (Integer id) throws DAOException;

	public List<CourierVO> listAllCouriersWorkers () throws DAOException;

	public List<CourierVO> listAllCourierBusiness (BusinessVO business) throws DAOException;

}

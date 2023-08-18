package br.com.mulato.cso.dao;

import java.util.List;

import br.com.mulato.cso.dry.InterfaceSQL;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.model.PriceListVO;
import br.com.mulato.cso.model.PriceVO;

public interface PriceDAO extends InterfaceSQL
{

	public PriceVO find (Integer id) throws DAOException;

	public void delete (Integer id) throws DAOException;

	public void deleteBusinessValues (Integer idBusiness) throws DAOException;

	public int insert (PriceVO price) throws DAOException;

	public void update (PriceVO price) throws DAOException;

	public List<PriceVO> listAllPriceCustomer (String nameTable) throws DAOException;

	public List<PriceVO> listAllPriceBusiness (Integer idBusiness) throws DAOException;

	public List<PriceVO> listAllPriceBusinessByTable (Integer idBusiness, String table) throws DAOException;

	public List<PriceListVO> listAllPriceListBusiness (Integer idBusiness) throws DAOException;

}

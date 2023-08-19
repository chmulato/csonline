package br.com.mulato.cso.service;

import java.io.Serializable;
import java.util.List;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.PriceListVO;
import br.com.mulato.cso.model.PriceVO;

public interface BusinessService extends Serializable {

	public List<BusinessVO> listAllBusiness () throws WebException;

	public List<PriceListVO> listPriceListBusiness (Integer ibBusiness) throws WebException;

	public List<PriceVO> listAllPriceBusiness (Integer idBusiness) throws WebException;

	public List<PriceVO> listAllPriceBusinessByTable (Integer idBusiness, String table) throws WebException;

	public void save (BusinessVO business) throws WebException;

	public BusinessVO find (Integer idBusiness) throws WebException;

	public BusinessVO findBusinessByCustomerId (Integer idCustomer) throws WebException;

	public BusinessVO findBusinessByCourierId (Integer idCourier) throws WebException;

	public void delete (Integer idBusiness) throws WebException;

	public void deleteBusinessValues (Integer idBusiness) throws WebException;

}

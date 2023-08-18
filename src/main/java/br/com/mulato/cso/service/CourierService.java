package br.com.mulato.cso.service;

import java.io.Serializable;
import java.util.List;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;

public interface CourierService
    extends Serializable
{

	public List<CourierVO> listAllCourierBusiness (BusinessVO business) throws WebException;

	public CourierVO find (Integer idCourier) throws WebException;

	public void save (CourierVO courier, boolean update_password) throws WebException;

	public void delete (Integer idCourier) throws WebException;
}

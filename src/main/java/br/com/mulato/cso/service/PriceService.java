package br.com.mulato.cso.service;

import java.io.Serializable;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.PriceVO;

public interface PriceService
    extends Serializable
{

	public PriceVO find (Integer idPrice) throws WebException;

	public void save (PriceVO price) throws WebException;

	public void delete (Integer idPrice) throws WebException;

}

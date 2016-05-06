package br.com.mulato.cso.dao;

import java.util.List;

import br.com.mulato.cso.dry.InterfaceSQL;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.model.SmsVO;

public interface SmsDAO extends InterfaceSQL
 {

	public int insert (SmsVO sms) throws DAOException;

	public void update (SmsVO sms) throws DAOException;

	public SmsVO find (Integer id) throws DAOException;

	public List<SmsVO> listAllSmsDelivery (Integer idDelivery) throws DAOException;

	public List<SmsVO> listAllSmsMobile (String mobile) throws DAOException;

}

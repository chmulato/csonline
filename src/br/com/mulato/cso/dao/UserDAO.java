package br.com.mulato.cso.dao;

import java.util.List;

import br.com.mulato.cso.dry.InterfaceSQL;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.model.UserVO;

public interface UserDAO extends InterfaceSQL
{

	public UserVO find (Integer id, boolean getPasswordTo) throws DAOException;

	public UserVO findByLogin (LoginVO login) throws DAOException;

	public List<UserVO> listAll () throws DAOException;

	public List<BusinessVO> listAllBusiness () throws DAOException;

}

package br.com.mulato.cso.dao;

import br.com.mulato.cso.dry.InterfaceSQL;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.model.LoginVO;

public interface LoginDAO extends InterfaceSQL
{

	public void authenticate (LoginVO login) throws DAOException;

	public void passwordChange (LoginVO login) throws DAOException;

	public boolean isThereLogin (LoginVO login) throws DAOException;

}

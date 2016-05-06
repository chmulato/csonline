package br.com.mulato.cso.service;

import java.io.Serializable;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.LoginVO;

public interface LoginService
    extends Serializable
{

	public Boolean authenticate (LoginVO login) throws WebException;

	public void changePassword (LoginVO login, boolean send_mail) throws WebException;

}

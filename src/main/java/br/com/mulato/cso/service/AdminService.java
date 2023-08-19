package br.com.mulato.cso.service;

import java.io.Serializable;
import java.util.List;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.model.UserVO;

public interface AdminService extends Serializable {

	public List<UserVO> listAllUsers () throws WebException;

	public UserVO find (Integer id) throws WebException;

	public UserVO findGetPasswordTo (Integer id) throws WebException;

	public UserVO findByLogin (LoginVO login) throws WebException;

}

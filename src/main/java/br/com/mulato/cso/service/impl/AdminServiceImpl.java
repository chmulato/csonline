package br.com.mulato.cso.service.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.model.UserVO;
import br.com.mulato.cso.service.AdminService;

public class AdminServiceImpl implements AdminService {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(AdminServiceImpl.class);

	@Override
	public List<UserVO> listAllUsers() throws WebException {
		List<UserVO> list = null;
		LOGGER.info("Listando todos os usuários.");
		try {
			FactoryDAO.onTransaction();
			list = FactoryDAO.getInstancia().getUserDAO().listAll();
			FactoryDAO.offTransaction();
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao listar usuários: " + e.getMessage());
		}
		return list;
	}

	@Override
	public UserVO find(final Integer id) throws WebException {
		UserVO user = null;
		if ((id == null) || (id <= 0)) {
			throw new WebException("Informe o id do usuário.");
		}
		LOGGER.info("Pesquisando usuário pelo id.");
		try {
			user = FactoryDAO.getInstancia().getUserDAO().find(id, false);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao pesquisar usuário: " + e.getMessage());
		}
		return user;
	}

	@Override
	public UserVO findByLogin(final LoginVO login) throws WebException {
		UserVO user = null;
		if (login == null) {
			throw new WebException("Informe o usuário.");
		}
		if (login.getLogin() == null) {
			throw new WebException("Informe o usuário.");
		}
		LOGGER.info("Pesquisando usuário por login.");
		try {
			user = FactoryDAO.getInstancia().getUserDAO().findByLogin(login);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao pesquisar usuário por login: " + e.getMessage());
		}
		return user;
	}

	@Override
	public UserVO findGetPasswordTo(final Integer id) throws WebException {
		UserVO user = null;
		if ((id == null) || (id <= 0)) {
			throw new WebException("Informe o id do usuário.");
		}
		LOGGER.info("Pesquisando usuário pelo id.");
		try {
			user = FactoryDAO.getInstancia().getUserDAO().find(id, true);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao pesquisar usuário para recuperação de senha: " + e.getMessage());
		}
		return user;
	}
}

package br.com.mulato.cso.service.impl;

import java.util.List;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.PriceVO;
import br.com.mulato.cso.service.CustomerService;

public class CustomerServiceImpl
		implements CustomerService {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(CustomerServiceImpl.class);

	@Override
	public List<CustomerVO> listAllCustomerBusiness(final BusinessVO business) throws WebException {

		List<CustomerVO> list = null;

		if (business == null) {
			throw new WebException("Informe o negócio.");
		}

		if (business.getId() == null) {
			throw new WebException("Informe o id do negócio!");
		}

		if (business.getId() <= 0) {
			throw new WebException("Informe o id do negócio!");
		}

		LOGGER.info("Listando todos os clientes do negócio.");

		try {
			list = FactoryDAO.getInstancia().getCustomerDAO().listAllCustomerBusiness(business);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException(e.getMessage());
		}
		return list;
	}

	@Override
	public void save(final CustomerVO customer) throws WebException {

		if (customer == null) {
			throw new DAOException("Informe o cliente!");
		}

		if (customer.getName() == null) {
			throw new DAOException("Informe o nome do cliente!");
		}

		if (customer.getRole() == null) {
			throw new DAOException("Informe o perfil do cliente!");
		}

		if (!customer.getRole().equals("CUSTOMER")) {
			throw new DAOException("Informe o perfil do cliente!");
		}

		if (customer.getLogin() == null) {
			throw new DAOException("Informe o login do cliente!");
		}

		if (customer.getLogin().getLogin() == null) {
			throw new DAOException("Informe o login do cliente!");
		}

		if (customer.getId() == null) {

			if (customer.getLogin().getPassword() == null) {
				throw new WebException("Informe a senha do cliente!");
			}

			if (customer.getLogin().getRepeat() == null) {
				throw new WebException("Repita a senha do cliente!");
			}

			if (!customer.getLogin().getPassword().equals(customer.getLogin().getRepeat())) {
				throw new WebException("Repita a senha corretamente!");
			}

		}

		if (customer.getEmail() == null) {
			throw new DAOException("Informe o e-mail do cliente!");
		}

		if (customer.getFactor_customer() == null) {
			throw new DAOException("Informe o fator do cliente!");
		}

		if (customer.getBusiness() == null) {
			throw new DAOException("Informe o negócio do cliente!");
		}

		if (customer.getBusiness().getId() == null) {
			throw new DAOException("Informe o negócio do cliente!");
		}

		if (customer.getBusiness().getId() <= 0) {
			throw new DAOException("Informe o negócio do cliente!");
		}

		try {
			if (customer.getId() == null) {
				LOGGER.info("Salvando cliente.");
				FactoryDAO.getInstancia().getCustomerDAO().insert(customer);
			} else {
				LOGGER.info("Atualizando cliente.");
				FactoryDAO.getInstancia().getCustomerDAO().update(customer);
			}
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao salvar/atualizar cliente: " + e.getMessage());
		}
	}

	@Override
	public CustomerVO find(final Integer idCustomer) throws WebException {

		CustomerVO customer = null;

		if ((idCustomer == null) || (idCustomer <= 0)) {
			throw new WebException("Informe o id do cliente.");
		}

		LOGGER.info("Buscando cliente.");

		try {
			customer = FactoryDAO.getInstancia().getCustomerDAO().find(idCustomer, true);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao buscar cliente: " + e.getMessage());
		}
		return customer;
	}

	@Override
	public void delete(final Integer idCustomer) throws WebException {

		if ((idCustomer == null) || (idCustomer <= 0)) {
			throw new WebException("Informe o id do cliente.");
		}

		LOGGER.info("Deletando cliente.");

		try {
			FactoryDAO.getInstancia().getCustomerDAO().delete(idCustomer);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao deletar cliente: " + e.getMessage());
		}
	}

	@Override
	public List<PriceVO> listAllPriceCustomer(final String nameTable) throws WebException {

		List<PriceVO> list = null;

		if (nameTable == null) {
			throw new WebException("Informe o nome da tabela de preço do cliente.");
		}

		if (nameTable.equals("")) {
			throw new WebException("Informe o nome da tabela de preço do cliente.");
		}

		LOGGER.info("Pesquisando lista de preços do cliente.");

		try {
			list = FactoryDAO.getInstancia().getPriceDAO().listAllPriceCustomer(nameTable);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao buscar lista de preços do cliente: " + e.getMessage());
		}
		return list;
	}
}

package br.com.mulato.cso.service.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.PriceListVO;
import br.com.mulato.cso.model.PriceVO;
import br.com.mulato.cso.service.BusinessService;

public class BusinessServiceImpl
		implements BusinessService {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(BusinessServiceImpl.class);

	@Override
	public List<BusinessVO> listAllBusiness() throws WebException {
		List<BusinessVO> list = null;
		LOGGER.info("Listando todos os negócios.");
		try {
			list = FactoryDAO.getInstancia().getBusinessDAO().listAll();
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao listar negócios: " + e.getMessage());
		}
		return list;
	}

	@Override
	public void save(final BusinessVO business) throws WebException {
		if (business == null) {
			throw new WebException("Informe o negócio.");
		}

		if (business.getName() == null) {
			throw new WebException("Informe o nome do negócio!");
		}

		if (business.getRole() == null) {
			throw new WebException("Informe o perfil do negócio!");
		}

		if (!business.getRole().equals("BUSINESS")) {
			throw new WebException("Informe o perfil do negócio!");
		}

		if (business.getLogin() == null) {
			throw new WebException("Informe o login do negócio!");
		}

		if (business.getLogin().getLogin() == null) {
			throw new WebException("Informe o login do negócio!");
		}

		if (business.getId() == null) {
			if (business.getLogin().getPassword() == null) {
				throw new WebException("Informe a senha do negócio!");
			}
			if (business.getLogin().getRepeat() == null) {
				throw new WebException("Repita a senha do negócio!");
			}
			if (!business.getLogin().getPassword().equals(business.getLogin().getRepeat())) {
				throw new WebException("Repita a senha corretamente!");
			}
		}

		if (business.getEmail() == null) {
			throw new WebException("Informe o e-mail do negócio!");
		}

		try {
			if (business.getId() == null) {
				LOGGER.info("Salvando negócio.");
				FactoryDAO.getInstancia().getBusinessDAO().insert(business);
			} else {
				LOGGER.info("Atualizando negócio.");
				FactoryDAO.getInstancia().getBusinessDAO().update(business);
			}
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao salvar/atualizar negócio: " + e.getMessage());
		}
	}

	@Override
	public BusinessVO find(final Integer idBusiness) throws WebException {
		BusinessVO business = null;
		if ((idBusiness == null) || (idBusiness <= 0)) {
			throw new WebException("Informe o id do negócio.");
		}
		LOGGER.info("Buscando negócio.");
		try {
			business = FactoryDAO.getInstancia().getBusinessDAO().find(idBusiness);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao buscar negócio: " + e.getMessage());
		}
		return business;
	}

	@Override
	public void delete(final Integer idBusiness) throws WebException {
		if ((idBusiness == null) || (idBusiness <= 0)) {
			throw new WebException("Informe o id do negócio.");
		}
		LOGGER.info("Deletando negócio.");
		try {
			FactoryDAO.getInstancia().getBusinessDAO().delete(idBusiness);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao deletar negócio: " + e.getMessage());
		}
	}

	@Override
	public BusinessVO findBusinessByCustomerId(final Integer idCustomer) throws WebException {
		BusinessVO business = null;
		if (idCustomer == null) {
			throw new WebException("Informe o id do cliente.");
		}
		if (idCustomer <= 0) {
			throw new WebException("Informe o id do cliente.");
		}
		try {
			final CustomerVO customer = new CustomerVO();
			customer.setId(idCustomer);
			business = FactoryDAO.getInstancia().getBusinessDAO().findCustomerBusiness(customer);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao buscar negócio do cliente: " + e.getMessage());
		}
		return business;
	}

	@Override
	public BusinessVO findBusinessByCourierId(final Integer idCourier) throws WebException {
		BusinessVO business = null;
		if (idCourier == null) {
			throw new WebException("Informe o id do entregador.");
		}
		if (idCourier <= 0) {
			throw new WebException("Informe o id do entregador.");
		}
		try {
			final CourierVO courier = new CourierVO();
			courier.setId(idCourier);
			business = FactoryDAO.getInstancia().getBusinessDAO().findCourierBusiness(courier);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao buscar negócio do entregador: " + e.getMessage());
		}
		return business;
	}

	@Override
	public List<PriceListVO> listPriceListBusiness(final Integer ibBusiness) throws WebException {
		List<PriceListVO> list = null;
		if (ibBusiness == null) {
			throw new WebException("Informe o id do negócio.");
		}
		if (ibBusiness <= 0) {
			throw new WebException("Informe o id do negócio.");
		}
		LOGGER.info("Pesquisando listas de preços do negócio.");
		try {
			list = FactoryDAO.getInstancia().getPriceDAO().listAllPriceListBusiness(ibBusiness);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao buscar listas de preços do negócio: " + e.getMessage());
		}
		return list;
	}

	@Override
	public List<PriceVO> listAllPriceBusiness(final Integer idBusiness) throws WebException {
		List<PriceVO> list = null;
		if (idBusiness == null) {
			throw new WebException("Informe o id do negócio.");
		}
		if (idBusiness <= 0) {
			throw new WebException("Informe o id do negócio.");
		}
		LOGGER.info("Pesquisando tabela de preços do negócio.");
		try {
			list = FactoryDAO.getInstancia().getPriceDAO().listAllPriceBusiness(idBusiness);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao buscar tabela de preços do negócio: " + e.getMessage());
		}
		return list;
	}

	@Override
	public List<PriceVO> listAllPriceBusinessByTable(final Integer idBusiness, final String table) throws WebException {
		List<PriceVO> list = null;
		if (idBusiness == null) {
			throw new WebException("Informe o id do negócio.");
		}
		if (idBusiness <= 0) {
			throw new WebException("Informe o id do negócio.");
		}
		if (table == null) {
			throw new WebException("Informe o nome da tabela.");
		}
		if (table.equals("")) {
			throw new WebException("Informe o nome da tabela.");
		}
		LOGGER.info("Pesquisando tabela de preços do negócio.");
		try {
			list = FactoryDAO.getInstancia().getPriceDAO().listAllPriceBusinessByTable(idBusiness, table);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao buscar tabela de preços do negócio: " + e.getMessage());
		}
		return list;
	}

	@Override
	public void deleteBusinessValues(final Integer idBusiness) throws WebException {
		if ((idBusiness == null) || (idBusiness <= 0)) {
			throw new WebException("Informe o id do negócio.");
		}
		LOGGER.info("Deletando todas as listas de preços do negócio.");
		try {
			FactoryDAO.getInstancia().getPriceDAO().deleteBusinessValues(idBusiness);
		} catch (final DAOException e) {
			LOGGER.error("Service error: " + e.getMessage());
			throw new WebException("Erro ao deletar listas de preços do negócio: " + e.getMessage());
		}
	}
}

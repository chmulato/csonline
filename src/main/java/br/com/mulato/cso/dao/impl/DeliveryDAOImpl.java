package br.com.mulato.cso.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dao.DeliveryDAO;
import br.com.mulato.cso.dry.DBConnection;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.utils.InitProperties;
import br.com.mulato.cso.utils.ToolUtils;

public class DeliveryDAOImpl implements DeliveryDAO, Serializable {

	private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(DeliveryDAOImpl.class);

	private BusinessVO findBusiness(final Integer idBusiness) throws DAOException {
		return FactoryDAO.getInstancia().getBusinessDAO().find(idBusiness);
	}

	private CustomerVO findCustomer(final Integer idCustomer) throws DAOException {
		return FactoryDAO.getInstancia().getCustomerDAO().find(idCustomer, false);
	}

	private CourierVO findCourier(final Integer idCourier) throws DAOException {
		return FactoryDAO.getInstancia().getCourierDAO().find(idCourier, false);
	}

	@Override
	public void setTransaction_active(boolean enable) throws DAOException {

		if (enable == TRANSACTION_ENABLE) {
			DBConnection.onTransaction();
		}

		if (enable == TRANSACTION_DISABLE) {
			DBConnection.offTransaction();
		}

	}

	@Override
	public DeliveryVO find(final Integer id) throws DAOException {

		final ToolUtils tools = new ToolUtils();
		DeliveryVO result = null;

		if (id == null) {
			throw new DAOException("Informe Id entrega!");
		}

		if (id.intValue() <= 0) {
			throw new DAOException("Informe Id entrega!");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			final String SQL = SELECT_DELIVERY_BY_ID;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql()) {
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, id);

			rs = stmt.executeQuery();

			if (rs.next()) {

				result = new DeliveryVO();

				result.setId(rs.getInt(rs.findColumn("ID")));

				if (rs.getString("DATETIME") != null) {
					final String datetime = rs.getString("DATETIME");
					result.setDatetime(tools.converteDataStringToDateUtil(datetime, "yyyy-MM-dd hh:mm:ss"));
				}

				result.setDatetime(rs.getDate(rs.findColumn("DATETIME")));
				result.setStart(rs.getString(rs.findColumn("START")));
				result.setDestination(rs.getString("DESTINATION"));
				result.setContact(rs.getString("CONTACT"));
				result.setDescription(rs.getString("DESCRIPTION"));
				result.setVolume(rs.getBigDecimal("VOLUME"));
				result.setWeight(rs.getBigDecimal("WEIGHT"));
				result.setKm(rs.getBigDecimal("KM"));
				result.setAdditionalCost(rs.getBigDecimal("ADDITIONAL_COST"));
				result.setCost(rs.getBigDecimal("COST"));

				if (rs.getString("RECEIVED") != null) {
					final String str_received = rs.getString("RECEIVED");
					if (str_received.charAt(0) == '0') {
						result.setReceived(false);
					} else {
						result.setReceived(true);
					}
				}

				if (rs.getString("COMPLETED") != null) {
					final String str_completed = rs.getString("COMPLETED");
					if (str_completed.charAt(0) == '0') {
						result.setCompleted(false);
					} else {
						result.setCompleted(true);
					}
				}

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0) {
					BusinessVO vo = new BusinessVO();
					vo = findBusiness(idBusiness);
					result.setBusiness(vo);
				}

				final int idCustomer = rs.getInt(rs.findColumn("IDCUSTOMER"));
				if (idCustomer > 0) {
					CustomerVO vo = new CustomerVO();
					vo = findCustomer(idCustomer);
					result.setCustomer(vo);
				}

				final int idCourier = rs.getInt(rs.findColumn("IDCOURIER"));
				if (idCourier > 0) {
					CourierVO vo = new CourierVO();
					vo = findCourier(idCourier);
					result.setCourier(vo);
				}
			}

			if (InitProperties.getViewSql()) {
				logger.info("SQL: OK!");
			}

		} catch (final ParameterException ex) {
			final String msg = "Erro ao pesquisar entrega! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} catch (final SQLException ex) {
			final String msg = "Erro ao pesquisar entrega! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} finally {
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return result;
	}

	@SuppressWarnings("resource")
	@Override
	public int insert(final DeliveryVO delivery) throws DAOException {

		int deliveryId = 0;

		if (delivery == null) {
			throw new DAOException("Informe entrega!");
		}

		if (delivery.getBusiness() == null) {
			throw new DAOException("Informe negócio!");
		}

		if (delivery.getBusiness().getId() == null) {
			throw new DAOException("Informe negócio!");
		}

		if (delivery.getCustomer() == null) {
			throw new DAOException("Informe cliente!");
		}

		if (delivery.getCustomer().getId() == null) {
			throw new DAOException("Informe cliente!");
		}

		if (delivery.getContact() == null) {
			throw new DAOException("Informe contato!");
		}

		if (delivery.getDescription() == null) {
			throw new DAOException("Informe descrição da entrega!");
		}

		if (delivery.getKm() == null) {
			throw new DAOException("Informe distância!");
		}

		if (delivery.getCost() == null) {
			throw new DAOException("Informe valor!");
		}

		logger.info("Salvar informações da entrega.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			final String SQL = INSERT_DELIVERY;

			conn = DBConnection.getConnectionDB();

			DBConnection.onTransaction();

			if (InitProperties.getViewSql()) {
				logger.info("INSERT: " + SQL);
			}

			stmt = conn.prepareStatement(GET_LAST_ID_ON_DELIVERY_TABLE);
			rs = stmt.executeQuery();

			if (rs.next()) {
				deliveryId = rs.getInt(1);
			}

			deliveryId = new Integer(deliveryId + 1);

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, deliveryId);
			stmt.setInt(2, delivery.getBusiness().getId());
			stmt.setInt(3, delivery.getCustomer().getId());

			if ((delivery.getCourier() == null) || (delivery.getCourier().getId() == null)) {
				stmt.setNull(4, java.sql.Types.INTEGER);
			} else {
				stmt.setInt(4, (delivery.getCourier().getId()));
			}

			stmt.setString(5, delivery.getStart());
			stmt.setString(6, delivery.getDestination());
			stmt.setString(7, delivery.getContact());
			stmt.setString(8, delivery.getDescription());
			stmt.setBigDecimal(9, delivery.getVolume());
			stmt.setBigDecimal(10, delivery.getWeight());
			stmt.setBigDecimal(11, delivery.getKm());
			stmt.setBigDecimal(12, delivery.getAdditionalCost());
			stmt.setBigDecimal(13, delivery.getCost());
			stmt.setBoolean(14, delivery.getReceived());
			stmt.setBoolean(15, delivery.getCompleted());

			stmt.executeUpdate();

			DBConnection.offTransaction();

			if (InitProperties.getViewSql()) {
				logger.info("INSERT: OK!");
			}

		} catch (final ParameterException ex) {

			final String msg = "Erro ao salvar entrega! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		} catch (final SQLException ex) {

			final String msg = "Erro ao salvar entrega! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		} finally {
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return deliveryId;
	}

	@Override
	public void update(final DeliveryVO delivery) throws DAOException {

		if (delivery == null) {
			throw new DAOException("Informe entrega!");
		}

		if (delivery.getId() == null) {
			throw new DAOException("Informe id entrega!");
		}

		if (delivery.getBusiness() == null) {
			throw new DAOException("Informe negócio!");
		}

		if (delivery.getBusiness().getId() == null) {
			throw new DAOException("Informe negócio!");
		}

		if (delivery.getCustomer() == null) {
			throw new DAOException("Informe cliente!");
		}

		if (delivery.getCustomer().getId() == null) {
			throw new DAOException("Informe cliente!");
		}

		if (delivery.getContact() == null) {
			throw new DAOException("Informe contato!");
		}

		if (delivery.getDescription() == null) {
			throw new DAOException("Informe descrição da entrega!");
		}

		if (delivery.getKm() == null) {
			throw new DAOException("Informe distância!");
		}

		if (delivery.getCost() == null) {
			throw new DAOException("Informe valor!");
		}

		logger.info("Atualizar informações da entrega.");

		Connection conn = null;
		PreparedStatement stmt = null;
		final ResultSet rs = null;

		try {

			final String SQL = UPDATE_DELIVERY_BY_ID;

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql()) {
				logger.info("UPDATE: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, delivery.getBusiness().getId());
			stmt.setInt(2, delivery.getCustomer().getId());

			if ((delivery.getCourier() == null) || (delivery.getCourier().getId() == null)) {
				stmt.setNull(3, java.sql.Types.INTEGER);
			} else {
				stmt.setInt(3, (delivery.getCourier().getId()));
			}

			stmt.setString(4, delivery.getStart());
			stmt.setString(5, delivery.getDestination());
			stmt.setString(6, delivery.getContact());
			stmt.setString(7, delivery.getDescription());
			stmt.setBigDecimal(8, delivery.getVolume());
			stmt.setBigDecimal(9, delivery.getWeight());
			stmt.setBigDecimal(10, delivery.getKm());
			stmt.setBigDecimal(11, delivery.getAdditionalCost());
			stmt.setBigDecimal(12, delivery.getCost());
			stmt.setBoolean(13, delivery.getReceived());
			stmt.setBoolean(14, delivery.getCompleted());
			stmt.setInt(15, delivery.getId());

			stmt.executeUpdate();

			if (InitProperties.getViewSql()) {
				logger.info("UPDATE: OK!");
			}

		} catch (final ParameterException ex) {
			final String msg = "Erro ao atualizar entrega! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} catch (final SQLException ex) {
			final String msg = "Erro ao atualizar entrega! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} finally {
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void delete(final Integer id) throws DAOException {

		int count = 0;

		if ((id == null) || (id.intValue() <= 0)) {
			throw new DAOException("Informe id entrega!");
		}

		logger.info("Deletar entrega que ainda não está a caminho.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			final String SQL = DELETE_DELIVERY_BY_ID;

			if (InitProperties.getViewSql()) {
				logger.info("DELETE: " + SQL);
			}

			conn = DBConnection.getConnectionDB();

			DBConnection.onTransaction();

			stmt = conn.prepareStatement(SELECT_DELIVERY_NOT_RECEIVED);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}

			if (count == 0) {
				stmt = conn.prepareStatement(SQL);
				stmt.setInt(1, id);
				stmt.executeUpdate();
			} else {
				throw new SQLException("Esta entrega já esta a caminho!");
			}

			DBConnection.offTransaction();

			if (InitProperties.getViewSql()) {
				logger.info("DELETE: OK!");
			}

		} catch (final ParameterException ex) {
			final String msg = "Erro ao deletar entrega! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);
		} catch (final SQLException ex) {
			final String msg = "Erro ao deletar entrega! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);
		} finally {
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@Override
	public List<DeliveryVO> listAllDeliveryBusinessCompleted(final BusinessVO business, final boolean completed)
			throws DAOException {

		final ToolUtils tools = new ToolUtils();
		List<DeliveryVO> result = null;

		boolean thereIs = false;

		if (business == null) {
			throw new DAOException("Informe negócio!");
		}

		if (business.getId() == null) {
			throw new DAOException("Informe id do negócio!");
		}

		logger.info("Pesquisar lista de entregas do negócio.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			String SQL = SELECT_ALL_DELIVERY_BUSINESS_NOT_COMPLETED;

			if (completed) {
				SQL = SELECT_ALL_DELIVERY_BUSINESS_COMPLETED;
			}

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql()) {
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, business.getId());
			rs = stmt.executeQuery();

			while (rs.next()) {

				if (!thereIs) {
					result = new ArrayList<DeliveryVO>();
					thereIs = true;
				}

				final DeliveryVO delivery = new DeliveryVO();

				delivery.setId(rs.getInt(rs.findColumn("ID")));

				if (rs.getString("DATETIME") != null) {
					final String datetime = rs.getString("DATETIME");
					delivery.setDatetime(tools.converteDataStringToDateUtil(datetime, "yyyy-MM-dd hh:mm:ss"));
				}

				delivery.setStart(rs.getString(rs.findColumn("START")));
				delivery.setDestination(rs.getString("DESTINATION"));
				delivery.setContact(rs.getString("CONTACT"));
				delivery.setDescription(rs.getString("DESCRIPTION"));
				delivery.setVolume(rs.getBigDecimal("VOLUME"));
				delivery.setWeight(rs.getBigDecimal("WEIGHT"));
				delivery.setKm(rs.getBigDecimal("KM"));
				delivery.setAdditionalCost(rs.getBigDecimal("ADDITIONAL_COST"));
				delivery.setCost(rs.getBigDecimal("COST"));

				if (rs.getString("RECEIVED") != null) {
					final String str_received = rs.getString("RECEIVED");
					if (str_received.charAt(0) == '0') {
						delivery.setReceived(false);
					} else {
						delivery.setReceived(true);
					}
				}

				if (rs.getString("COMPLETED") != null) {
					final String str_completed = rs.getString("COMPLETED");
					if (str_completed.charAt(0) == '0') {
						delivery.setCompleted(false);
					} else {
						delivery.setCompleted(true);
					}
				}

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0) {
					BusinessVO vo = new BusinessVO();
					vo = findBusiness(idBusiness);
					delivery.setBusiness(vo);
				}

				final int idCustomer = rs.getInt(rs.findColumn("IDCUSTOMER"));
				if (idCustomer > 0) {
					CustomerVO vo = new CustomerVO();
					vo = findCustomer(idCustomer);
					delivery.setCustomer(vo);
				}

				final int idCourier = rs.getInt(rs.findColumn("IDCOURIER"));
				if (idCourier > 0) {
					CourierVO vo = new CourierVO();
					vo = findCourier(idCourier);
					delivery.setCourier(vo);
				}

				result.add(delivery);

			}

			if (InitProperties.getViewSql()) {
				logger.info("SQL: OK!");
			}

		} catch (final ParameterException ex) {
			final String msg = "Erro ao pesquisar lista de entregas do negócio! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} catch (final SQLException ex) {
			final String msg = "Erro ao pesquisar lista de entregas do negócio! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} finally {
			DBConnection.closeConnection(conn, stmt, rs);
		}
		return result;
	}

	@Override
	public List<DeliveryVO> listAllDeliveryCustomerCompleted(final CustomerVO customer, final boolean completed)
			throws DAOException {

		final ToolUtils tools = new ToolUtils();
		List<DeliveryVO> result = null;

		boolean thereIs = false;

		if (customer == null) {
			throw new DAOException("Informe cliente!");
		}

		if (customer.getId() == null) {
			throw new DAOException("Informe id cliente!");
		}

		logger.info("Pesquisar lista de entregas do cliente.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			String SQL = SELECT_ALL_DELIVERY_CUSTOMER_NOT_COMPLETED;

			if (completed) {
				SQL = SELECT_ALL_DELIVERY_CUSTOMER_COMPLETED;
			}

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql()) {
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, customer.getId());
			rs = stmt.executeQuery();

			while (rs.next()) {

				if (!thereIs) {
					result = new ArrayList<DeliveryVO>();
					thereIs = true;
				}

				final DeliveryVO delivery = new DeliveryVO();

				delivery.setId(rs.getInt(rs.findColumn("ID")));

				if (rs.getString("DATETIME") != null) {
					final String datetime = rs.getString("DATETIME");
					delivery.setDatetime(tools.converteDataStringToDateUtil(datetime, "yyyy-MM-dd hh:mm:ss"));
				}

				delivery.setStart(rs.getString(rs.findColumn("START")));
				delivery.setDestination(rs.getString("DESTINATION"));
				delivery.setContact(rs.getString("CONTACT"));
				delivery.setDescription(rs.getString("DESCRIPTION"));
				delivery.setVolume(rs.getBigDecimal("VOLUME"));
				delivery.setWeight(rs.getBigDecimal("WEIGHT"));
				delivery.setKm(rs.getBigDecimal("KM"));
				delivery.setAdditionalCost(rs.getBigDecimal("ADDITIONAL_COST"));
				delivery.setCost(rs.getBigDecimal("COST"));

				if (rs.getString("RECEIVED") != null) {
					final String str_received = rs.getString("RECEIVED");
					if (str_received.charAt(0) == '0') {
						delivery.setReceived(false);
					} else {
						delivery.setReceived(true);
					}
				}

				if (rs.getString("COMPLETED") != null) {
					final String str_completed = rs.getString("COMPLETED");
					if (str_completed.charAt(0) == '0') {
						delivery.setCompleted(false);
					} else {
						delivery.setCompleted(true);
					}
				}

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0) {
					BusinessVO vo = new BusinessVO();
					vo = findBusiness(idBusiness);
					delivery.setBusiness(vo);
				}

				final int idCustomer = rs.getInt(rs.findColumn("IDCUSTOMER"));
				if (idCustomer > 0) {
					CustomerVO vo = new CustomerVO();
					vo = findCustomer(idCustomer);
					delivery.setCustomer(vo);
				}

				final int idCourier = rs.getInt(rs.findColumn("IDCOURIER"));
				if (idCourier > 0) {
					CourierVO vo = new CourierVO();
					vo = findCourier(idCourier);
					delivery.setCourier(vo);
				}

				result.add(delivery);

			}

			if (InitProperties.getViewSql()) {
				logger.info("SQL: OK!");
			}

		} catch (final ParameterException ex) {
			final String msg = "Erro ao pesquisar lista de entregas do cliente! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} catch (final SQLException ex) {
			final String msg = "Erro ao pesquisar lista de entregas do cliente! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} finally {
			DBConnection.closeConnection(conn, stmt, rs);
		}
		return result;
	}

	@Override
	public List<DeliveryVO> listAllDeliveryCourierCompleted(final CourierVO courier, final boolean completed)
			throws DAOException {

		final ToolUtils tools = new ToolUtils();
		List<DeliveryVO> result = null;

		boolean thereIs = false;

		if (courier == null) {
			throw new DAOException("Informe entregador!");
		}

		if (courier.getId() == null) {
			throw new DAOException("Informe id entregador!");
		}

		logger.info("Pesquisar lista de entregas do entregador.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			String SQL = SELECT_ALL_DELIVERY_COURIER_NOT_COMPLETED;

			if (completed) {
				SQL = SELECT_ALL_DELIVERY_COURIER_COMPLETED;
			}

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql()) {
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, courier.getId());
			rs = stmt.executeQuery();

			while (rs.next()) {

				if (!thereIs) {
					result = new ArrayList<DeliveryVO>();
					thereIs = true;
				}

				final DeliveryVO delivery = new DeliveryVO();

				delivery.setId(rs.getInt(rs.findColumn("ID")));

				if (rs.getString("DATETIME") != null) {
					final String datetime = rs.getString("DATETIME");
					delivery.setDatetime(tools.converteDataStringToDateUtil(datetime, "yyyy-MM-dd hh:mm:ss"));
				}

				delivery.setStart(rs.getString(rs.findColumn("START")));
				delivery.setDestination(rs.getString("DESTINATION"));
				delivery.setContact(rs.getString("CONTACT"));
				delivery.setDescription(rs.getString("DESCRIPTION"));
				delivery.setVolume(rs.getBigDecimal("VOLUME"));
				delivery.setWeight(rs.getBigDecimal("WEIGHT"));
				delivery.setKm(rs.getBigDecimal("KM"));
				delivery.setAdditionalCost(rs.getBigDecimal("ADDITIONAL_COST"));
				delivery.setCost(rs.getBigDecimal("COST"));

				if (rs.getString("RECEIVED") != null) {
					final String str_received = rs.getString("RECEIVED");
					if (str_received.charAt(0) == '0') {
						delivery.setReceived(false);
					} else {
						delivery.setReceived(true);
					}
				}

				if (rs.getString("COMPLETED") != null) {
					final String str_completed = rs.getString("COMPLETED");
					if (str_completed.charAt(0) == '0') {
						delivery.setCompleted(false);
					} else {
						delivery.setCompleted(true);
					}
				}

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0) {
					BusinessVO vo = new BusinessVO();
					vo = findBusiness(idBusiness);
					delivery.setBusiness(vo);
				}

				final int idCustomer = rs.getInt(rs.findColumn("IDCUSTOMER"));
				if (idCustomer > 0) {
					CustomerVO vo = new CustomerVO();
					vo = findCustomer(idCustomer);
					delivery.setCustomer(vo);
				}

				final int idCourier = rs.getInt(rs.findColumn("IDCOURIER"));
				if (idCourier > 0) {
					CourierVO vo = new CourierVO();
					vo = findCourier(idCourier);
					delivery.setCourier(vo);
				}

				result.add(delivery);

			}

			if (InitProperties.getViewSql()) {
				logger.info("SQL: OK!");
			}

		} catch (final ParameterException ex) {
			final String msg = "Erro ao pesquisar lista de entregas do entregador! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} catch (final SQLException ex) {
			final String msg = "Erro ao pesquisar lista de entregas do entregador! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} finally {
			DBConnection.closeConnection(conn, stmt, rs);
		}
		return result;
	}

	@Override
	public List<DeliveryVO> listAllDeliveriesOpen() throws DAOException {

		final ToolUtils tools = new ToolUtils();
		List<DeliveryVO> result = null;

		boolean thereIs = false;

		logger.info("Pesquisar lista de entregas não completadas.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			final String SQL = SELECT_ALL_DELIVERY_NOT_COMPLETED;

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql()) {
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			rs = stmt.executeQuery();

			while (rs.next()) {

				if (!thereIs) {
					result = new ArrayList<DeliveryVO>();
					thereIs = true;
				}

				final DeliveryVO delivery = new DeliveryVO();

				delivery.setId(rs.getInt(rs.findColumn("ID")));

				if (rs.getString("DATETIME") != null) {
					final String datetime = rs.getString("DATETIME");
					delivery.setDatetime(tools.converteDataStringToDateUtil(datetime, "yyyy-MM-dd hh:mm:ss"));
				}

				delivery.setStart(rs.getString(rs.findColumn("START")));
				delivery.setDestination(rs.getString("DESTINATION"));
				delivery.setContact(rs.getString("CONTACT"));
				delivery.setDescription(rs.getString("DESCRIPTION"));
				delivery.setVolume(rs.getBigDecimal("VOLUME"));
				delivery.setWeight(rs.getBigDecimal("WEIGHT"));
				delivery.setKm(rs.getBigDecimal("KM"));
				delivery.setAdditionalCost(rs.getBigDecimal("ADDITIONAL_COST"));
				delivery.setCost(rs.getBigDecimal("COST"));

				if (rs.getString("RECEIVED") != null) {
					final String str_received = rs.getString("RECEIVED");
					if (str_received.charAt(0) == '0') {
						delivery.setReceived(false);
					} else {
						delivery.setReceived(true);
					}
				}

				if (rs.getString("COMPLETED") != null) {
					final String str_completed = rs.getString("COMPLETED");
					if (str_completed.charAt(0) == '0') {
						delivery.setCompleted(false);
					} else {
						delivery.setCompleted(true);
					}
				}

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0) {
					BusinessVO vo = new BusinessVO();
					vo = findBusiness(idBusiness);
					delivery.setBusiness(vo);
				}

				final int idCustomer = rs.getInt(rs.findColumn("IDCUSTOMER"));
				if (idCustomer > 0) {
					CustomerVO vo = new CustomerVO();
					vo = findCustomer(idCustomer);
					delivery.setCustomer(vo);
				}

				final int idCourier = rs.getInt(rs.findColumn("IDCOURIER"));
				if (idCourier > 0) {
					CourierVO vo = new CourierVO();
					vo = findCourier(idCourier);
					delivery.setCourier(vo);
				}

				result.add(delivery);

			}

			if (InitProperties.getViewSql()) {
				logger.info("SQL: OK!");
			}

		} catch (final ParameterException ex) {
			final String msg = "Erro ao pesquisar lista de entregas não completadas! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} catch (final SQLException ex) {
			final String msg = "Erro ao pesquisar lista de entregas não completadas! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		} finally {
			DBConnection.closeConnection(conn, stmt, rs);
		}
		return result;
	}
}

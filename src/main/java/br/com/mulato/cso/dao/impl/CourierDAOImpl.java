package br.com.mulato.cso.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dao.CourierDAO;
import br.com.mulato.cso.dry.DBConnection;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.utils.InitProperties;

public class CourierDAOImpl 
    implements CourierDAO, Serializable
{

	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(CourierDAOImpl.class);

	private boolean isThereLogin (final LoginVO login) throws DAOException
	{
		return FactoryDAO.getInstancia().getLoginDAO().isThereLogin(login);
	}

	private BusinessVO findBusiness (final Integer idBusiness) throws DAOException
	{
		return FactoryDAO.getInstancia().getBusinessDAO().find(idBusiness);
	}

	private List<DeliveryVO> listAllDeliveryCourierNotCompleted (final CourierVO courier) throws DAOException
	{
		return FactoryDAO.getInstancia().getDeliveryDAO().listAllDeliveryCourierCompleted(courier, false);
	}

	@Override
	public void setTransaction_active(boolean enable) throws DAOException {
		
		if (enable == TRANSACTION_ENABLE)
		{
			DBConnection.onTransaction();
		}

		if (enable == TRANSACTION_DISABLE)
		{
			DBConnection.offTransaction();
		}
		
	}

	@Override
	public CourierVO find (final Integer id, final boolean full) throws DAOException
	{

		CourierVO result = null;

		if (id == null)
		{
			throw new DAOException("Informe Id entregador!");
		}

		if (id.intValue() <= 0)
		{
			throw new DAOException("Informe Id entregador!");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_COURIER_BY_ID;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, id);

			rs = stmt.executeQuery();

			if (rs.next())
			{

				final LoginVO login = new LoginVO();

				result = new CourierVO();

				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setRole(rs.getString(rs.findColumn("ROLE")));
				result.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				result.setLogin(login);

				result.setEmail(rs.getString(rs.findColumn("EMAIL")));
				result.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				result.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				result.setMobile(rs.getString(rs.findColumn("MOBILE")));

				result.setFactor_courier(rs.getBigDecimal(rs.findColumn("FACTOR_COURIER")));

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));

				if (idBusiness > 0)
				{
					BusinessVO vo = null;
					if (full)
					{
						vo = findBusiness(idBusiness);
					}
					else
					{
						vo = new BusinessVO();
						vo.setId(idBusiness);
					}
					result.setBusiness(vo);
				}

				if (full)
				{
					final List<DeliveryVO> list = listAllDeliveryCourierNotCompleted(result);
					if ((list != null) && (list.size() > 0))
					{
						result.setDeliveries(list);
					}
				}

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar entregador! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar entregador! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return result;
	}

	@SuppressWarnings("resource")
	@Override
	public void insert (final CourierVO courier) throws DAOException
	{

		int idCourier = 0;
		int id_team_table = 0;

		if (courier == null)
		{
			throw new DAOException("Informe entregador!");
		}

		if (courier.getRole() == null)
		{
			throw new DAOException("Informe o perfil de entregador!");
		}

		if (!courier.getRole().equals("COURIER"))
		{
			throw new DAOException("Informe perfil do entregador!");
		}

		if (courier.getName() == null)
		{
			throw new DAOException("Informe nome do entregador!");
		}

		if (courier.getLogin() == null)
		{
			throw new DAOException("Informe login do entregador!");
		}

		if (courier.getLogin().getLogin() == null)
		{
			throw new DAOException("Informe login do entregador!");
		}

		if (courier.getLogin().getPassword() == null)
		{
			throw new DAOException("Informe senha do entregador!");
		}

		if (courier.getEmail() == null)
		{
			throw new DAOException("Informe email do entregador!");
		}

		if (courier.getMobile() == null)
		{
			throw new DAOException("Informe número do celular do entregador!");
		}

		if (courier.getFactor_courier() == null)
		{
			throw new DAOException("Informe fator do entregador!");
		}

		if (courier.getBusiness() == null)
		{
			throw new DAOException("Informe negócio do entregador!");
		}

		if (courier.getBusiness().getId() == null)
		{
			throw new DAOException("Informe negócio do entregador!");
		}

		if (courier.getBusiness().getId().intValue() <= 0)
		{
			throw new DAOException("Informe negócio do entregador!");
		}

		if (isThereLogin(courier.getLogin()))
		{
			throw new DAOException("Login já existente!");
		}

		logger.info("Salvar informações do entregador.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = INSERT_COURIER_01;

			conn = DBConnection.getConnectionDB();

			DBConnection.onTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("INSERT: " + SQL);
			}

			stmt = conn.prepareStatement(GET_LAST_ID_ON_USER_TABLE);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				idCourier = rs.getInt(1);
			}

			idCourier = (idCourier + 1);

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, idCourier);
			stmt.setString(2, courier.getRole());
			stmt.setString(3, courier.getName());
			stmt.setString(4, courier.getLogin().getLogin());
			stmt.setString(5, courier.getLogin().getPassword());
			stmt.setString(6, courier.getEmail());
			stmt.setString(7, courier.getEmail2());
			stmt.setString(8, courier.getAddress());
			stmt.setString(9, courier.getMobile());

			stmt.executeUpdate();

			stmt = conn.prepareStatement(GET_LAST_ID_ON_TEAM_TABLE);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				id_team_table = rs.getInt(1);
			}

			id_team_table = (id_team_table + 1);

			stmt = conn.prepareStatement(INSERT_COURIER_02);

			stmt.setInt(1, id_team_table);
			stmt.setInt(2, courier.getBusiness().getId());
			stmt.setInt(3, idCourier);
			stmt.setBigDecimal(4, courier.getFactor_courier());

			stmt.executeUpdate();

			DBConnection.offTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("INSERT: OK!");
			}

		}
		catch (final ParameterException ex)
		{

			final String msg = "Erro ao salvar entregador! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		catch (final SQLException ex)
		{

			final String msg = "Erro ao salvar entregador! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void update (final CourierVO courier, final boolean update_password) throws DAOException
	{

		if (courier == null)
		{
			throw new DAOException("Informe entregador!");
		}

		if (courier.getId() == null)
		{
			throw new DAOException("Informe id entregador!");
		}

		if (courier.getId().intValue() <= 0)
		{
			throw new DAOException("Informe id entregador!");
		}

		if (courier.getRole() == null)
		{
			throw new DAOException("Informe o perfil de entregador!");
		}

		if (!courier.getRole().equals("COURIER"))
		{
			throw new DAOException("Informe perfil do entregador!");
		}

		if (courier.getName() == null)
		{
			throw new DAOException("Informe nome do entregador!");
		}

		if (courier.getLogin() == null)
		{
			throw new DAOException("Informe login do entregador!");
		}

		if (courier.getLogin().getLogin() == null)
		{
			throw new DAOException("Informe login do entregador!");
		}

		if (update_password)
		{

			if (courier.getLogin().getPassword() == null)
			{
				throw new DAOException("Informe senha do entregador!");
			}

		}

		if (courier.getEmail() == null)
		{
			throw new DAOException("Informe email do entregador!");
		}

		if (courier.getMobile() == null)
		{
			throw new DAOException("Informe número do celular do entregador!");
		}

		if (courier.getFactor_courier() == null)
		{
			throw new DAOException("Informe fator do entregador!");
		}

		if (courier.getBusiness() == null)
		{
			throw new DAOException("Informe negócio do entregador!");
		}

		if (courier.getBusiness().getId() == null)
		{
			throw new DAOException("Informe negócio do entregador!");
		}

		if (courier.getBusiness().getId().intValue() <= 0)
		{
			throw new DAOException("Informe negócio do entregador!");
		}

		logger.info("Atualizar informações do entregador.");

		Connection conn = null;
		PreparedStatement stmt = null;
		final ResultSet rs = null;

		try
		{

			String SQL = UPDATE_COURIER_01;

			if (update_password)
			{
				SQL = UPDATE_COURIER_03;
			}

			conn = DBConnection.getConnectionDB();
			
			DBConnection.onTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("UPDATE: " + SQL);
			}

			int i = 1;

			stmt = conn.prepareStatement(SQL);

			stmt.setString(i++, courier.getName());

			if (update_password)
			{
				stmt.setString(i++, courier.getLogin().getPassword());
			}

			stmt.setString(i++, courier.getEmail());
			stmt.setString(i++, courier.getEmail2());
			stmt.setString(i++, courier.getAddress());
			stmt.setString(i++, courier.getMobile());
			stmt.setInt(i++, courier.getId());

			stmt.executeUpdate();

			stmt = conn.prepareStatement(UPDATE_COURIER_02);

			stmt.setBigDecimal(1, courier.getFactor_courier());
			stmt.setInt(2, courier.getBusiness().getId());
			stmt.setInt(3, courier.getId());

			stmt.executeUpdate();

			DBConnection.offTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("UPDATE: OK!");
			}

		}
		catch (final ParameterException ex)
		{

			final String msg = "Erro ao atualizar entregador! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		catch (final SQLException ex)
		{

			final String msg = "Erro ao atualizar entregador! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void delete (final Integer id) throws DAOException
	{

		int count = 0;

		if (id == null)
		{
			throw new DAOException("Informe id entregador!");
		}

		if (id.intValue() <= 0)
		{
			throw new DAOException("Informe id entregador!");
		}

		logger.info("Deletar informações do entregador.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = DELETE_COURIER_BY_ID_02;

			conn = DBConnection.getConnectionDB();
			
			DBConnection.onTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("DELETE: " + SQL);
			}

			stmt = conn.prepareStatement(COUNT_DELIVERY_COURIER_BY_IDCOURIER);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				count = rs.getInt(1);
			}

			if (count > 0)
			{
				throw new SQLException("Existe entrega relacionada ao entregador!");
			}

			stmt = conn.prepareStatement(DELETE_COURIER_BY_ID_01);
			stmt.setInt(1, id);
			stmt.executeUpdate();

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, id);
			stmt.executeUpdate();

			DBConnection.offTransaction();
			
			if (InitProperties.getViewSql())
			{
				logger.info("DELETE: OK!");
			}

		}
		catch (final ParameterException ex)
		{

			final String msg = "Erro ao deletar entregador! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		catch (final SQLException ex)
		{

			final String msg = "Erro ao deletar entregador! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@Override
	public List<CourierVO> listAllCourierBusiness (final BusinessVO business) throws DAOException
	{

		List<CourierVO> result = null;

		boolean thereIs = false;

		if (business == null)
		{
			throw new DAOException("Informe negócio!");
		}

		if (business.getId().intValue() <= 0)
		{
			throw new DAOException("Informe Id negócio!");
		}

		logger.info("Pesquisar informações dos entregadores do negócio.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_ALL_COURIER_BUSINESS;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, business.getId());

			rs = stmt.executeQuery();

			if (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<CourierVO>();
					thereIs = true;
				}

				final LoginVO login = new LoginVO();

				final CourierVO courier = new CourierVO();

				courier.setId(rs.getInt(rs.findColumn("ID")));
				courier.setRole(rs.getString(rs.findColumn("ROLE")));
				courier.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				courier.setLogin(login);

				courier.setEmail(rs.getString(rs.findColumn("EMAIL")));
				courier.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				courier.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				courier.setMobile(rs.getString(rs.findColumn("MOBILE")));

				courier.setFactor_courier(rs.getBigDecimal(rs.findColumn("FACTOR_COURIER")));

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0)
				{
					final BusinessVO vo = findBusiness(idBusiness);
					if ((vo != null) && (vo.getId() != null))
					{
						courier.setBusiness(vo);
					}
				}

				result.add(courier);

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar entregadores do negócio! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar entregadores do negócio! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return result;
	}

	@Override
	public CourierVO findDeliveryCourier (final Integer idCourier) throws DAOException
	{

		CourierVO result = null;

		if (idCourier == null)
		{
			throw new DAOException("Informe Id entregador!");
		}

		if (idCourier.intValue() <= 0)
		{
			throw new DAOException("Informe Id entregador!");
		}

		logger.info("Pesquisar informaõees de entregador da entrega.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_COURIER_BY_ID;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, idCourier);

			rs = stmt.executeQuery();

			if (rs.next())
			{

				final LoginVO login = new LoginVO();

				result = new CourierVO();

				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setRole(rs.getString(rs.findColumn("ROLE")));
				result.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				result.setLogin(login);

				result.setEmail(rs.getString(rs.findColumn("EMAIL")));
				result.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				result.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				result.setMobile(rs.getString(rs.findColumn("MOBILE")));

				result.setFactor_courier(rs.getBigDecimal(rs.findColumn("FACTOR_COURIER")));

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0)
				{
					final BusinessVO vo = findBusiness(idBusiness);
					if ((vo != null) && (vo.getId() != null))
					{
						result.setBusiness(vo);
					}
				}

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar entregador da entrega! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar entregador da entrega! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return result;
	}

	@Override
	public List<CourierVO> listAllCouriersWorkers () throws DAOException
	{

		List<CourierVO> result = null;

		boolean thereIs = false;

		logger.info("Pesquisar todos os entregadores.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_ALL_COURIER;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);

			rs = stmt.executeQuery();

			if (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<CourierVO>();
					thereIs = true;
				}

				final LoginVO login = new LoginVO();

				final CourierVO courier = new CourierVO();

				courier.setId(rs.getInt(rs.findColumn("ID")));
				courier.setRole(rs.getString(rs.findColumn("ROLE")));
				courier.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				courier.setLogin(login);

				courier.setEmail(rs.getString(rs.findColumn("EMAIL")));
				courier.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				courier.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				courier.setMobile(rs.getString(rs.findColumn("MOBILE")));

				courier.setFactor_courier(rs.getBigDecimal(rs.findColumn("FACTOR_COURIER")));

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0)
				{
					final BusinessVO vo = findBusiness(idBusiness);
					if ((vo != null) && (vo.getId() != null))
					{
						courier.setBusiness(vo);
					}
				}

				result.add(courier);

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar todos os entregadores! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar todos os entregadores! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return result;
	}
}

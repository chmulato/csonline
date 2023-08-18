package br.com.mulato.cso.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dao.BusinessDAO;
import br.com.mulato.cso.dry.DBConnection;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.utils.InitProperties;

public class BusinessDAOImpl 
    implements BusinessDAO, Serializable
{

	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = Logger.getLogger(BusinessDAOImpl.class);

	private boolean isThereLogin (final LoginVO login) throws DAOException
	{
		return FactoryDAO.getInstancia().getLoginDAO().isThereLogin(login);
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
	public BusinessVO find (final Integer id) throws DAOException
	{

		BusinessVO result = null;

		if (id == null)
		{
			throw new DAOException("Informe Id negócio!");
		}

		if (id <= 0)
		{
			throw new DAOException("Informe Id negócio!");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_BUSINESS_BY_ID;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				LOGGER.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, id);

			rs = stmt.executeQuery();

			if (rs.next())
			{
				final LoginVO login = new LoginVO();
				result = new BusinessVO();
				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setRole(rs.getString(rs.findColumn("ROLE")));
				result.setName(rs.getString(rs.findColumn("NAME")));
				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				result.setLogin(login);
				result.setEmail(rs.getString(rs.findColumn("EMAIL")));
				result.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				result.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				result.setMobile(rs.getString(rs.findColumn("MOBILE")));
			}
			if (InitProperties.getViewSql())
			{
				LOGGER.info("SQL: OK!");
			}
		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar negócio! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar negócio! ";
			LOGGER.error(msg + ex.getMessage());
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
	public void insert (final BusinessVO business) throws DAOException
	{

		int businessId = 0;

		if (business == null)
		{
			throw new DAOException("Informe negócio!");
		}

		if (business.getName() == null)
		{
			throw new DAOException("Informe nome do negócio!");
		}

		if (business.getRole() == null)
		{
			throw new DAOException("Informe o perfil de negócio!");
		}

		if (!business.getRole().equals("BUSINESS"))
		{
			throw new DAOException("Informe o perfil de negócio!");
		}

		if (business.getLogin() == null)
		{
			throw new DAOException("Informe login do negócio!");
		}

		if (business.getLogin().getLogin() == null)
		{
			throw new DAOException("Informe login do negócio!");
		}

		if (business.getLogin().getPassword() == null)
		{
			throw new DAOException("Informe senha do negócio!");
		}

		if (business.getEmail() == null)
		{
			throw new DAOException("Informe email do negócio!");
		}

		if (isThereLogin(business.getLogin()))
		{
			throw new DAOException("Login já existente!");
		}

		LOGGER.info("Salvar informações do negócio.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = INSERT_BUSINESS;

			conn = DBConnection.getConnectionDB();
			
			DBConnection.onTransaction();

			if (InitProperties.getViewSql())
			{
				LOGGER.info("INSERT: " + SQL);
			}

			stmt = conn.prepareStatement(GET_LAST_ID_ON_USER_TABLE);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				businessId = rs.getInt(1);
			}

			businessId = (businessId + 1);

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, businessId);
			stmt.setString(2, business.getRole());
			stmt.setString(3, business.getName());
			stmt.setString(4, business.getLogin().getLogin());
			stmt.setString(5, business.getLogin().getPassword());
			stmt.setString(6, business.getEmail());
			stmt.setString(7, business.getEmail2());
			stmt.setString(8, business.getAddress());
			stmt.setString(9, business.getMobile());

			stmt.executeUpdate();

			DBConnection.offTransaction();
			
			if (InitProperties.getViewSql())
			{
				LOGGER.info("INSERT: OK!");
			}

		}
		catch (final ParameterException ex)
		{

			final String msg = "Erro ao salvar negócio! ";
			LOGGER.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao salvar negócio! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@Override
	public void update (final BusinessVO business) throws DAOException
	{

		if (business == null)
		{
			throw new DAOException("Informe negócio!");
		}

		if (business.getId() == null)
		{
			throw new DAOException("Informe id negócio!");
		}

		if (business.getName() == null)
		{
			throw new DAOException("Informe nome do negócio!");
		}

		if (business.getRole() == null)
		{
			throw new DAOException("Informe perfil de negócio!");
		}

		if (!business.getRole().equals("BUSINESS"))
		{
			throw new DAOException("Informe perfil de negócio!");
		}

		if (business.getLogin() == null)
		{
			throw new DAOException("Informe login do negócio!");
		}

		if (business.getLogin().getLogin() == null)
		{
			throw new DAOException("Informe login do negócio!");
		}

		if (business.getEmail() == null)
		{
			throw new DAOException("Informe email do negócio!");
		}

		LOGGER.info("Salvar informações do negócio.");

		Connection conn = null;
		PreparedStatement stmt = null;

		try
		{

			final String SQL = UPDATE_BUSINESS;

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				LOGGER.info("UPDATE: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);

			stmt.setString(1, business.getName());
			stmt.setString(2, business.getEmail());
			stmt.setString(3, business.getEmail2());
			stmt.setString(4, business.getAddress());
			stmt.setString(5, business.getMobile());
			stmt.setInt(6, business.getId());

			stmt.executeUpdate();

			if (InitProperties.getViewSql())
			{
				LOGGER.info("UPDATE: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao atualizar negócio! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao atualizar negócio! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void delete (final Integer id) throws DAOException
	{

		int count = 0;

		if (id == null)
		{
			throw new DAOException("Informe id negócio!");
		}

		LOGGER.info("Deletar negócio.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = DELETE_BUSINESS_BY_ID;

			if (InitProperties.getViewSql())
			{
				LOGGER.info("DELETE: " + SQL);
			}

			conn = DBConnection.getConnectionDB();
			
			DBConnection.onTransaction();

			stmt = conn.prepareStatement(COUNT_DELIVERY_BUSINESS);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				count = rs.getInt(1);
			}

			if (count > 0)
			{
				throw new SQLException("Existe entrega relacionada ao negócio!");
			}

			stmt = conn.prepareStatement(COUNT_CUSTOMER_BUSINESS);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				count = rs.getInt(1);
			}

			if (count > 0)
			{
				throw new SQLException("Existe cliente relacionado ao negócio!");
			}

			stmt = conn.prepareStatement(COUNT_COURIER_BUSINESS);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				count = rs.getInt(1);
			}

			if (count > 0)
			{
				throw new SQLException("Existe entregador relacionado ao negócio!");
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, id);
			stmt.executeUpdate();

			DBConnection.offTransaction();

			if (InitProperties.getViewSql())
			{
				LOGGER.info("DELETE: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao atualizar negócio! ";
			LOGGER.error(msg + ex.getMessage());

			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao atualizar negócio! ";
			LOGGER.error(msg + ex.getMessage());

			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@Override
	public List<BusinessVO> listAll () throws DAOException
	{

		List<BusinessVO> result = null;

		boolean thereIs = false;

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_ALL_BUSINESS;

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				LOGGER.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);

			rs = stmt.executeQuery();

			while (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<BusinessVO>();
					thereIs = true;
				}

				final LoginVO login = new LoginVO();

				final BusinessVO business = new BusinessVO();

				business.setId(rs.getInt(rs.findColumn("ID")));
				business.setRole(rs.getString(rs.findColumn("ROLE")));
				business.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				business.setLogin(login);

				business.setEmail(rs.getString(rs.findColumn("EMAIL")));
				business.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				business.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				business.setMobile(rs.getString(rs.findColumn("MOBILE")));

				result.add(business);

			}

			if (InitProperties.getViewSql())
			{
				LOGGER.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar lista de negócios! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar lista de negócios! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
		return result;
	}

	@Override
	public BusinessVO findCustomerBusiness (final CustomerVO customer) throws DAOException
	{

		BusinessVO result = null;

		if (customer == null)
		{
			throw new DAOException("Informe cliente!");
		}

		if (customer.getId() == null)
		{
			throw new DAOException("Informe id cliente!");
		}

		if (customer.getId().intValue() <= 0)
		{
			throw new DAOException("Informe id cliente!");
		}

		LOGGER.info("Pesquisar informações de negócio do cliente.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_CUSTOMER_BUSINESS_BY_IDCUSTOMER;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				LOGGER.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, customer.getId());

			rs = stmt.executeQuery();

			if (rs.next())
			{

				final LoginVO login = new LoginVO();

				result = new BusinessVO();

				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setRole(rs.getString(rs.findColumn("ROLE")));
				result.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				result.setLogin(login);

				result.setEmail(rs.getString(rs.findColumn("EMAIL")));
				result.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				result.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				result.setMobile(rs.getString(rs.findColumn("MOBILE")));

			}

			if (InitProperties.getViewSql())
			{
				LOGGER.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar negócio do cliente! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar negócio do cliente! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return result;
	}

	@Override
	public BusinessVO findCourierBusiness (final CourierVO courier) throws DAOException
	{

		BusinessVO result = null;

		if (courier == null)
		{
			throw new DAOException("Informe entregador!");
		}

		if (courier.getId() == null)
		{
			throw new DAOException("Informe id entregador!");
		}

		if (courier.getId() <= 0)
		{
			throw new DAOException("Informe id entregador!");
		}

		LOGGER.info("Pesquisar informações de negócio do entregador.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_COURIER_BUSINESS_BY_IDCOURIER;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				LOGGER.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, courier.getId());

			rs = stmt.executeQuery();

			if (rs.next())
			{

				final LoginVO login = new LoginVO();

				result = new BusinessVO();

				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setRole(rs.getString(rs.findColumn("ROLE")));
				result.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				result.setLogin(login);

				result.setEmail(rs.getString(rs.findColumn("EMAIL")));
				result.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				result.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				result.setMobile(rs.getString(rs.findColumn("MOBILE")));

			}

			if (InitProperties.getViewSql())
			{
				LOGGER.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar negócio do entregador! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar negócio do entregador! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return result;
	}

	@Override
	public BusinessVO findDeliveryBusiness (final DeliveryVO delivery) throws DAOException
	{

		BusinessVO result = null;

		if (delivery == null)
		{
			throw new DAOException("Informe entrega!");
		}

		if (delivery.getId() <= 0)
		{
			throw new DAOException("Informe id entrega!");
		}

		if (delivery.getId() <= 0)
		{
			throw new DAOException("Informe id entrega!");
		}

		LOGGER.info("Pesquisar informações de negócio da entrega.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		

		try
		{

			final String SQL = SELECT_DELIVERY_BUSINESS_BY_IDDELIVERY;

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				LOGGER.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, delivery.getId());

			rs = stmt.executeQuery();

			if (rs.next())
			{

				final LoginVO login = new LoginVO();

				result = new BusinessVO();

				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setRole(rs.getString(rs.findColumn("ROLE")));
				result.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				result.setLogin(login);

				result.setEmail(rs.getString(rs.findColumn("EMAIL")));
				result.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				result.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				result.setMobile(rs.getString(rs.findColumn("MOBILE")));

			}

			if (InitProperties.getViewSql())
			{
				LOGGER.info("SQL: OK!");
			}
			
		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar negócio da entrega! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar negócio da entrega! ";
			LOGGER.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
		return result;
	}
}

package br.com.mulato.cso.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dao.CustomerDAO;
import br.com.mulato.cso.dry.DBConnection;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.CustomerVO;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.utils.InitProperties;

public class CustomerDAOImpl
    implements CustomerDAO, Serializable
{

	private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(CustomerDAOImpl.class);

	private boolean isThereLogin (final LoginVO login) throws DAOException
	{
		return FactoryDAO.getInstancia().getLoginDAO().isThereLogin(login);
	}

	private BusinessVO findBusiness (final Integer idBusiness) throws DAOException
	{
		return FactoryDAO.getInstancia().getBusinessDAO().find(idBusiness);
	}

	private List<DeliveryVO> findAllDeliveryCustomerNotCompleted (final CustomerVO customer) throws DAOException
	{
		return FactoryDAO.getInstancia().getDeliveryDAO().listAllDeliveryCustomerCompleted(customer, false);
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
	public CustomerVO find (final Integer id, final boolean full) throws DAOException
	{

		CustomerVO result = null;

		if ((id == null) || (id.intValue() <= 0))
		{
			throw new DAOException("Informe Id cliente!");
		}

		logger.info("Pesquisar cliente.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_CUSTOMER_BY_ID;
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

				result = new CustomerVO();

				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setRole(rs.getString(rs.findColumn("ROLE")));
				result.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				result.setLogin(login);

				result.setEmail(rs.getString(rs.findColumn("EMAIL")));
				result.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				result.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				result.setMobile(rs.getString(rs.findColumn("MOBILE")));
				result.setFactor_customer(rs.getBigDecimal(rs.findColumn("FACTOR_CUSTOMER")));
				result.setPrice_table(rs.getString(rs.findColumn("PRICE_TABLE")));

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
					final List<DeliveryVO> list = findAllDeliveryCustomerNotCompleted(result);
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
			final String msg = "Erro ao pesquisar cliente! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar cliente! ";
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
	public void insert (final CustomerVO customer) throws DAOException
	{

		int customerId = 0;
		int id_customer_table = 0;

		if (customer == null)
		{
			throw new DAOException("Informe cliente!");
		}

		if (customer.getName() == null)
		{
			throw new DAOException("Informe nome do cliente!");
		}

		if (customer.getRole() == null)
		{
			throw new DAOException("Informe o perfil de cliente!");
		}

		if (!customer.getRole().equals("CUSTOMER"))
		{
			throw new DAOException("Informe o perfil de cliente!");
		}

		if (customer.getLogin() == null)
		{
			throw new DAOException("Informe login do cliente!");
		}

		if (customer.getLogin().getLogin() == null)
		{
			throw new DAOException("Informe login do cliente!");
		}

		if (customer.getLogin().getPassword() == null)
		{
			throw new DAOException("Informe senha do cliente!");
		}

		if (customer.getEmail() == null)
		{
			throw new DAOException("Informe email do cliente!");
		}

		if (customer.getFactor_customer() == null)
		{
			throw new DAOException("Informe fator do cliente!");
		}

		if (customer.getBusiness() == null)
		{
			throw new DAOException("Informe negócio do cliente!");
		}

		if (customer.getBusiness().getId() == null)
		{
			throw new DAOException("Informe negócio do cliente!");
		}

		if (customer.getBusiness().getId().intValue() <= 0)
		{
			throw new DAOException("Informe negócio do cliente!");
		}

		if (isThereLogin(customer.getLogin()))
		{
			throw new DAOException("Login já existente!");
		}

		logger.info("Salvar informações do cliente.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = INSERT_CUSTOMER_BUSINESS_01;

			
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
				customerId = rs.getInt(1);
			}

			customerId = (customerId + 1);

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, customerId);
			stmt.setString(2, customer.getRole());
			stmt.setString(3, customer.getName());
			stmt.setString(4, customer.getLogin().getLogin());
			stmt.setString(5, customer.getLogin().getPassword());
			stmt.setString(6, customer.getEmail());
			stmt.setString(7, customer.getEmail2());
			stmt.setString(8, customer.getAddress());
			stmt.setString(9, customer.getMobile());

			stmt.executeUpdate();

			stmt = conn.prepareStatement(GET_LAST_ID_ON_CUSTOMER_TABLE);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				id_customer_table = rs.getInt(1);
			}

			id_customer_table = (id_customer_table + 1);

			stmt = conn.prepareStatement(INSERT_CUSTOMER_BUSINESS_02);

			stmt.setInt(1, id_customer_table);
			stmt.setInt(2, customer.getBusiness().getId());
			stmt.setInt(3, customerId);
			stmt.setBigDecimal(4, customer.getFactor_customer());

			if (customer.getPrice_table() == null)
			{
				stmt.setString(5, "");
			}
			else
			{
				stmt.setString(5, customer.getPrice_table().trim().toUpperCase());
			}

			stmt.executeUpdate();

			DBConnection.offTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("INSERT: OK!");
			}

		}
		catch (final ParameterException ex)
		{

			final String msg = "Erro ao salvar cliente! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		catch (final SQLException ex)
		{

			final String msg = "Erro ao salvar cliente! ";
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
	public void update (final CustomerVO customer) throws DAOException
	{

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

		if (customer.getName() == null)
		{
			throw new DAOException("Informe nome do cliente!");
		}

		if (customer.getRole() == null)
		{
			throw new DAOException("Informe perfil de cliente!");
		}

		if (!customer.getRole().equals("CUSTOMER"))
		{
			throw new DAOException("Informe perfil de cliente!");
		}

		if (customer.getLogin() == null)
		{
			throw new DAOException("Informe login do cliente!");
		}

		if (customer.getLogin().getLogin() == null)
		{
			throw new DAOException("Informe login do cliente!");
		}

		if (customer.getEmail() == null)
		{
			throw new DAOException("Informe email do cliente!");
		}

		if (customer.getBusiness() == null)
		{
			throw new DAOException("Informe negócio do cliente!");
		}

		if (customer.getBusiness().getId() == null)
		{
			throw new DAOException("Informe negócio do cliente!");
		}

		if (customer.getBusiness().getId().intValue() <= 0)
		{
			throw new DAOException("Informe negócio do cliente!");
		}

		logger.info("Salvar informações do cliente.");

		Connection conn = null;
		PreparedStatement stmt = null;

		try
		{

			final String SQL = UPDATE_CUSTOMER_01;

			if (InitProperties.getViewSql())
			{
				logger.info("UPDATE: " + SQL);
			}

			conn = DBConnection.getConnectionDB();

			stmt = conn.prepareStatement(SQL);

			stmt.setString(1, customer.getName());
			stmt.setString(2, customer.getEmail());
			stmt.setString(3, customer.getEmail2());
			stmt.setString(4, customer.getAddress());
			stmt.setString(5, customer.getMobile());
			stmt.setInt(6, customer.getId());

			stmt.executeUpdate();

			stmt = conn.prepareStatement(UPDATE_CUSTOMER_02);

			stmt.setBigDecimal(1, customer.getFactor_customer());

			if (customer.getPrice_table() == null)
			{
				stmt.setString(2, "");
			}
			else
			{
				stmt.setString(2, customer.getPrice_table().trim().toUpperCase());
			}

			stmt.setInt(3, customer.getBusiness().getId());
			stmt.setInt(4, customer.getId());

			stmt.executeUpdate();

			if (InitProperties.getViewSql())
			{
				logger.info("UPDATE: OK!");
			}

		}
		catch (final ParameterException ex)
		{

			final String msg = "Erro ao atualizar cliente! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		catch (final SQLException ex)
		{

			final String msg = "Erro ao atualizar cliente! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, null);
		}
	}

	@SuppressWarnings("resource")
	public void delete (final Integer id) throws DAOException
	{

		int count = 0;

		if ((id == null) || (id.intValue() <= 0))
		{
			throw new DAOException("Informe id cliente!");
		}

		logger.info("Deletar cliente.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = DELETE_CUSTOMER_BY_ID_02;

			if (InitProperties.getViewSql())
			{
				logger.info("DELETE: " + SQL);
			}

			conn = DBConnection.getConnectionDB();
			
			DBConnection.onTransaction();

			stmt = conn.prepareStatement(COUNT_DELIVERY_CUSTOMER);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				count = rs.getInt(1);
			}

			if (count > 0)
			{
				throw new SQLException("Existe entrega relacionada ao cliente!");
			}

			stmt = conn.prepareStatement(DELETE_CUSTOMER_BY_ID_01);
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
			final String msg = "Erro ao deletar cliente! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao deletar cliente! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@Override
	public List<CustomerVO> listAllCustomerBusiness (final BusinessVO business) throws DAOException
	{

		List<CustomerVO> result = null;

		boolean thereIs = false;

		if (business == null)
		{
			throw new DAOException("Informe negócio!");
		}

		if (business.getId() == null)
		{
			throw new DAOException("Informe negócio!");
		}

		if (business.getId().intValue() <= 0)
		{
			throw new DAOException("Informe negócio!");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_ALL_CUSTOMER_BUSINESS;

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, business.getId());
			rs = stmt.executeQuery();

			while (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<CustomerVO>();
					thereIs = true;
				}

				final LoginVO login = new LoginVO();

				final CustomerVO customer = new CustomerVO();

				customer.setId(rs.getInt(rs.findColumn("ID")));
				customer.setRole(rs.getString(rs.findColumn("ROLE")));
				customer.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				customer.setLogin(login);

				customer.setEmail(rs.getString(rs.findColumn("EMAIL")));
				customer.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				customer.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				customer.setMobile(rs.getString(rs.findColumn("MOBILE")));
				customer.setFactor_customer(rs.getBigDecimal(rs.findColumn("FACTOR_CUSTOMER")));
				customer.setPrice_table(rs.getString(rs.findColumn("PRICE_TABLE")));

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0)
				{
					final BusinessVO vo = findBusiness(idBusiness);
					if ((vo != null) && (vo.getId() != null))
					{
						customer.setBusiness(vo);
					}
				}

				final List<DeliveryVO> list = findAllDeliveryCustomerNotCompleted(customer);
				if ((list != null) && (list.size() > 0))
				{
					customer.setDeliveries(list);
				}

				result.add(customer);

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar lista de clientes! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar lista de clientes! ";
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
	public CustomerVO findDeliveryCustomer (final Integer idCustomer) throws DAOException
	{

		CustomerVO result = null;

		if ((idCustomer == null) || (idCustomer.intValue() <= 0))
		{
			throw new DAOException("Informe Id cliente!");
		}

		logger.info("Pesquisar cliente da entrega.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_CUSTOMER_BY_ID;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, idCustomer);

			rs = stmt.executeQuery();

			if (rs.next())
			{

				final LoginVO login = new LoginVO();

				result = new CustomerVO();

				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setRole(rs.getString(rs.findColumn("ROLE")));
				result.setName(rs.getString(rs.findColumn("NAME")));

				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				result.setLogin(login);

				result.setEmail(rs.getString(rs.findColumn("EMAIL")));
				result.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				result.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				result.setMobile(rs.getString(rs.findColumn("MOBILE")));
				result.setFactor_customer(rs.getBigDecimal(rs.findColumn("FACTOR_CUSTOMER")));
				result.setPrice_table(rs.getString(rs.findColumn("PRICE_TABLE")));

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
			final String msg = "Erro ao pesquisar cliente da entrega! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar cliente da entrega! ";
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

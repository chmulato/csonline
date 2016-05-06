package br.com.mulato.cso.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dao.UserDAO;
import br.com.mulato.cso.dry.DBConnection;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.model.UserVO;
import br.com.mulato.cso.utils.InitProperties;

public class UserDAOImpl 
    implements UserDAO, Serializable
{

	private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(UserDAOImpl.class);

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
	public UserVO find (final Integer id, final boolean getPasswordTo) throws DAOException
	{

		UserVO result = null;

		if (id == null)
		{
			throw new DAOException("Informe Id usuário!");
		}

		if (id.intValue() <= 0)
		{
			throw new DAOException("Informe Id usuário!");
		}

		Connection conn = null;

		logger.info("Pesquisar de usuário.");

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_USER_BY_ID;

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			conn = DBConnection.getConnectionDB();

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, id);

			rs = stmt.executeQuery();

			if (rs.next())
			{

				result = new UserVO();

				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setRole(rs.getString(rs.findColumn("ROLE")));
				result.setName(rs.getString(rs.findColumn("NAME")));

				final LoginVO login = new LoginVO();
				login.setLogin(rs.getString(rs.findColumn("LOGIN")));

				if (getPasswordTo)
				{
					login.setPassword((rs.getString(rs.findColumn("PASSWORD"))));
				}

				result.setLogin(login);

				result.setEmail(rs.getString(rs.findColumn("EMAIL")));
				result.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				result.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				result.setMobile(rs.getString(rs.findColumn("MOBILE")));

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar usuário! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar usuário! ";
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
	public List<UserVO> listAll () throws DAOException
	{

		List<UserVO> result = null;

		boolean thereIs = false;

		logger.info("Pesquisar lista de usuários.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_ALL_USER;

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			conn = DBConnection.getConnectionDB();
			stmt = conn.prepareStatement(SQL);
			rs = stmt.executeQuery();

			while (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<UserVO>();
					thereIs = true;
				}

				final UserVO user = new UserVO();

				user.setId(rs.getInt(rs.findColumn("ID")));
				user.setRole(rs.getString(rs.findColumn("ROLE")));
				user.setName(rs.getString(rs.findColumn("NAME")));

				final LoginVO login = new LoginVO();
				login.setLogin(rs.getString(rs.findColumn("LOGIN")));
				user.setLogin(login);

				user.setEmail(rs.getString(rs.findColumn("EMAIL")));
				user.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				user.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				user.setMobile(rs.getString(rs.findColumn("MOBILE")));

				result.add(user);

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar lista de usuários! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar lista de usuários! ";
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
	public List<BusinessVO> listAllBusiness () throws DAOException
	{

		List<BusinessVO> result = null;

		boolean thereIs = false;

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		logger.info("Pesquisar lista de negócios.");

		try
		{

			final String SQL = SELECT_ALL_BUSINESS;

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			conn = DBConnection.getConnectionDB();
			stmt = conn.prepareStatement(SQL);
			rs = stmt.executeQuery();

			while (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<BusinessVO>();
					thereIs = true;
				}

				final BusinessVO business = new BusinessVO();

				business.setId(rs.getInt(rs.findColumn("ID")));
				business.setRole(rs.getString(rs.findColumn("ROLE")));
				business.setName(rs.getString(rs.findColumn("NAME")));

				final LoginVO login = new LoginVO();
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
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar lista de negócios! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar lista de negócios! ";
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
	public UserVO findByLogin (final LoginVO login) throws DAOException
	{

		UserVO result = null;

		if (login == null)
		{
			throw new DAOException("Informe usuário!");
		}

		if (login.getLogin() == null)
		{
			throw new DAOException("Informe login!");
		}

		logger.info("Pesquisar de usuário por login.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_USER_BY_LOGIN;

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			conn = DBConnection.getConnectionDB();
			stmt = conn.prepareStatement(SQL);
			stmt.setString(1, login.getLogin());

			rs = stmt.executeQuery();

			if (rs.next())
			{

				result = new UserVO();

				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setRole(rs.getString(rs.findColumn("ROLE")));
				result.setName(rs.getString(rs.findColumn("NAME")));

				final LoginVO vo = new LoginVO();
				vo.setLogin(rs.getString(rs.findColumn("LOGIN")));
				result.setLogin(vo);

				result.setEmail(rs.getString(rs.findColumn("EMAIL")));
				result.setEmail2(rs.getString(rs.findColumn("EMAIL2")));
				result.setAddress(rs.getString(rs.findColumn("ADDRESS")));
				result.setMobile(rs.getString(rs.findColumn("MOBILE")));

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar usuário pelo login! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar usuário pelo login! ";
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

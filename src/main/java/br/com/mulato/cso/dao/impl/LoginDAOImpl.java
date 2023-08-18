package br.com.mulato.cso.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dao.LoginDAO;
import br.com.mulato.cso.dry.DBConnection;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.utils.InitProperties;

public class LoginDAOImpl
    implements LoginDAO
{

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
	public void authenticate (final LoginVO login) throws DAOException
	{

		String usr = null;
		String pwd = null;

		if (login == null)
		{
			throw new DAOException("Informe seu login!");
		}

		if (login.getLogin() == null)
		{
			throw new DAOException("Informe seu login!");
		}

		if (login.getPassword() == null)
		{
			throw new DAOException("Informe seu senha!");
		}

		usr = login.getLogin().trim();
		pwd = login.getPassword().trim();

		logger.info("Verificar login e senha do usuário.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_USER_BY_LOGIN_AND_PASSWORD;

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setString(1, usr);
			stmt.setString(2, pwd);

			rs = stmt.executeQuery();

			if (rs.next())
			{
				String pwd_db = rs.getString(rs.findColumn("PASSWORD"));
				if (pwd_db != null)
				{
					pwd_db = pwd_db.trim();
					if (!pwd.equals(pwd_db))
					{
						throw new DAOException("Usuário ou senha inválidos, tente novamente!");
					}
				}
			}
			else
			{
				throw new DAOException("Usuário ou senha inválidos, tente novamente!");
			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao verificar usuário e senha! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao verificar usuário e senha! ";
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
	public void passwordChange (final LoginVO login) throws DAOException
	{

		boolean update = false;

		if (login == null)
		{
			throw new DAOException("Informe login!");
		}

		if (login.getLogin() == null)
		{
			throw new DAOException("Informe login!");
		}

		if (login.getPassword() == null)
		{
			throw new DAOException("Informe senha!");
		}

		if (login.getNewPassword() == null)
		{
			throw new DAOException("Informe nova senha!");
		}

		if (login.getNewRepeat() == null)
		{
			throw new DAOException("Repita sua nova senha!");
		}

		if (!login.getNewPassword().equals(login.getNewRepeat()))
		{
			throw new DAOException("Repita sua nova senha corretamente!");
		}

		logger.info("Trocar senha.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = UPDATE_LOGIN_PASSWORD;

			conn = DBConnection.getConnectionDB();
			
			DBConnection.onTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("UPDATE: " + SQL);
			}

			stmt = conn.prepareStatement(SELECT_USER_BY_LOGIN_AND_PASSWORD);
			stmt.setString(1, login.getLogin());
			stmt.setString(2, login.getPassword());

			rs = stmt.executeQuery();

			if (rs.next())
			{
				String password = rs.getString(rs.findColumn("PASSWORD"));
				if (password != null)
				{
					password = password.trim();
					if (login.getPassword().equals(password))
					{
						update = true;
					}
				}
			}

			if (update)
			{
				stmt = conn.prepareStatement(SQL);
				stmt.setString(1, login.getNewPassword());
				stmt.setString(2, login.getLogin());
				stmt.executeUpdate();
			}

			DBConnection.offTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("UPDATE: OK!");
			}

		}
		catch (final ParameterException ex)
		{

			final String msg = "Erro ao trocar senha! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		catch (final SQLException ex)
		{

			final String msg = "Erro ao trocar senha! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@Override
	public boolean isThereLogin (final LoginVO login) throws DAOException
	{

		boolean result = false;

		if (login == null)
		{
			throw new DAOException("Informe login!");
		}

		if (login.getLogin() == null)
		{
			throw new DAOException("Informe login!");
		}

		logger.info("Pesquisar login.");

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
				result = true;
			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar login! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar login! ";
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

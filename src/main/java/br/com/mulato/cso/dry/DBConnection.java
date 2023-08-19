package br.com.mulato.cso.dry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Locale;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.utils.InitProperties;

/**
 * @author Christian Mulato
 * @date October/12th/2013
 */
public class DBConnection extends DBTransaction
{

	private final static Logger LOGGER = Logger.getLogger(DBConnection.class);

	private static boolean getting;
	private static boolean jndiActive;
	private static String jndiCSO;
	private static String driver;
	private static String urlCSO;
	private static String username;
	private static String password;
	
	static
	{
		Locale.setDefault(new Locale("pt", "BR"));
	}

	private static void getParamaters () throws ParameterException
	{
		if (!getting)
		{
			jndiActive = InitProperties.getJndiActive();
			jndiCSO = InitProperties.getJndiCSO();
			driver = InitProperties.getDriver();
			urlCSO = InitProperties.getDatabaseCSO();
			username = InitProperties.getUsername();
			password = InitProperties.getPassword();
			getting = true;
		}
	}

	public DBConnection ()
	{
		super();
	}

	public static java.sql.Date converteDateUtilToDateSql (final Date dataUtil)
	{
		java.sql.Date dataSql = null;
		if (dataUtil != null)
		{
			dataSql = new java.sql.Date(dataUtil.getTime());
		}
		return dataSql;
	}
	
	@SuppressWarnings("unused")
	public static Connection getConnectionDB() throws DAOException, ParameterException
	{
		String msg = "Criação da conexão com o banco de dados CSO.";
		LOGGER.info(msg);
		getParamaters();
		if (jndiActive)
		{
			try
			{
				InitialContext initialContext;
				DataSource dataSource;
				initialContext = new InitialContext();
				if (initialContext == null)
				{
					msg = "Não foi possí­vel ler o contexto de conexão com o banco.";
					LOGGER.error(msg);
					throw new DAOException(msg);
				}
				else
				{
					dataSource = (DataSource)initialContext.lookup(jndiCSO);
				}
				if (dataSource == null)
				{
					msg = "Não foi possí­vel carregar data source de conexão com o banco.";
					LOGGER.error(msg);
					throw new DAOException(msg);
				}
				else
				{
					setConnection(dataSource.getConnection());
										
				}
				if (getConnection() == null)
				{
					msg = "Não foi possí­vel abrir a conexão com o banco.";
					LOGGER.error(msg);
					throw new DAOException(msg);
				}
				return getConnection();
			}
			catch (NamingException | SQLException e)
			{
				msg = "Erro na conexão com o banco. ";
				LOGGER.error(msg + e.getMessage());
				throw new DAOException(msg);
			}
		}
		else
		{
			try
			{
				Class.forName(driver).newInstance();
				return DriverManager.getConnection(urlCSO, username, password);
			}
			catch (final ClassNotFoundException cnf)
			{
				msg = "Driver de conexão com o banco não encontrado. ";
				LOGGER.error(msg + cnf.getMessage());
				throw new DAOException(msg);
			}
			catch (final SQLException sql)
			{
				msg = "Não foi possí­vel abrir a conexão com o banco. ";
				LOGGER.error(msg + sql.getMessage());
				throw new DAOException(msg);
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				msg = "Erro na conexão com o banco. ";
				LOGGER.error(msg + e.getMessage());
				throw new DAOException(msg);
			}
		}
	}

	public static void closeConnection (final Connection conn, final Statement stmt, final ResultSet rs) throws DAOException
	{
		close(conn, stmt, rs);
	}

	public static void closeConnection (final Connection conn, final Statement stmt) throws DAOException
	{
		close(conn, stmt, null);
	}

	public static void close (final Connection conn) throws DAOException
	{
		close(conn, null, null);
	}

	public static void close (final Connection conn, final Statement stmt, final ResultSet rs) throws DAOException
	{
		
		final String msg = "Desconexão com a base de dados não efetuada corretamente. ";
		
		try
		{

			if (rs != null)
			{
				rs.close();
			}
			if (stmt != null)
			{
				stmt.close();
			}
			if (conn != null)
			{
				if (isTransaction_active())
				{
					conn.setAutoCommit(isTransaction_active());
				}
				conn.close();
			}
		}
		catch (final Exception e)
		{
			LOGGER.error(msg + e.getMessage());
			throw new DAOException(msg);
		}
	}

}

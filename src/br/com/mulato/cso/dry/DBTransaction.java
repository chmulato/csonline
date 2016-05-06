package br.com.mulato.cso.dry;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.mulato.cso.exception.DAOException;

public class DBTransaction {

	private static Connection connection;
	
	private static boolean transaction_active;

	private static void begin() throws SQLException
	{
		if ((connection != null) && (transaction_active))
		{
			if (connection.getAutoCommit() == Boolean.TRUE)
			{
				connection.setAutoCommit(false);
			}
		}
	}

	private static void commit() throws SQLException
	{
		if ((connection != null) && (transaction_active))
		{
			if (connection.getAutoCommit() == Boolean.FALSE)
			{
				connection.commit();
			}
		}
	}

	private static void enableTransaction() throws SQLException
	{
		if (connection != null)
		{
			if (connection.getAutoCommit() == Boolean.TRUE)
			{
				connection.setAutoCommit(false);
				transaction_active = true;
			}
		}
	}

	private static void disableTransaction() throws SQLException
	{
		if (connection != null)
		{
			if (connection.getAutoCommit() == Boolean.FALSE)
			{
				connection.setAutoCommit(true);
				transaction_active = false;
			}
		}
	}
	
	public static void onTransaction() throws DAOException
	{
		try {
			enableTransaction();
			begin();
		} catch (SQLException ex)
		{
			throw new DAOException("Falha ao ativa transacao...");
		}
	}
	
	public static void offTransaction() throws DAOException
	{
		try {
			commit();
			disableTransaction();
		} catch (SQLException ex)
		{
			throw new DAOException("Falha ao desativar transacao...");
		}
	}

	public static void setConnection(Connection connection) {
		DBTransaction.connection = connection;
	}

	public static Connection getConnection() {
		return connection;
	}

	public static boolean isTransaction_active() {
		return transaction_active;
	}

}

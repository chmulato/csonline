package br.com.mulato.cso.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dao.PriceDAO;
import br.com.mulato.cso.dry.DBConnection;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.PriceListVO;
import br.com.mulato.cso.model.PriceVO;
import br.com.mulato.cso.utils.InitProperties;
import br.com.mulato.cso.utils.vo.TableVO;

public class PriceDAOImpl
    implements PriceDAO, Serializable
{

	private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(PriceDAOImpl.class);

	private BusinessVO findBusiness (final Integer idBusiness) throws DAOException
	{
		return FactoryDAO.getInstancia().getBusinessDAO().find(idBusiness);
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

	private boolean thereIsEqualByOtherId (final PriceVO price) throws DAOException
	{

		boolean result = false;

		if (price == null)
		{
			throw new DAOException("Informe preço!");
		}

		if (price.getId() == null)
		{
			throw new DAOException("Informe id preço!");
		}

		if (price.getId().intValue() < 0)
		{
			throw new DAOException("Informe id preço!");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_PRICE_EQUAL_OTHER_ID;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, price.getId());
			stmt.setInt(2, price.getBusiness().getId());
			stmt.setString(3, price.getTable().trim().toUpperCase());
			stmt.setString(4, price.getVehicle().trim().toUpperCase());
			stmt.setString(5, price.getLocal().trim().toUpperCase());

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
			final String msg = "Erro ao pesquisar preço! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar preço! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return result;

	}

	private boolean thereIsEqual (final PriceVO price) throws DAOException
	{

		boolean result = false;

		if (price == null)
		{
			throw new DAOException("Informe preço!");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_PRICE_EQUAL;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, price.getBusiness().getId());
			stmt.setString(2, price.getTable().trim().toUpperCase());
			stmt.setString(3, price.getVehicle().trim().toUpperCase());
			stmt.setString(4, price.getLocal().trim().toUpperCase());

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
			final String msg = "Erro ao pesquisar preço! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar preço! ";
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
	public PriceVO find (final Integer id) throws DAOException
	{

		PriceVO result = null;

		if (id == null)
		{
			throw new DAOException("Informe Id tabela de preço!");
		}

		if (id.intValue() <= 0)
		{
			throw new DAOException("Informe Id tabela de preço!");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_PRICE_BY_ID;
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

				result = new PriceVO();

				result.setId(rs.getInt(rs.findColumn("ID")));
				result.setTable(rs.getString(rs.findColumn("TABLE_NAME")));
				result.setVehicle(rs.getString("VEHICLE"));
				result.setLocal(rs.getString("LOCAL"));
				result.setPrice(rs.getBigDecimal("PRICE"));

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0)
				{
					BusinessVO vo = new BusinessVO();
					vo = findBusiness(idBusiness);
					result.setBusiness(vo);
				}

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar preço! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar preço! ";
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
	public void delete (final Integer id) throws DAOException
	{

		if ((id == null) || (id.intValue() <= 0))
		{
			throw new DAOException("Informe id preço!");
		}

		logger.info("Deletar preço por id.");

		Connection conn = null;
		PreparedStatement stmt = null;
		final ResultSet rs = null;

		try
		{

			final String SQL = DELETE_PRICE_BY_ID;

			if (InitProperties.getViewSql())
			{
				logger.info("DELETE: " + SQL);
			}

			conn = DBConnection.getConnectionDB();

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, id);
			stmt.executeUpdate();

			if (InitProperties.getViewSql())
			{
				logger.info("DELETE: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao deletar preço! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao deletar preço! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@Override
	public void deleteBusinessValues (final Integer idBusiness) throws DAOException
	{

		if ((idBusiness == null) || (idBusiness.intValue() <= 0))
		{
			throw new DAOException("Informe id negócio!");
		}

		logger.info("Deletar preços do negócio");

		Connection conn = null;
		PreparedStatement stmt = null;
		final ResultSet rs = null;

		try
		{

			final String SQL = DELETE_BUSINESS_VALUES;

			if (InitProperties.getViewSql())
			{
				logger.info("DELETE: " + SQL);
			}

			conn = DBConnection.getConnectionDB();

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, idBusiness);

			stmt.executeUpdate();

			if (InitProperties.getViewSql())
			{
				logger.info("DELETE: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao deletar preços do negócio! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao deletar preços do negócio! ";
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
	public int insert (final PriceVO price) throws DAOException
	{

		int id = 0;

		if (price == null)
		{
			throw new DAOException("Informe preço!");
		}

		if (price.getBusiness() == null)
		{
			throw new DAOException("Informe o negócio!");
		}

		if (price.getBusiness().getId() == null)
		{
			throw new DAOException("Informe o negócio!");
		}

		if (price.getBusiness().getId().intValue() <= 0)
		{
			throw new DAOException("Informe o negócio!");
		}

		if (price.getTable() == null)
		{
			throw new DAOException("Informe tabela de preço!");
		}

		if (price.getTable().equals(""))
		{
			throw new DAOException("Informe tabela de preço!");
		}

		if (price.getVehicle() == null)
		{
			throw new DAOException("Informe tipo de transporte!");
		}

		if (price.getVehicle().equals(""))
		{
			throw new DAOException("Informe tipo de transporte!");
		}

		if (price.getLocal() == null)
		{
			throw new DAOException("Informe local!");
		}

		if (price.getLocal().equals(""))
		{
			throw new DAOException("Informe local!");
		}

		if (thereIsEqual(price))
		{
			throw new DAOException("Item já cadastrado!");
		}

		logger.info("Salvar dados da tabela de preço.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = INSERT_PRICE;

			conn = DBConnection.getConnectionDB();
			
			DBConnection.onTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("INSERT: " + SQL);
			}

			stmt = conn.prepareStatement(GET_LAST_ID_ON_PRICE_TABLE);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				id = rs.getInt(1);
			}

			id = (id + 1);

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, id);
			stmt.setInt(2, price.getBusiness().getId());
			stmt.setString(3, price.getTable());
			stmt.setString(4, price.getVehicle());

			if (price.getLocal() == null)
			{
				stmt.setString(5, "");
			}
			else
			{
				stmt.setString(5, price.getLocal().trim().toUpperCase());
			}

			if (price.getPrice() == null)
			{
				stmt.setNull(6, java.sql.Types.DECIMAL);
			}
			else
			{
				stmt.setBigDecimal(6, price.getPrice());
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

			final String msg = "Erro ao salvar preço! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		catch (final SQLException ex)
		{

			final String msg = "Erro ao salvar preço! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return id;
	}

	@Override
	public void update (final PriceVO price) throws DAOException
	{

		if (price == null)
		{
			throw new DAOException("Informe preço!");
		}

		if (price.getId() == null)
		{
			throw new DAOException("Informe id preço!");
		}

		if (price.getId().intValue() < 0)
		{
			throw new DAOException("Informe id preço!");
		}

		if (price.getBusiness() == null)
		{
			throw new DAOException("Informe o negócio!");
		}

		if (price.getBusiness().getId() == null)
		{
			throw new DAOException("Informe o negócio!");
		}

		if (price.getBusiness().getId().intValue() <= 0)
		{
			throw new DAOException("Informe o negócio!");
		}

		if (price.getTable() == null)
		{
			throw new DAOException("Informe tabela de preço!");
		}

		if (price.getTable().equals(""))
		{
			throw new DAOException("Informe tabela de preço!");
		}

		if (price.getVehicle() == null)
		{
			throw new DAOException("Informe tipo de transporte!");
		}

		if (price.getVehicle().equals(""))
		{
			throw new DAOException("Informe tipo de transporte!");
		}

		if (thereIsEqualByOtherId(price))
		{
			throw new DAOException("Item já cadastrado!");
		}

		logger.info("Atualizar preço.");

		Connection conn = null;
		PreparedStatement stmt = null;
		final ResultSet rs = null;

		try
		{

			final String SQL = UPDATE_PRICE_BY_ID;

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("UPDATE: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, price.getBusiness().getId());
			stmt.setString(2, price.getTable());
			stmt.setString(3, price.getVehicle());

			if (price.getLocal() == null)
			{
				stmt.setString(4, "");
			}
			else
			{
				stmt.setString(4, price.getLocal().trim().toUpperCase());
			}

			if (price.getPrice() == null)
			{
				stmt.setNull(5, java.sql.Types.DECIMAL);
			}
			else
			{
				stmt.setBigDecimal(5, price.getPrice());
			}

			stmt.setInt(6, price.getId());

			stmt.executeUpdate();

			if (InitProperties.getViewSql())
			{
				logger.info("UPDATE: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao atualizar preço! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao atualizar preço! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}
	}

	@Override
	public List<PriceVO> listAllPriceCustomer (final String nameTable) throws DAOException
	{

		List<PriceVO> result = null;

		boolean thereIs = false;

		if (nameTable == null)
		{
			throw new DAOException("Informe nome da tabela cliente!");
		}

		if (nameTable.equals(""))
		{
			throw new DAOException("Informe nome da tabela cliente!");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_CUSTOMER_VALUES;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setString(1, nameTable.trim().toUpperCase());

			rs = stmt.executeQuery();

			while (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<PriceVO>();
					thereIs = true;
				}

				final PriceVO price = new PriceVO();

				price.setId(rs.getInt(rs.findColumn("ID")));
				price.setTable(rs.getString(rs.findColumn("TABLE_NAME")));
				price.setVehicle(rs.getString("VEHICLE"));
				price.setLocal(rs.getString("LOCAL"));
				price.setPrice(rs.getBigDecimal("PRICE"));

				final int idBusiness = rs.getInt(rs.findColumn("IDBUSINESS"));
				if (idBusiness > 0)
				{
					BusinessVO vo = new BusinessVO();
					vo = findBusiness(idBusiness);
					price.setBusiness(vo);
				}

				result.add(price);

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar lista de preços do cliente! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar lista de preços do cliente! ";
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
	public List<PriceVO> listAllPriceBusinessByTable (final Integer idBusiness, final String table) throws DAOException
	{

		List<PriceVO> result = null;

		boolean thereIs = false;

		if (idBusiness == null)
		{
			throw new DAOException("Informe id negócio!");
		}

		if (idBusiness.intValue() <= 0)
		{
			throw new DAOException("Informe id negócio!");
		}

		if (table == null)
		{
			throw new DAOException("Informe nome da tabela!");
		}

		if (table.equals(""))
		{
			throw new DAOException("Informe nome da tabela!");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_BUSINESS_VALUES_BY_PRICE_TABLE;
			
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, idBusiness);
			stmt.setString(2, table.trim().toUpperCase());

			rs = stmt.executeQuery();

			while (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<PriceVO>();
					thereIs = true;
				}

				final PriceVO price = new PriceVO();

				price.setId(rs.getInt(rs.findColumn("ID")));
				price.setTable(rs.getString(rs.findColumn("TABLE_NAME")));
				price.setVehicle(rs.getString("VEHICLE"));
				price.setLocal(rs.getString("LOCAL"));
				price.setPrice(rs.getBigDecimal("PRICE"));

				if (idBusiness.intValue() > 0)
				{
					BusinessVO vo = new BusinessVO();
					vo = findBusiness(idBusiness);
					price.setBusiness(vo);
				}

				result.add(price);

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar lista de preços do negócio! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar lista de preços do negócio! ";
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
	public List<PriceListVO> listAllPriceListBusiness (final Integer idBusiness) throws DAOException
	{

		List<PriceListVO> result = null;

		boolean thereIs = false;

		if (idBusiness == null)
		{
			throw new DAOException("Informe id negócio!");
		}

		if (idBusiness.intValue() <= 0)
		{
			throw new DAOException("Informe id negócio!");
		}

		try
		{

			final int total = InitProperties.getListPriceTable().size();

			if (total > 0)
			{

				for (int i = 0; i < total; i++)
				{

					if (!thereIs)
					{
						result = new ArrayList<PriceListVO>();
						thereIs = true;
					}

					final PriceListVO priceList = new PriceListVO();

					final List<TableVO> listTable = InitProperties.getListPriceTable();

					if ((listTable != null) && (listTable.size() > 0))
					{

						for (final TableVO table : listTable)
						{

							final List<PriceVO> listPrices = listAllPriceBusinessByTable(idBusiness, table.getName());

							if ((listPrices != null) && (listPrices.size() > 0))
							{

								priceList.setTable(table.getName());

								priceList.setList(listPrices);

							}

						}

					}

					result.add(priceList);

				}
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar todas as listas de preços do negócio! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}

		return result;
	}

	@Override
	public List<PriceVO> listAllPriceBusiness (final Integer idBusiness) throws DAOException
	{

		List<PriceVO> result = null;

		boolean thereIs = false;

		if (idBusiness == null)
		{
			throw new DAOException("Informe id negócio!");
		}

		if (idBusiness.intValue() <= 0)
		{
			throw new DAOException("Informe id negócio!");
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_BUSINESS_VALUES;
			
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, idBusiness);

			rs = stmt.executeQuery();

			while (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<PriceVO>();
					thereIs = true;
				}

				final PriceVO price = new PriceVO();

				price.setId(rs.getInt(rs.findColumn("ID")));
				price.setTable(rs.getString(rs.findColumn("TABLE_NAME")));
				price.setVehicle(rs.getString("VEHICLE"));
				price.setLocal(rs.getString("LOCAL"));
				price.setPrice(rs.getBigDecimal("PRICE"));

				if (idBusiness.intValue() > 0)
				{
					BusinessVO vo = new BusinessVO();
					vo = findBusiness(idBusiness);
					price.setBusiness(vo);
				}

				result.add(price);

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar lista de preços do negócio! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar lista de preços do negócio! ";
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

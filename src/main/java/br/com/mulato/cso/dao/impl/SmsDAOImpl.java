package br.com.mulato.cso.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dao.SmsDAO;
import br.com.mulato.cso.dry.DBConnection;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.model.DeliveryVO;
import br.com.mulato.cso.model.SmsVO;
import br.com.mulato.cso.utils.InitProperties;
import br.com.mulato.cso.utils.ToolUtils;

public class SmsDAOImpl
    implements SmsDAO, Serializable
{

	private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(SmsDAOImpl.class);

	private final static int SMS_SIZE_MESSAGE = 255;

	private DeliveryVO findDelivery (final Integer idDelivery) throws DAOException
	{
		return FactoryDAO.getInstancia().getDeliveryDAO().find(idDelivery);
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

	@SuppressWarnings("resource")
	private int insertSMSPieces (final SmsVO sms) throws DAOException
	{

		int id = 0;

		logger.info("Salvar mensagem de sms em pedaços.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			String SQL = INSERT_SMS_SENT;

			if ((sms.getType() == 'R'))
			{
				SQL = INSERT_SMS_RECEIVED;
			}

			conn = DBConnection.getConnectionDB();
			
			DBConnection.onTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("INSERT: " + SQL);
			}

			stmt = conn.prepareStatement(GET_LAST_ID_ON_SMS_TABLE);
			rs = stmt.executeQuery();

			if (rs.next())
			{
				id = rs.getInt(1);
			}

			id = (id + 1);

			logger.info("Id of SMS Table: " + id);

			stmt = conn.prepareStatement(SQL);

			stmt.setInt(1, id);

			if ((sms.getDelivery() == null) || (sms.getDelivery().getId() == null))
			{
				stmt.setNull(2, java.sql.Types.INTEGER);
			}
			else
			{
				stmt.setInt(2, (sms.getDelivery().getId()));
			}

			stmt.setShort(3, sms.getPiece());
			stmt.setString(4, sms.getTo());
			stmt.setString(5, sms.getFrom());
			stmt.setString(6, sms.getMessage().toUpperCase());

			stmt.executeUpdate();

			DBConnection.offTransaction();

			if (InitProperties.getViewSql())
			{
				logger.info("INSERT: OK!");
			}

		}
		catch (final ParameterException ex)
		{

			final String msg = "Erro ao salvar sms em pedaços! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		catch (final SQLException ex)
		{

			final String msg = "Erro ao salvar sms em pedaços! ";
			logger.error(msg + ex.getMessage());

			throw new DAOException(msg);

		}
		finally
		{
			DBConnection.closeConnection(conn, stmt, rs);
		}

		return id;
	}

	public void update (final SmsVO sms) throws DAOException
	{

		if (sms == null)
		{
			throw new DAOException("Informe mensagem!");
		}

		if (sms.getId() == null)
		{
			throw new DAOException("Informe id mensagem!");
		}

		if (sms.getId().intValue() <= 0)
		{
			throw new DAOException("Informe id mensagem!");
		}

		if (sms.getDelivery() == null)
		{
			throw new DAOException("Informe entrega!");
		}

		if (sms.getDelivery().getId() == null)
		{
			throw new DAOException("Informe id entrega!");
		}

		if (sms.getDelivery().getId().intValue() <= 0)
		{
			throw new DAOException("Informe id entrega!");
		}

		logger.info("Atualizar mensagem.");

		Connection conn = null;
		PreparedStatement stmt = null;

		try
		{

			final String SQL = UPDATE_SMS_RECEIVED;

			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("UPDATE: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);

			if ((sms.getDelivery() == null) || (sms.getDelivery().getId() == null))
			{
				stmt.setNull(1, java.sql.Types.INTEGER);
			}
			else
			{
				stmt.setInt(1, sms.getDelivery().getId());
			}

			stmt.setInt(2, sms.getId());

			stmt.executeUpdate();

			if (InitProperties.getViewSql())
			{
				logger.info("UPDATE: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao atualizar mensagem! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao atualizar mensagem! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		finally
		{
			DBConnection.closeConnection(conn, stmt);
		}
	}

	public int insert (final SmsVO sms) throws DAOException
	{

		String originalMsg;
		final ToolUtils tools = new ToolUtils();

		int id = 0;
		short piece = 1;

		if (sms == null)
		{
			throw new DAOException("Informe sms!");
		}

		if ((sms.getType() != 'S') && (sms.getType() != 'R'))
		{
			throw new DAOException("Informe sms de envio ['S'] ou resposta ['R']!");
		}

		if (sms.getTo() == null)
		{
			throw new DAOException("Informe celular a receber mensagem!");
		}

		if (sms.getTo().equals(""))
		{
			throw new DAOException("Informe celular a receber mensagem!");
		}

		if (sms.getFrom() == null)
		{
			throw new DAOException("Informe celular de envio da mensagem!");
		}

		if (sms.getFrom().equals(""))
		{
			throw new DAOException("Informe celular de envio da mensagem!");
		}

		if (sms.getMessage() == null)
		{
			throw new DAOException("Informe mensagem do sms!");
		}

		if (sms.getMessage().equals(""))
		{
			throw new DAOException("Informe mensagem do sms!");
		}

		if (!tools.validarNumero(sms.getTo()))
		{
			throw new DAOException("Informe número de celular válido a receber da mensagem!");
		}

		if (!tools.validarNumero(sms.getFrom()))
		{
			throw new DAOException("Informe número de celular válido de envio da mensagem!");
		}

		String message = sms.getMessage().trim();
		message = message.replaceAll("[\"\']", "");
		originalMsg = tools.removeAccentuation(message);
		originalMsg = originalMsg.toUpperCase();

		logger.info("Mensagem de SMS p/ salvar: " + originalMsg + ". Total caracteres: " + originalMsg.length());

		if (originalMsg.length() > SMS_SIZE_MESSAGE)
		{

			String msg;

			int start = 0;
			int end = SMS_SIZE_MESSAGE;
			final int last = originalMsg.length();

			final int total = last / end;

			for (int i = 0; i < total; i++)
			{

				msg = originalMsg.substring(start, end);

				sms.setPiece(piece);
				sms.setMessage(msg);

				insertSMSPieces(sms);

				start = start + SMS_SIZE_MESSAGE;
				end = end + SMS_SIZE_MESSAGE;
				piece = (short)(piece + 1);

			}

			msg = originalMsg.substring(start, last);

			sms.setPiece(piece);
			sms.setMessage(msg);

			id = insertSMSPieces(sms);

		}
		else
		{

			sms.setPiece(piece);

			id = insertSMSPieces(sms);

		}

		return id;
	}

	@Override
	public SmsVO find (final Integer id) throws DAOException
	{

		final ToolUtils tools = new ToolUtils();
		SmsVO result = null;

		if (id == null)
		{
			throw new DAOException("Informe Id mensagem de sms!");
		}

		if (id.intValue() <= 0)
		{
			throw new DAOException("Informe Id mensagem de sms!");
		}

		logger.info("Pesquisar mensagem de sms pelo id.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_SMS_BY_ID;
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

				result = new SmsVO();

				result.setId(rs.getInt(rs.findColumn("ID")));

				final int idDelivery = rs.getInt(rs.findColumn("IDDELIVERY"));

				if (idDelivery > 0)
				{
					final DeliveryVO vo = new DeliveryVO();
					vo.setId(idDelivery);
					result.setDelivery(vo);
				}

				result.setTo(rs.getString(rs.findColumn("MOBILE_TO")));
				result.setFrom(rs.getString(rs.findColumn("MOBILE_FROM")));

				result.setPiece(rs.getShort(rs.findColumn("PIECE")));
				result.setType((rs.getString(rs.findColumn("TYPE")).charAt(0)));

				if (rs.getString("DATETIME") != null)
				{
					final String datetime = rs.getString(rs.findColumn("DATETIME"));
					result.setDatetime(tools.converteDataStringToDateUtil(datetime, "yyyy-MM-dd hh:mm:ss"));
				}

				result.setMessage(rs.getString(rs.findColumn("MESSAGE")));

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao pesquisar mensagem de sms! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao pesquisar mensagem de sms! ";
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
	public List<SmsVO> listAllSmsDelivery (final Integer idDelivery) throws DAOException
	{

		final ToolUtils tools = new ToolUtils();
		List<SmsVO> result = null;

		boolean thereIs = false;

		if (idDelivery == null)
		{
			throw new DAOException("Informe Id da entrega!");
		}

		if (idDelivery.intValue() <= 0)
		{
			throw new DAOException("Informe Id da entrega!");
		}

		logger.info("Pesquisar sms da entrega.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_SMS_DELIVERY;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setInt(1, idDelivery);

			rs = stmt.executeQuery();

			while (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<SmsVO>();
					thereIs = true;
				}

				final SmsVO sms = new SmsVO();

				sms.setId(rs.getInt(rs.findColumn("ID")));

				if (idDelivery > 0)
				{
					final DeliveryVO vo = new DeliveryVO();
					vo.setId(idDelivery);
					sms.setDelivery(vo);
				}

				sms.setPiece(rs.getShort(rs.findColumn("PIECE")));
				sms.setType((rs.getString(rs.findColumn("TYPE")).charAt(0)));

				if (rs.getString(rs.findColumn("DATETIME")) != null)
				{
					final String datetime = rs.getString(rs.findColumn("DATETIME"));
					sms.setDatetime(tools.converteDataStringToDateUtil(datetime, "yyyy-MM-dd hh:mm:ss"));
				}

				sms.setTo(rs.getString(rs.findColumn("MOBILE_TO")));
				sms.setFrom(rs.getString(rs.findColumn("MOBILE_FROM")));

				sms.setMessage(rs.getString(rs.findColumn("MESSAGE")));

				result.add(sms);

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao listar mensagens da entrega! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao listar mensagens da entrega! ";
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
	public List<SmsVO> listAllSmsMobile (final String mobile) throws DAOException
	{

		final ToolUtils tools = new ToolUtils();
		List<SmsVO> result = null;

		boolean thereIs = false;

		if (mobile == null)
		{
			throw new DAOException("Informe celular!");
		}

		if (mobile.equals(""))
		{
			throw new DAOException("Informe celular!");
		}

		if (!tools.validarNumero(mobile))
		{
			throw new DAOException("Informe número de celular válido!");
		}

		logger.info("Pesquisar todas mensagens do celular.");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{

			final String SQL = SELECT_SMS_MOBILE;
			conn = DBConnection.getConnectionDB();

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: " + SQL);
			}

			stmt = conn.prepareStatement(SQL);
			stmt.setString(1, mobile);
			stmt.setString(2, mobile);

			rs = stmt.executeQuery();

			while (rs.next())
			{

				if (!thereIs)
				{
					result = new ArrayList<SmsVO>();
					thereIs = true;
				}

				final SmsVO sms = new SmsVO();

				sms.setId(rs.getInt(rs.findColumn("ID")));

				final int idDelivery = rs.getInt(rs.findColumn("IDDELIVERY"));

				if (idDelivery > 0)
				{
					DeliveryVO vo = new DeliveryVO();
					vo = findDelivery(idDelivery);
					if (vo != null)
					{
						sms.setDelivery(vo);
					}
				}

				sms.setPiece(rs.getShort(rs.findColumn("PIECE")));
				sms.setType((rs.getString(rs.findColumn("TYPE")).charAt(0)));

				if (rs.getString(rs.findColumn("DATETIME")) != null)
				{
					final String datetime = rs.getString(rs.findColumn("DATETIME"));
					sms.setDatetime(tools.converteDataStringToDateUtil(datetime, "yyyy-MM-dd hh:mm:ss"));
				}

				sms.setTo(rs.getString(rs.findColumn("MOBILE_TO")));
				sms.setFrom(rs.getString(rs.findColumn("MOBILE_FROM")));

				sms.setMessage(rs.getString(rs.findColumn("MESSAGE")));

				result.add(sms);

			}

			if (InitProperties.getViewSql())
			{
				logger.info("SQL: OK!");
			}

		}
		catch (final ParameterException ex)
		{
			final String msg = "Erro ao listar todas mensagens do celular! ";
			logger.error(msg + ex.getMessage());
			throw new DAOException(msg);
		}
		catch (final SQLException ex)
		{
			final String msg = "Erro ao listar todas mensagens do celular! ";
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

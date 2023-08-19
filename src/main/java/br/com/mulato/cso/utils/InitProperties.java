package br.com.mulato.cso.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import br.com.mulato.cso.exception.ParameterException;
import br.com.mulato.cso.utils.vo.TableVO;
import br.com.mulato.cso.utils.vo.VehicleVO;

public class InitProperties
{

	private final static Logger LOGGER = Logger.getLogger(InitProperties.class);

	private static boolean getting;
	private static String jndiCSO;
	private static boolean jndiActive;
	private static String driver;
	private static String username;
	private static String password;
	private static String urlDatabaseCSO;
	private static boolean viewSql;
	private static boolean viewObject;
	private static boolean singletonDAO;
	private static boolean singletonService;

	// variáveis para os parâmetros de envio de email
	private static boolean email_active;
	private static String email_user;
	private static String email_password;
	private static String email_protocol_smtp;
	private static String email_server_smtp;
	private static String email_server_smtp_port;
	private static boolean email_server_smtp_ssl;
	private static boolean email_server_smtp_tls;
	private static String email_server_smtp_authentication;
	private static String email_protocol_imap;
	private static String email_server_imap;
	private static String email_server_imap_port;
	private static boolean email_server_imap_ssl;
	private static long email_server_imap_timeout;
	private static String email_server_imap_folder;
	private static String email_format;
	private static boolean email_debug;

	// variáveis para os parâmetros caixa postal do android
	private static String email_android;

	// variáveis para os parâmetros de recebimento de email
	// variáveis para envio de sms
	private static boolean smsActive;
	private static String smsMobile;
	private static long smsTimer;

	private static String applicationName;
	private static String themeApplication;

	private static List<VehicleVO> listVehicle;
	private static List<TableVO> listPriceTable;

	public InitProperties ()
	{
		super();
	}

	private static void getProperties () throws ParameterException
	{

		if (!getting)
		{

			String msg;
			String condition;

			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			final InputStream inputStream = classLoader.getResourceAsStream("config.properties");

			LOGGER.info("Leitura de parâmetros do arquivo properties config.");

			try
			{
				final Properties properties = new Properties();
				properties.load(inputStream);
				if (properties.getProperty("jndi.cso") != null)
				{
					jndiCSO = properties.getProperty("jndi.cso");
				}
				else
				{
					msg = "Informe parâmetro jndi cso!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("jndi.active") != null)
				{
					condition = properties.getProperty("jndi.active").toUpperCase();
					if (condition.equals("TRUE"))
					{
						jndiActive = true;
					}
					if (condition.equals("FALSE"))
					{
						jndiActive = false;
					}
				}
				else
				{
					msg = "Informe parâmetro ativar jndi!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("jdbc.driver") != null)
				{
					driver = properties.getProperty("jdbc.driver");
				}
				else
				{
					msg = "Informe parâmetro driver!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("jdbc.username") != null)
				{
					username = properties.getProperty("jdbc.username");
				}
				else
				{
					msg = "Informe parâmetro username!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("jdbc.password") != null)
				{
					password = properties.getProperty("jdbc.password");
				}
				else
				{
					msg = "Informe parâmetro password!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("jdbc.url.cso") != null)
				{
					urlDatabaseCSO = properties.getProperty("jdbc.url.cso");
				}
				else
				{
					msg = "Informe parâmetro url cso!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("view.sql") != null)
				{
					condition = properties.getProperty("view.sql").toUpperCase();
					if (condition.equals("TRUE"))
					{
						viewSql = true;
					}
					if (condition.equals("FALSE"))
					{
						viewSql = false;
					}
				}
				else
				{
					msg = "Informe parâmetro sql view true or false!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("view.object") != null)
				{
					condition = properties.getProperty("view.object").toUpperCase();
					if (condition.equals("TRUE"))
					{
						viewObject = true;
					}
					if (condition.equals("FALSE"))
					{
						viewObject = false;
					}
				}
				else
				{
					msg = "Informe parâmetro object view true or false!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("singleton.dao") != null)
				{
					condition = properties.getProperty("singleton.dao").toUpperCase();
					if (condition.equals("TRUE"))
					{
						singletonDAO = true;
					}
					if (condition.equals("FALSE"))
					{
						singletonDAO = false;
					}
				}
				else
				{
					msg = "Informe parâmetro dao singleton true or false!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("singleton.service") != null)
				{
					condition = properties.getProperty("singleton.service").toUpperCase();
					if (condition.equals("TRUE"))
					{
						singletonService = true;
					}
					if (condition.equals("FALSE"))
					{
						singletonService = false;
					}
				}
				else
				{
					msg = "Informe parâmetro service singleton true or false!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				// seta parâmetros para envio de mensagens de email
				if (properties.getProperty("email.active") != null)
				{
					condition = properties.getProperty("email.active").toUpperCase();
					if (condition.equals("TRUE"))
					{
						email_active = true;
					}
					if (condition.equals("FALSE"))
					{
						email_active = false;
					}
				}
				else
				{
					msg = "Informe parâmetro email de envio ativo: true or false!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.email") != null)
				{
					email_user = properties.getProperty("email.email");
				}
				else
				{
					msg = "Informe parâmetro email de envio!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.password") != null)
				{
					email_password = properties.getProperty("email.password");
				}
				else
				{
					msg = "Informe parâmetro email de envio!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.protocolo_smtp") != null)
				{
					email_protocol_smtp = properties.getProperty("email.protocolo_smtp");
					email_protocol_smtp = email_protocol_smtp.toLowerCase();
				}
				else
				{
					msg = "Informe parâmetro protocolo do email de envio!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.server_smtp") != null)
				{
					email_server_smtp = properties.getProperty("email.server_smtp");
				}
				else
				{
					msg = "Informe parâmetro endereço do email de envio!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.server_smtp_port") != null)
				{
					email_server_smtp_port = properties.getProperty("email.server_smtp_port");
				}
				else
				{
					msg = "Informe parâmetro porta do email para envio!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.server_smtp_ssl") != null)
				{
					condition = properties.getProperty("email.server_smtp_ssl").toUpperCase();
					if (condition.equals("TRUE"))
					{
						email_server_smtp_ssl = true;
					}
					if (condition.equals("FALSE"))
					{
						email_server_smtp_ssl = false;
					}
				}
				else
				{
					msg = "Informe parâmetro ssl do email de envio!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.server_smtp_tls") != null)
				{
					condition = properties.getProperty("email.server_smtp_tls").toUpperCase();
					if (condition.equals("TRUE"))
					{
						email_server_smtp_tls = true;
					}
					if (condition.equals("FALSE"))
					{
						email_server_smtp_tls = false;
					}
				}
				else
				{
					msg = "Informe parâmetro tls do email de envio!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.server_smtp_authentication") != null)
				{
					email_server_smtp_authentication = properties.getProperty("email.server_smtp_authentication");
				}
				else
				{
					msg = "Informe parâmetro autenticação do servidor de email de envio!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.protocolo_imap") != null)
				{
					email_protocol_imap = properties.getProperty("email.protocolo_imap");
					email_protocol_imap = email_protocol_imap.toLowerCase();
				}
				else
				{
					msg = "Informe parâmetro protocolo email de recebimento!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.server_imap") != null)
				{
					email_server_imap = properties.getProperty("email.server_imap");
				}
				else
				{
					msg = "Informe parâmetro endereço do protocolo de recepção!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.server_imap_port") != null)
				{
					email_server_imap_port = properties.getProperty("email.server_imap_port");
				}
				else
				{
					msg = "Informe parâmetro porta email de recebimento!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.server_imap_ssl") != null)
				{
					condition = properties.getProperty("email.server_imap_ssl").toUpperCase();
					if (condition.equals("TRUE"))
					{
						email_server_imap_ssl = true;
					}
					if (condition.equals("FALSE"))
					{
						email_server_imap_ssl = false;
					}
				}
				else
				{
					msg = "Informe parâmetro ssl email de recebimento!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.server_imap_timeout") != null)
				{
					final String strEmailReceiverTimeout = properties.getProperty("email.server_imap_timeout");
					if (new ToolUtils().validarNumero(strEmailReceiverTimeout))
					{
						final long timeout = Long.parseLong(strEmailReceiverTimeout);
						if ((timeout >= 5000) && (timeout <= 30000))
						{
							email_server_imap_timeout = timeout;
						}
						else
						{
							msg = "Informe parâmetro timeout do email de recebimento! Valor entre 5000 milisegundos a 30000 milisegundos";
							LOGGER.error(msg);
							throw new ParameterException(msg);
						}
					}
					else
					{
						msg = "Informe parâmetro timeout do email de recebimento! Valor entre 5000 milisegundos a 30000 milisegundos";
						LOGGER.error(msg);
						throw new ParameterException(msg);
					}
				}
				else
				{
					msg = "Informe parâmetro timeout do email de recebimento! Valor entre 5000 milisegundos a 30000 milisegundos";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.server_imap_folder") != null)
				{
					email_server_imap_folder = properties.getProperty("email.server_imap_folder");
					email_server_imap_folder = email_server_imap_folder.toUpperCase();
				}
				else
				{
					msg = "Informe parâmetro pasta principal do email de recebimento!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.debug") != null)
				{
					condition = properties.getProperty("email.debug").toUpperCase();
					if (condition.equals("TRUE"))
					{
						email_debug = true;
					}
					if (condition.equals("FALSE"))
					{
						email_debug = false;
					}
				}
				else
				{
					msg = "Informe parâmetro debug email!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("email.format") != null)
				{
					email_format = properties.getProperty("email.format");
				}
				else
				{
					msg = "Informe parâmetro formato email!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				// seta parâmetros para endereço da caixa postal de email do android
				if (properties.getProperty("email.android") != null)
				{
					email_android = properties.getProperty("email.android");
				}
				else
				{
					msg = "Informe parâmetro email de envio!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				// seta parâmetros para envio de sms
				if (properties.getProperty("sms.active") != null)
				{
					condition = properties.getProperty("sms.active").toUpperCase();
					if (condition.equals("TRUE"))
					{
						smsActive = true;
					}
					if (condition.equals("FALSE"))
					{
						smsActive = false;
					}
				}
				else
				{
					msg = "Informe parâmetro de enviar sms: true or false!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("sms.mobile_number") != null)
				{
					final String strMobile = properties.getProperty("sms.mobile_number");
					if (new ToolUtils().validarNumero(strMobile))
					{
						smsMobile = strMobile;
					}
					else
					{
						msg = "Informe parâmetro de nÃºmero de celular para envio de sms!";
						LOGGER.error(msg);
						throw new ParameterException(msg);
					}
				}
				else
				{
					msg = "Informe parâmetro de nÃºmero de celular para envio de sms!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("sms.timer_milliseconds") != null)
				{
					final String strTimer = properties.getProperty("sms.timer_milliseconds");
					if (new ToolUtils().validarNumero(strTimer))
					{
						smsTimer = Integer.parseInt(strTimer);
					}
					else
					{
						msg = "Informe parâmetro de tempo de leitura de sms!";
						LOGGER.error(msg);
						throw new ParameterException(msg);
					}
				}
				else
				{
					msg = "Informe parâmetro de tempo de leitura de sms!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				// seta parâmetros para nome da aplicação
				if (properties.getProperty("client.application_name") != null)
				{
					applicationName = properties.getProperty("client.application_name");
				}
				else
				{
					msg = "Informe parâmetro nome da aplicação!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("client.theme_application") != null)
				{
					themeApplication = properties.getProperty("client.theme_application");
				}
				else
				{
					msg = "Informe parâmetro aparência da aplicação!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("list.vehicle.type") != null)
				{
					final String strVehicle = properties.getProperty("list.vehicle.type");
					final String v[] = strVehicle.split("\\,");
					if (v.length > 0)
					{
						listVehicle = new ArrayList<>();
						for (int j = 0; j < v.length; j++)
						{
							if (v[j] != null)
							{
								final VehicleVO vehicle = new VehicleVO();
								vehicle.setId(j + 1);
								vehicle.setVehicle(v[j].trim().toUpperCase());
								listVehicle.add(vehicle);
							}
						}
					}
					else
					{
						msg = "Informe parâmetro tipo de veÃ­culos!";
						LOGGER.error(msg);
						throw new ParameterException(msg);
					}
				}
				else
				{
					msg = "Informe parâmetro tipo de veÃ­culos!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				if (properties.getProperty("list.price.table") != null)
				{
					final String strTable = properties.getProperty("list.price.table");
					final String p[] = strTable.split("\\,");
					if (p.length > 0)
					{
						listPriceTable = new ArrayList<>();
						for (int j = 0; j < p.length; j++)
						{
							if (p[j] != null)
							{
								final TableVO table = new TableVO();
								table.setId(j + 1);
								table.setName(p[j].trim().toUpperCase());
								listPriceTable.add(table);
							}
						}
					}
					else
					{
						msg = "Informe parâmetro tipo de veÃ­culos!";
						LOGGER.error(msg);
						throw new ParameterException(msg);
					}
				}
				else
				{
					msg = "Informe parâmetro tipo de veÃ­culos!";
					LOGGER.error(msg);
					throw new ParameterException(msg);
				}

				getting = true;

			}
			catch (final ParameterException e)
			{
				msg = "Parâmetro de configuração não encontrado! ";
				msg = msg + e.getMessage();
				LOGGER.error(msg);
				throw new ParameterException(msg);
			}
			catch (IOException | NumberFormatException e)
			{
				msg = "Arquivo de configuração não encontrado! ";
				LOGGER.error(msg + e.getMessage());
				throw new ParameterException(msg);
			}

		}
	}

	public static boolean getJndiActive () throws ParameterException
	{
		getProperties();
		return jndiActive;
	}

	public static String getJndiCSO () throws ParameterException
	{
		getProperties();
		return jndiCSO;
	}

	public static String getDriver () throws ParameterException
	{
		getProperties();
		return driver;
	}

	public static String getUsername () throws ParameterException
	{
		getProperties();
		return username;
	}

	public static String getPassword () throws ParameterException
	{
		getProperties();
		return password;
	}

	public static String getDatabaseCSO () throws ParameterException
	{
		getProperties();
		return urlDatabaseCSO;
	}

	public static boolean getViewSql () throws ParameterException
	{
		getProperties();
		return viewSql;
	}

	public static boolean getViewObject () throws ParameterException
	{
		getProperties();
		return viewObject;
	}

	public static boolean getSingletonDAO () throws ParameterException
	{
		getProperties();
		return singletonDAO;
	}

	public static boolean getSingletonService () throws ParameterException
	{
		getProperties();
		return singletonService;
	}

	// seta parâmetros para envio de mensagens de email
	public static boolean getEmail_active () throws ParameterException
	{
		getProperties();
		return email_active;
	}

	public static String getEmail_user () throws ParameterException
	{
		getProperties();
		return email_user;
	}

	public static String getEmail_password () throws ParameterException
	{
		getProperties();
		return email_password;
	}

	public static String getEmail_protocol_smtp () throws ParameterException
	{
		getProperties();
		return email_protocol_smtp;
	}

	public static String getEmail_server_smtp () throws ParameterException
	{
		getProperties();
		return email_server_smtp;
	}

	public static String getEmail_server_smtp_port () throws ParameterException
	{
		getProperties();
		return email_server_smtp_port;
	}

	public static boolean getEmail_server_smtp_ssl () throws ParameterException
	{
		getProperties();
		return email_server_smtp_ssl;
	}

	public static boolean getEmail_server_smtp_tls () throws ParameterException
	{
		getProperties();
		return email_server_smtp_tls;
	}

	public static String getEmail_server_smtp_authentication () throws ParameterException
	{
		getProperties();
		return email_server_smtp_authentication;
	}

	public static String getEmail_protocol_imap () throws ParameterException
	{
		getProperties();
		return email_protocol_imap;
	}

	public static String getEmail_server_imap () throws ParameterException
	{
		getProperties();
		return email_server_imap;
	}

	public static String getEmail_server_imap_port () throws ParameterException
	{
		getProperties();
		return email_server_imap_port;
	}

	public static boolean getEmail_server_imap_ssl () throws ParameterException
	{
		getProperties();
		return email_server_imap_ssl;
	}

	public static long getEmail_server_imap_timeout () throws ParameterException
	{
		getProperties();
		return email_server_imap_timeout;
	}

	public static String getEmail_server_imap_folder () throws ParameterException
	{
		getProperties();
		return email_server_imap_folder;
	}

	public static boolean getEmail_debug () throws ParameterException
	{
		getProperties();
		return email_debug;
	}

	public static String getEmail_format () throws ParameterException
	{
		getProperties();
		return email_format;
	}

	public static String getEmail_android () throws ParameterException
	{
		getProperties();
		return email_android;
	}

	public static boolean getSmsActive () throws ParameterException
	{
		getProperties();
		return smsActive;
	}

	public static String getSmsMobile () throws ParameterException
	{
		getProperties();
		return smsMobile;
	}

	public static long getSmsTimer () throws ParameterException
	{
		getProperties();
		return smsTimer;
	}

	public static String getApplicationName () throws ParameterException
	{
		getProperties();
		return applicationName;
	}

	public static String getThemeApplication () throws ParameterException
	{
		getProperties();
		return themeApplication;
	}

	public static List<VehicleVO> getListVehicle () throws ParameterException
	{
		getProperties();
		return listVehicle;
	}

	public static List<TableVO> getListPriceTable () throws ParameterException
	{
		getProperties();
		return listPriceTable;
	}

	public static VehicleVO getVehicleVO (final String vehicle)
	{
		VehicleVO result = null;
		try
		{
			getProperties();
			final List<VehicleVO> vehicles = getListVehicle();
			if ((vehicles != null) && (vehicles.size() > 0))
			{
				for (final VehicleVO v : vehicles)
				{
					if (vehicle.equals(v.getVehicle()))
					{
						result = v;
					}
				}
			}
		}
		catch (final ParameterException e)
		{
			LOGGER.error("Erro na leitura de parâmetro [ veÃ­culos ] ..." + e.getMessage());
		}
		return result;
	}

	public static VehicleVO getVehicleVO (final Integer id)
	{
		VehicleVO result = null;
		try
		{
			getProperties();
			final List<VehicleVO> vehicles = getListVehicle();
			if ((vehicles != null) && (vehicles.size() > 0))
			{
				for (final VehicleVO vehicle : vehicles)
				{
					if (id.equals(vehicle.getId()))
					{
						result = vehicle;
					}
				}
			}
		}
		catch (final ParameterException e)
		{
			LOGGER.error("Erro na leitura de parâmetro [veÃ­culos] ..." + e.getMessage());
		}
		return result;
	}

	public static TableVO getTableVO (final String table)
	{
		TableVO result = null;
		try
		{
			getProperties();
			final List<TableVO> tables = getListPriceTable();
			if ((tables != null) && (tables.size() > 0))
			{
				for (final TableVO tb : tables)
				{
					if (table.equals(tb.getName()))
					{
						result = tb;
					}
				}
			}
		}
		catch (final ParameterException e)
		{
			LOGGER.error("Erro na leitura de parâmetro [tabelas de preços] ..." + e.getMessage());
		}
		return result;
	}

	public static TableVO getTableVO (final Integer id)
	{
		TableVO result = null;
		try
		{
			getProperties();
			final List<TableVO> tables = getListPriceTable();
			if ((tables != null) && (tables.size() > 0))
			{
				for (final TableVO table : tables)
				{
					if (id.equals(table.getId()))
					{
						result = table;
					}
				}
			}
		}
		catch (final ParameterException e)
		{
			LOGGER.error("Erro na leitura de parâmetro [tabelas de preços] ..." + e.getMessage());
		}
		return result;
	}
}

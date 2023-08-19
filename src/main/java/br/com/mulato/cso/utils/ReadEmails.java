package br.com.mulato.cso.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.CourierVO;
import br.com.mulato.cso.utils.vo.EmailVO;

public class ReadEmails
{

	private final static Logger LOGGER = Logger.getLogger(ReadEmails.class);

	private List<CourierVO> listAllCouriers = new ArrayList<>();

	private static List<EmailVO> listAllEmails;

	private static boolean deleteMessages;

	public static void setDeleteMessages (final boolean deleteMessages)
	{
		ReadEmails.deleteMessages = deleteMessages;
	}

	public static void setListAllEmails (final List<EmailVO> listAllEmails)
	{
		ReadEmails.listAllEmails = listAllEmails;
	}

	public static List<EmailVO> getListAllEmails ()
	{
		return listAllEmails;
	}

	private boolean isThisMessageToDelete (final Message message) throws MessagingException
	{
		boolean validate = false;
		boolean delete = false;
		if ((message.getSubject() != null) && (!message.getSubject().equals("")))
		{
			validate = true;
		}
		if (validate)
		{
			validate = message.getSentDate() != null;
		}

		if (validate)
		{
			final String subject = message.getSubject();
			if ((getListAllEmails() != null) && (getListAllEmails().size() > 0))
			{
				final ListIterator<EmailVO> listIterator = getListAllEmails().listIterator();
				while (listIterator.hasNext())
				{
					final EmailVO email = listIterator.next();
					if (email != null)
					{
						boolean equal = false;
						if (email.getFrom() != null)
						{
							equal = true;
						}

						if (equal)
						{
							equal = email.getData() != null;
						}

						if (equal)
						{
							final String mobile = email.getFrom();
							final Date date = email.getData();
							if ((subject.contains(mobile)) && (date.equals(message.getSentDate())))
							{
								LOGGER.info("Deletar email: subject = " + message.getSubject() + " | date= " +
								    ToolUtils.converteDateToString(message.getSentDate(), "dd/MM/yyyy HH:mm:ss"));
								delete = true;
								break;
							}
						}
					}
				}
			}
		}
		return delete;
	}

	private String isThisCourier (final String subject) throws DAOException
	{
		String mobileNumber = "";
		if ((getListAllCouriers() != null) && (getListAllCouriers().size() > 0))
		{
			final ListIterator<CourierVO> listIterator = getListAllCouriers().listIterator();
			while (listIterator.hasNext())
			{
				final CourierVO courier = listIterator.next();
				if (courier != null)
				{
					if ((courier.getMobile() != null) && (!courier.getMobile().equals("")))
					{
						final String mobile = courier.getMobile();
						if (new ToolUtils().validarNumero(mobile))
						{
							if (subject.contains(mobile))
							{
								LOGGER.info("Número de celular válido= " + mobile);
								mobileNumber = mobile;
								break;
							}
						}
					}
				}
			}
		}
		return mobileNumber;
	}

	private List<CourierVO> getListAllCouriers () throws DAOException
	{
		List<CourierVO> list;
		list = FactoryDAO.getInstancia().getCourierDAO().listAllCouriersWorkers();
		if ((list != null) && (list.size() > 0))
		{
			listAllCouriers = list;
		}
		return listAllCouriers;
	}

	public void connectionAccountMail () throws WebException
	{

		String msg;
		final String username = InitProperties.getEmail_user();
		final String password = InitProperties.getEmail_password();
		final String protocolo = InitProperties.getEmail_protocol_imap();
		final String server = InitProperties.getEmail_server_imap();
		final String port = InitProperties.getEmail_server_imap_port();
		final boolean ssl = InitProperties.getEmail_server_imap_ssl();
		final String format = InitProperties.getEmail_format();
		final String folder = InitProperties.getEmail_server_imap_folder();
		final String timeout = String.valueOf(InitProperties.getEmail_server_imap_timeout());

		if ((protocolo == null) || (protocolo.equals("")))
		{
			msg = "Informe protocolo do servidor p/ recebimento de email!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((server == null) || (server.equals("")))
		{
			msg = "Informe endereí§o IMAP do servidor!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((port == null) || (port.equals("")))
		{
			msg = "Informe a porta do servidor IMAP!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((username == null) || (username.equals("")))
		{
			msg = "Informe a conta do usuário do servidor IMAP!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((password == null) || (password.equals("")))
		{
			msg = "Informe a senha do usuário do servidor IMAP!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((format == null) || (format.equals("")))
		{
			msg = "Informe formato de e-mail p/ leitura!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((timeout == null) || (timeout.equals("")))
		{
			msg = "Informe timeout de e-mail p/ leitura!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		try
		{

			final Properties properties = new Properties();

			properties.setProperty("mail.store.protocol", protocolo);
			properties.setProperty("mail.imap.host", server);
			properties.setProperty("mail.imap.port", port);
			properties.setProperty("mail.imap.connectiontimeout", timeout);
			properties.setProperty("mail.imap.timeout", timeout);

			if (ssl)
			{
				properties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				properties.put("mail.imap.socketFactory.fallback", "true");
			}

			// Cria um autenticador que sera usado a seguir
			final AuthenticationEmail auth = new AuthenticationEmail(username, password);

			if (!deleteMessages)
			{
				LOGGER.info("--------------Iní­cio de Processamento de Leitura de Emails-----------------");
			}
			else
			{
				LOGGER.info("--------------Iní­cio de Processamento de Deletar de Emails-----------------");
			}

			// Session - objeto que ira realizar a conexão com o servidor
			/*
			 * Como há necessidade de autenticação é criada uma autenticação que
			 * é responsável por solicitar e retornar o usuário e senha para
			 * autenticação
			 */
			final Session session = Session.getDefaultInstance(properties, auth);
			LOGGER.info("Abrindo sessão para acessar emails ...");
			final Store store = session.getStore(protocolo);
			LOGGER.info("Conexão estabelecida com o servidor imap: " + server);
			store.connect(server, username, password);
			LOGGER.info(store);
			final Folder inbox = store.getFolder(folder);
			if (!deleteMessages)
			{
				inbox.open(Folder.READ_ONLY);
				readerAllMails(inbox);
			}
			else
			{
				inbox.open(Folder.READ_WRITE);
				deleteAllMails(inbox);
			}
			// Close connection
			inbox.close(true);
			store.close();
			if (!deleteMessages)
			{
				LOGGER.info("--------------Finalização do Processo de Leitura de Emails-----------------");
			}
			else
			{
				LOGGER.info("--------------Finalização do Processo de Deletar de Emails-----------------");
			}
		}
		catch (final MessagingException e)
		{
			LOGGER.error("Não foi possí­vel acesar as mensagens! " + e.getMessage());
			throw new WebException("Não foi possí­vel acessar as mensagens!");
		}
	}

	private void readerAllMails (final Folder inbox)
	{
		LOGGER.info("Leitura de emails... pasta: " + inbox.getFullName());
		boolean loop = false;
		try
		{
			final Message msg[] = inbox.getMessages();
			if ((msg != null) && (msg.length > 0))
			{
				LOGGER.info("Total de emails lidos: " + msg.length);
				int count = 0;
				for (final Message message : msg)
				{
					LOGGER.info("Mensagem existente para leitura! " + (count + 1));
					LOGGER.info("e ..., contanto.");
					if ((message.getSubject() != null) && (!message.getSubject().equals("")))
					{
						final String courierMobile = isThisCourier(message.getSubject());
						LOGGER.info("Validar celular do entregador: " + courierMobile);
						if (!courierMobile.equals(""))
						{
							final EmailVO vo = new EmailVO();
							if (!loop)
							{
								listAllEmails = new ArrayList<>();
								loop = true;
							}
							LOGGER.info("DATE: " + ToolUtils.converteDateToString(message.getSentDate(), "dd/MM/yyyy HH:mm:ss"));
							vo.setData(message.getSentDate());
							LOGGER.info("FROM: " + message.getFrom()[0].toString());
							vo.setFrom(courierMobile);
							LOGGER.info("SUBJECT: " + String.valueOf(message.getSubject()));
							vo.setSubject(String.valueOf(message.getSubject()));
							String content = "";
							if (message instanceof MimeMessage)
							{
								final MimeMessage m = (MimeMessage)message;
								final Object contentObject = m.getContent();
								if (contentObject instanceof Multipart)
								{
									BodyPart clearTextPart = null;
									BodyPart htmlTextPart = null;
									final Multipart multipart = (Multipart)contentObject;
									final int mpCount = multipart.getCount();
									for (int i = 0; i < mpCount; i++)
									{
										final BodyPart part = multipart.getBodyPart(i);
										if (part.isMimeType("text/plain"))
										{
											clearTextPart = part;
											break;
										}
										else if (part.isMimeType("text/html"))
										{
											htmlTextPart = part;
										}
									}
									if (clearTextPart != null)
									{
										content = (String)clearTextPart.getContent();
									}
									else if (htmlTextPart != null)
									{
										final String html = (String)htmlTextPart.getContent();
										content = Jsoup.parse(html).text();
									}
								}
								else if (contentObject instanceof String)
								{
									// a simple text message
									content = (String)contentObject;
								}
								else
								{
									// not a mime message
									LOGGER.error("It is not a mime part or multipart");
									content = null;
								}
							}
							LOGGER.info("CONTENT: " + String.valueOf(content));
							vo.setContent(String.valueOf(content));
							LOGGER.info("Carregando mensagens na lista ...");
							listAllEmails.add(vo);
						}
						else
						{
							LOGGER.info("Celular do entregador inválido!");
						}
						LOGGER.info("Mensagens carregadas corretamente!");
					}
					count = count + 1;
				}
				LOGGER.info("Looping leitura finalizado!");
			}
		}
		catch (final MessagingException e)
		{
			LOGGER.error("Não foi possí­vel ler as mensagens! " + e.getMessage());
		}
		catch (DAOException | IOException e)
		{
			LOGGER.error("Não foi possí­vel ler as mensagens! " + e.getMessage());
		}

		if (listAllEmails != null)
		{
			if (listAllEmails.isEmpty())
			{
				listAllEmails = null;
			}
			else
			{
				LOGGER.info("Ordenar lista de mensagens!");
				Collections.sort(listAllEmails);
			}
		}
	}

	private void deleteAllMails (final Folder inbox)
	{
		LOGGER.info("Deletar mensagens... pasta: " + inbox.getFullName());
		try
		{
			final Message msg[] = inbox.getMessages();
			if ((msg != null) && (msg.length > 0))
			{
				LOGGER.info("Total de emails para verificar se será deletadas: " + msg.length);
				int count = 0;
				for (final Message message : msg)
				{
					LOGGER.info("Mensagem existente para deletar: " + (count + 1));
					LOGGER.info("e ..., contanto.");
					final boolean delete = isThisMessageToDelete(message);
					LOGGER.info("Deletar mensagem selecionada? " + (delete ? "- Sim" : " - Não"));
					if (delete)
					{
						message.setFlag(Flags.Flag.DELETED, true);
						LOGGER.info("Mensagem deletada!");
					}
					count = count + 1;
				}
				LOGGER.info("Looping delete finalizado!");
			}
		}
		catch (final MessagingException e)
		{
			LOGGER.error("Não foi possí­vel deletar todas as mensagens! " + e.getMessage());
		}
	}
}

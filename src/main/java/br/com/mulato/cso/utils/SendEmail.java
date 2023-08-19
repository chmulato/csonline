package br.com.mulato.cso.utils;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;
import br.com.mulato.cso.exception.WebException;

/**
 * @author Christian Mulato
 * @date Nov/14th/2013
 */
public class SendEmail
{

	private final static Logger LOGGER = Logger.getLogger(SendEmail.class);

	public SendEmail (final String[] recipient, final String subject, final String message) throws WebException
	{
		if (InitProperties.getEmail_active())
		{
			sendEmail(recipient, subject, message, false, null, null);
		}
	}

	public SendEmail (final String[] recipient, final String subject, final String message, final String pathFileAttached, final String filename)
	    throws WebException
	{
		if (InitProperties.getEmail_active())
		{
			sendEmail(recipient, subject, message, true, pathFileAttached, filename);
		}
	}

	private void sendEmail (final String[] recipient, final String subject, final String message, final boolean attachment,
	    final String pathFileAttached, final String filename) throws WebException
	{

		String msg;
		final String username = InitProperties.getEmail_user();
		final String password = InitProperties.getEmail_password();
		final String protocolo = InitProperties.getEmail_protocol_smtp();
		final String server = InitProperties.getEmail_server_smtp();
		final String port = InitProperties.getEmail_server_smtp_port();
		final boolean ssl = InitProperties.getEmail_server_smtp_ssl();
		final boolean tls = InitProperties.getEmail_server_smtp_tls();
		final String authentication = InitProperties.getEmail_server_smtp_authentication();
		final boolean debug = InitProperties.getEmail_debug();
		final String format = InitProperties.getEmail_format();

		if ((protocolo == null) || (protocolo.equals("")))
		{
			msg = "Informe protocolo do servidor p/ envio do email!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((server == null) || (server.equals("")))
		{
			msg = "Informe endereí§o SMTP do servidor!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((port == null) || (port.equals("")))
		{
			msg = "Informe a porta do servidor SMTP!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((authentication == null) || (authentication.equals("")))
		{
			msg = "Informe se o servidor SMTP utiliza autenticaçío!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((username == null) || (username.equals("")))
		{
			msg = "Informe a conta do usuário do servidor SMTP!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((password == null) || (password.equals("")))
		{
			msg = "Informe a senha do usuário do servidor SMTP!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((recipient == null) || (recipient.length == 0))
		{
			msg = "Informe a conta eletrí´nica p/ envio do(s) documento(s)!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		if ((format == null) || (format.equals("")))
		{
			msg = "Informe formato de e-mail p/ enviar!";
			LOGGER.error(msg);
			throw new WebException(msg);
		}

		try
		{

            final Properties properties = new Properties();

            //define protocolo de envio
            properties.put("mail.transport.protocol", String.valueOf(protocolo));
            //server SMTP
            properties.put("mail.smtp.host", server);
            //para ativar autenticacao insira "true"
            properties.put("mail.smtp.auth", authentication);
            //conta que esta enviando o email
            properties.put("mail.smtp.user", username);

            if (debug)
            {
                properties.put("mail.debug", "true");
            }

            //porta
            properties.put("mail.smtp.port", port);
            //mesma porta para o socket
            properties.put("mail.smtp.socketFactory.port", port);

        	// SSL on, the port default is 465
            if (ssl)
            {
            	properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.put("mail.smtp.socketFactory.fallback", "false");
            }

        	// TLS on, the port default is 587
            if (tls)
            {
            	properties.put("mail.smtp.starttls.enable", "true");
            }
           
            properties.put("mail.smtp.quitwait", "false");

            //Cria um autenticador que sera usado a seguir
            final AuthenticationEmail auth = new AuthenticationEmail (username, password);

            //Session - objeto que ira realizar a conexão com o servidor
            /*Como há necessidade de autenticação é criada uma autenticação que
             * é responsável por solicitar e retornar o usuário e senha para
             * autenticação */
            final Session session = Session.getDefaultInstance(properties, auth);

            //Habilita o LOG das ações executadas durante o envio do email
            session.setDebug(true);
            //Objeto que contém a mensagem
            MimeMessage mimeMessage = new MimeMessage(session);

            //Setando os destinatários
            InternetAddress[] addressTo = new InternetAddress[recipient.length];
            for (int i = 0; i < recipient.length; i++) {
                addressTo[i] = new InternetAddress(recipient[i]);
            }

            mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);             	
            //Setando a origem do email
            mimeMessage.setFrom(new InternetAddress(username));
            //Setando o assunto
            mimeMessage.setSubject(subject);
            // Create the message part 
            BodyPart messageBodyPart = new MimeBodyPart();
            // Fill the message
            messageBodyPart.setText(message);
            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            // Part two is attachment
            if (attachment) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(pathFileAttached + filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
            }
            // Send the complete message parts
            mimeMessage.setContent(multipart);
            //Objeto encarregado de enviar os dados para o email
            Transport tr;
            //define smtp para transporte
            tr = session.getTransport(protocolo); 
            /*
             *  1 - define o servidor smtp
             *  2 - seu nome de usuario
             *  3 - sua senha
             */
            tr.connect(server, username, password);
            mimeMessage.saveChanges(); // não esqueçaa isso
            //envio da mensagem
            tr.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            tr.close();
		}
		catch (MessagingException e)
		{
			msg = "Nío foi possí­vel enviar a mensagem! ";
			LOGGER.error(msg + e.getMessage());
			throw new WebException(msg);
		}
	}
}

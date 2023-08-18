package br.com.mulato.cso.view.beans;

import java.io.Serializable;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesMessages
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	public static void mensInfo (final String message)
	{
		mensagem("Aviso: ", message, "SEVERITY_INFO");
	}

	public static void mensWarn (final String message)
	{
		mensagem("Alerta: ", message, "SEVERITY_WARN");
	}

	public static void mensErro (final String message)
	{
		mensagem("Erro: ", message, "SEVERITY_ERROR");
	}

	public static void mensFatal (final String message)
	{
		mensagem("Erro Fatal: ", message, "SEVERITY_FATAL");
	}

	public static void mensagem (final String summary, final String message, final String severity)
	{

		final FacesContext facesContext = FacesContext.getCurrentInstance();

		// remove all old messages
		final Iterator<FacesMessage> iteratorMsg = facesContext.getMessages();
		while (iteratorMsg.hasNext())
		{
			iteratorMsg.next();
			iteratorMsg.remove();
		}

		final FacesMessage facesMessage = new FacesMessage();
		facesMessage.setSummary(summary);
		facesMessage.setDetail(message);
		if (severity.equals("SEVERITY_INFO"))
		{
			facesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
		}
		else if (severity.equals("SEVERITY_WARN"))
		{
			facesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
		}
		else if (severity.equals("SEVERITY_ERROR"))
		{
			facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		}
		else if (severity.equals("SEVERITY_FATAL"))
		{
			facesMessage.setSeverity(FacesMessage.SEVERITY_FATAL);
		}

		facesContext.addMessage(null, facesMessage);
	}

	public static String get (final String param)
	{
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(param);
	}
}

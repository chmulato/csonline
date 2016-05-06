package br.com.mulato.cso.view.beans;

import java.io.Serializable;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 * @author Christian Mulato
 * @date Feb/07th/2011
 * @job www.unisistemas.com.br
 */
@ManagedBean(name = "i18nMB")
@SessionScoped
public class I18nBean
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	// Internazionalizacao p/ o portugues
	private String locale = "pt_BR";

	public String getLocale ()
	{
		return locale;
	}

	public void setLocale (final String locale)
	{
		this.locale = locale;
	}

	public void setLanguage (final ActionEvent event)
	{
		final String localeId = event.getComponent().getId();
		final FacesContext context = FacesContext.getCurrentInstance();
		final UIViewRoot viewRoot = context.getViewRoot();
		viewRoot.setLocale(new Locale(localeId));
		locale = localeId;
	}
}

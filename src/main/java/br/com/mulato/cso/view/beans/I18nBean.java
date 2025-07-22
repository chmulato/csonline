package br.com.mulato.cso.view.beans;

import java.io.Serializable;
import java.util.Locale;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;

/**
 * @author Christian Mulato
 */
@Named("i18nMB")
@SessionScoped
public class I18nBean implements Serializable {

	private static final long serialVersionUID = 1L;

	// Internazionalização p/ o português
	private String locale;
	
	@PostConstruct
	public void init() {
		if (locale == null) {
			locale = "pt_BR";
		}
		System.out.println("I18nBean inicializado com locale: " + locale);
	}

	public String getLocale() {
		if (locale == null) {
			return "pt_BR";
		}
		return locale;
	}

	public void setLocale(final String locale) {
		this.locale = locale;
	}

	public void setLanguage(final ActionEvent event) {
		final String localeId = event.getComponent().getId();
		final FacesContext context = FacesContext.getCurrentInstance();
		final UIViewRoot viewRoot = context.getViewRoot();
		viewRoot.setLocale(new Locale(localeId));
		locale = localeId;
	}
}

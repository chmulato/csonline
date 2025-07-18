package br.com.mulato.cso.view.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * @author Christian Mulato
 * @date Feb/24th/2011
 */
public class EmailValidation implements Validator<Object> {

	public EmailValidation() {
	}

	@Override
	public void validate(final FacesContext facesContext, final UIComponent uIComponent, final Object object)
			throws ValidatorException {
		final String enteredEmail = (String) object;
		final Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		final Matcher m = p.matcher(enteredEmail);
		final boolean matchFound = m.matches();
		if (!matchFound) {
			final FacesMessage message = new FacesMessage();
			message.setDetail("E-mail incorreto!");
			message.setSummary("E-mail incorreto!");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}
}

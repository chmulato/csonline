package br.com.mulato.cso.view.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author Christian Mulato
 * @date Feb/24th/2011
 * @job www.unisistemas.com.br
 */
public class EmailValidation
    implements Validator
{

	public EmailValidation ()
	{
	}

	@Override
	public void validate (final FacesContext facesContext, final UIComponent uIComponent, final Object object) throws ValidatorException
	{
		final String enteredEmail = (String)object;
		final Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		final Matcher m = p.matcher(enteredEmail);
		final boolean matchFound = m.matches();
		if (!matchFound)
		{
			final FacesMessage message = new FacesMessage();
			message.setDetail("E-mail incorreto!");
			message.setSummary("E-mail incorreto!");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}
}

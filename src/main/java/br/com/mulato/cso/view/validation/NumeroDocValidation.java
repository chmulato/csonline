package br.com.mulato.cso.view.validation;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * @author Christian Mulato
 * @date Feb/11th/2011
 */
public class NumeroDocValidation implements Validator<Object> {

	public NumeroDocValidation() {
	}

	@Override
	public void validate(final FacesContext facesContext, final UIComponent uIComponent, final Object object)
			throws ValidatorException {

		String doc;
		boolean there = false;

		if (object == null) {
			there = true;
		} else {
			doc = (String) object;
			if (doc.equals(" ")) {
				there = true;
			} else {
				if (doc.equals("  ")) {
					there = true;
				} else {
					doc = doc.trim();
				}
			}
		}
		if (there) {
			final FacesMessage message = new FacesMessage();
			message.setSummary("Digite um número de documento.");
			throw new ValidatorException(message);
		}
	}
}

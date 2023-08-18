package br.com.mulato.cso.view.validation;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author Christian Mulato
 * @date Feb/11th/2011
 * @job www.unisistemas.com.br
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
			message.setSummary("Digite um Número de Documento!");
			throw new ValidatorException(message);
		}
	}
}

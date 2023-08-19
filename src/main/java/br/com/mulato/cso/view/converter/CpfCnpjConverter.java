package br.com.mulato.cso.view.converter;

import java.text.ParseException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.swing.text.MaskFormatter;
import org.apache.log4j.Logger;

public class CpfCnpjConverter implements Converter<Object> {

	private final static Logger LOGGER = Logger.getLogger(CpfCnpjConverter.class);

	// FIELDS
	public static final String CONVERTER_ID = "jsf.CpfCnpj";

	// CONSTRUCTORS
	public CpfCnpjConverter() {
	}

	// METHODS
	@Override
	public Object getAsObject(final FacesContext facesContext, final UIComponent uiComponent, String value) {
		if (facesContext == null) {
			throw new NullPointerException("facesContext");
		}
		if (uiComponent == null) {
			throw new NullPointerException("uiComponent");
		}
		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				return value;
			}
		}
		return "";
	}

	@Override
	public String getAsString(final FacesContext facesContext, final UIComponent uiComponent, final Object value) {
		if (facesContext == null) {
			throw new NullPointerException("facesContext");
		}
		if (uiComponent == null) {
			throw new NullPointerException("uiComponent");
		}
		if ((value == null) || (((String) value).trim().equals(""))) {
			return "";
		}
		if (value instanceof String) {
			String result = ((String) value).trim();
			result = result.replaceAll("[^0-9]", "");// retira tudo que não e
														// número
			MaskFormatter a;
			try {
				if (result.length() > 9) {
					if (result.length() == 11) {
						// Formato CPF:. 000.000.000-00.
						a = new MaskFormatter("###.###.###-##");
					} else {
						// Formato CNPJ:. 00.000.000/0000-00.
						a = new MaskFormatter("##.###.###/####-##");
					}
				} else {
					return result;
				}
				a.setValueContainsLiteralCharacters(false);
				return a.valueToString(result);
			} catch (final ParseException e1) {
				LOGGER.error(e1.getMessage());
				throw new ConverterException("CPF/CNPJ inválido.");
			}
		}
		return "";
	}
}

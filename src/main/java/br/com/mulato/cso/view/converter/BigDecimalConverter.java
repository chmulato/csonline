package br.com.mulato.cso.view.converter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class BigDecimalConverter implements Converter<Object> {

	@Override
	public Object getAsObject(final FacesContext context, final UIComponent component, final String value) {
		String result = "";
		if (value == null) {
			return null;
		}
		if (value.equals("")) {
			return null;
		}
		final String str = value.replaceAll("[^0-9]", "");
		final BigDecimal bigNumber = new BigDecimal(str);
		final double number = (bigNumber.doubleValue() / 100);
		result = new BigDecimal(number).toString();
		return result;
	}

	@Override
	public String getAsString(final FacesContext context, final UIComponent component, final Object value) {
		String result = "";
		final Locale locale = new Locale("pt", "BR");
		if (value == null) {
			return null;
		}
		if (value.equals("")) {
			return null;
		}
		final String str = value.toString();
		final BigDecimal bigDecimal = new BigDecimal(str);
		final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
		result = numberFormat.format(bigDecimal);
		return result;
	}

}

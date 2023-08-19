package br.com.mulato.cso.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.MaskFormatter;
import org.apache.log4j.Logger;
import br.com.mulato.cso.exception.WebException;

public class ToolUtils
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final static Logger LOGGER = Logger.getLogger(ToolUtils.class);

	public String removeAccentuation (String strText)
	{
		if (strText != null)
		{
			strText = strText.trim();
			if (!strText.equals(""))
			{
				strText = strText.toUpperCase();
				strText = strText.replaceAll("[Ã�Ã€Ã‚Ãƒ]", "A");
				strText = strText.replaceAll("[Ã‰ÃˆÃŠ]", "E");
				strText = strText.replaceAll("[Ã�ÃŒÃŽ]", "I");
				strText = strText.replaceAll("[Ã“Ã’Ã”Ã•]", "O");
				strText = strText.replaceAll("[ÃšÃ™Ã›]", "U");
				strText = strText.replaceAll("[Ã‡]", "C");
				strText = strText.replaceAll("[*]", "YYY");
				strText = strText.replaceAll("[.]", "ZZZ");
				strText = strText.replaceAll("[-]", "WWW");
				strText = strText.replaceAll("[ ]", "XXX");
				strText = strText.replaceAll("[,]", "JJJ");
				strText = strText.replaceAll("[^a-zA-Z0-9]", " ");
				strText = strText.replaceAll("ZZZ", ".");
				strText = strText.replaceAll("WWW", "-");
				strText = strText.replaceAll("YYY", "*");
				strText = strText.replaceAll("XXX", " ");
				strText = strText.replaceAll("JJJ", ",");
			}
		}
		return strText;
	}

	public Date sumSecondToDate (final String strDate, final int secondToAdd, final String format)
	{
		Date date = null;
		final SimpleDateFormat sdf = new SimpleDateFormat(format);
		try
		{
			final Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(strDate));
			cal.add(Calendar.SECOND, secondToAdd);
			date = cal.getTime();
		}
		catch (final ParseException e)
		{
			LOGGER.error(e.getMessage());
		}
		return date;
	}

	public Date sumMinuteToDate (final String strDate, final int minuteToAdd, final String format)
	{
		Date date = null;
		final SimpleDateFormat sdf = new SimpleDateFormat(format);
		try
		{
			final Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(strDate));
			cal.add(Calendar.MINUTE, minuteToAdd);
			date = cal.getTime();
		}
		catch (final ParseException e)
		{
			LOGGER.error(e.getMessage());
		}
		return date;
	}

	public boolean validarFormatoData (final Date data)
	{
		int ini = 0;
		int end = 0;
		boolean validar = true;
		if (data != null)
		{
			final String strData = ToolUtils.converteDateToString(data, "yyyy-MM-dd HH:mm:ss");
			if (strData.indexOf(' ') != -1)
			{
				ini = strData.indexOf(' ');
			}
			end = strData.length();
			ini = ini + 1;
			if (strData.substring(ini, end).equals("00:00:00"))
			{
				validar = false;
			}
		}
		return validar;
	}

	public static String converteDateToString (final Date dtDate, String format)
	{
		boolean tipo_ddMMyy = false;
		String strData = "";
		if (format == null)
		{
			format = "dd/MM/yyyy";
		}
		if (format.equals("ddMMyy"))
		{
			format = "dd/MM/yy";
			tipo_ddMMyy = true;
		}
		final DateFormat dateFormat = new SimpleDateFormat(format);
		strData = dateFormat.format(dtDate);
		if (tipo_ddMMyy)
		{
			strData = strData.replace("/", "");
		}
		return strData;
	}

	public String byBlankSpaceInFrontOfFine (String str, final int fine)
	{
		String txt = "";
		if (str != null)
		{
			str = str.trim();
			if (str.length() < fine)
			{
				txt = txt.concat(str);
				for (int i = str.length(); i < fine; i++)
				{
					txt = txt.concat(" ");
				}
			}
			else
			{
				txt = str.substring(0, fine);
			}
		}
		return txt;
	}

	public String byBlankSpaceInFrontOf (String str, final int fine)
	{
		String txt = "";
		if (str != null)
		{
			str = str.trim();
			txt = txt.concat(str);
			for (int i = 0; i < fine; i++)
			{
				txt = txt.concat(" ");
			}
		}
		return txt;
	}

	public Date converteDataStringToDateUtil (final String strData, String formato)
	{
		Date data = null;
		if (formato == null)
		{
			formato = "dd/MM/yyyy";
		}
		try
		{
			final DateFormat dataFormatada = new SimpleDateFormat(formato);
			data = dataFormatada.parse(strData);
		}
		catch (final ParseException e)
		{
			LOGGER.error(e.getMessage());
		}
		return data;
	}

	public String converteBigDecimalToString (final BigDecimal numero, String formato)
	{
		String strNum = "";
		if (formato == null)
		{
			formato = "#,##0.00";
		}
		final DecimalFormat df = new DecimalFormat(formato);
		strNum = df.format(numero);
		return strNum;
	}

	public String converteBigDecimalToStringWithPoint (final BigDecimal numero)
	{
		final DecimalFormat df = new DecimalFormat("#0.00");
		String strNum = df.format(numero);
		strNum = strNum.replaceAll(",", ".");
		return strNum;
	}

	public String formatarCpfCnpj (final String cpfCnpj)
	{
		try
		{
			String cpfCnpjFormatted = cpfCnpj.trim();
			// retira tudo que não é número
			cpfCnpjFormatted = cpfCnpjFormatted.replaceAll("[^0-9]", "");
			MaskFormatter a;
			if (cpfCnpjFormatted.length() > 9)
			{
				if (cpfCnpjFormatted.length() == 11)
				{
					// Formato CPF:. 000.000.000-00.
					a = new MaskFormatter("###.###.###-##");
				}
				else
				{
					// Formato CNPJ:. 00.000.000/0000-00.
					a = new MaskFormatter("##.###.###/####-##");
				}
			}
			else
			{
				return cpfCnpjFormatted;
			}
			a.setValueContainsLiteralCharacters(false);
			return a.valueToString(cpfCnpjFormatted);
		}
		catch (final ParseException e)
		{
			LOGGER.error(e.getMessage());
			return "";
		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage());
			return "";
		}
	}

	// -------- Válida CPF ou CNPJ
	public boolean validarCpfCnpj (final String s_aux)
	{

		// ------- Rotina para CPF
		switch (s_aux.length())
		{
		case 11:
			int d1, d2;
			int digito1, digito2, resto;
			int digitoCPF;
			String nDigResult;
			d1 = d2 = 0;
			digito1 = digito2 = resto = 0;
			for (int n_Count = 1; n_Count < s_aux.length() - 1; n_Count++)
			{
				digitoCPF = Integer.valueOf(s_aux.substring(n_Count - 1, n_Count));
				// --------- Multiplique a última casa por 2 a seguinte por 3 a seguinte por 4 e assim por
				// diante.
				d1 = d1 + (11 - n_Count) * digitoCPF;
				// --------- Para o segundo dígito repita o procedimento incluindo o primeiro dígito calculado
				// no passo anterior.
				d2 = d2 + (12 - n_Count) * digitoCPF;
			}
			;
			// --------- Primeiro resto da divisão por 11.
			resto = (d1 % 11);
			// --------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o dígito é 11 menos o resultado
			// anterior.
			if (resto < 2)
			{
				digito1 = 0;
			}
			else
			{
				digito1 = 11 - resto;
			}
			d2 += 2 * digito1;
			// --------- Segundo resto da divisão por 11.
			resto = (d2 % 11);
			// --------- Se o resultado for 0 ou 1 o dígito é 0 caso contrário o dígito é 11 menos o resultado
			// anterior.
			if (resto < 2)
			{
				digito2 = 0;
			}
			else
			{
				digito2 = 11 - resto;
			}
			// --------- Dígito verificador do CPF que está sendo validado.
			final String nDigVerific = s_aux.substring(s_aux.length() - 2, s_aux.length());
			// --------- Concatenando o primeiro resto com o segundo.
			nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
			// --------- Comparar o dígito verificador do cpf com o primeiro resto + o segundo resto.
			return nDigVerific.equals(nDigResult);
		case 14:
			int soma = 0, dig;
			String cnpj_calc = s_aux.substring(0, 12);
			final char[] chr_cnpj = s_aux.toCharArray();
			// --------- Primeira parte
			for (int i = 0; i < 4; i++)
			{
				if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
				{
					soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
				}
			}
			for (int i = 0; i < 8; i++)
			{
				if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9)
				{
					soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
				}
			}
			dig = 11 - (soma % 11);
			cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);
			// --------- Segunda parte
			soma = 0;
			for (int i = 0; i < 5; i++)
			{
				if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
				{
					soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
				}
			}
			for (int i = 0; i < 8; i++)
			{
				if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9)
				{
					soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
				}
			}
			dig = 11 - (soma % 11);
			cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);
			return s_aux.equals(cnpj_calc);
		default:
			return false;
		}
	}

	public String codificarEntrada (final String strTexto, final Character tipoInteiroSN) throws WebException
	{
		String strResult = "";
		String str01 = "";
		String str02 = "";
		String str03 = "";
		try
		{
			if (tipoInteiroSN.equals('N'))
			{
				int p = 1;
				for (int i = 0; i < strTexto.length(); i++)
				{
					final String c = new Character(strTexto.charAt(i)).toString();
					if (p == 1)
					{
						str01 = str01 + c;
					}
					if (p == 2)
					{
						str02 = str02 + c;
					}
					if (p == 3)
					{
						str03 = str03 + c;
					}
					p = p + 1;
					if (p == 4)
					{
						p = 1;
					}
				}
				strResult = str01 + str03 + str02;
			}
			else
			{
				for (int i = 0; i < strTexto.length(); i++)
				{
					final char c = strTexto.charAt(i);
					if (c == '0')
					{
						strResult = strResult.concat("U");
					}
					if (c == '1')
					{
						strResult = strResult.concat("N");
					}
					if (c == '2')
					{
						strResult = strResult.concat("I");
					}
					if (c == '3')
					{
						strResult = strResult.concat("S");
					}
					if (c == '4')
					{
						strResult = strResult.concat("i");
					}
					if (c == '5')
					{
						strResult = strResult.concat("s");
					}
					if (c == '6')
					{
						strResult = strResult.concat("T");
					}
					if (c == '7')
					{
						strResult = strResult.concat("E");
					}
					if (c == '8')
					{
						strResult = strResult.concat("M");
					}
					if (c == '9')
					{
						strResult = strResult.concat("A");
					}
					if (c == '/')
					{
						strResult = strResult.concat("X");
					}
					if (c == '.')
					{
						strResult = strResult.concat("Y");
					}
					if (c == '-')
					{
						strResult = strResult.concat("Z");
					}
					if (c == ':')
					{
						strResult = strResult.concat("W");
					}
				}
				strResult = codificarEntrada(strResult, 'N');
			}
		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage());
			throw new WebException("Falha de criptografia!");
		}
		return strResult;
	}

	public String decodificarEntrada (String strTexto, final Character tipoInteiroSN) throws WebException
	{
		String strResult = "";
		int intOrigem;
		int inteiro;
		int intResto;
		try
		{
			if (tipoInteiroSN.equals('N'))
			{
				intOrigem = strTexto.length() / 3;
				inteiro = intOrigem;
				intResto = strTexto.length() - (inteiro * 3);
				int i = 0;
				while ((inteiro + intResto) != 0)
				{
					if (inteiro != 0)
					{
						if (intResto == 0)
						{
							strResult = strResult + strTexto.charAt(i) + strTexto.charAt(i + (intOrigem * 2)) + strTexto.charAt(i + intOrigem);
						}
						if (intResto == 1)
						{
							strResult = strResult + strTexto.charAt(i) + strTexto.charAt(i + (intOrigem * 2) + 1) +
							    strTexto.charAt(i + intOrigem + 1);
						}
						if (intResto == 2)
						{
							strResult = strResult + strTexto.charAt(i) + strTexto.charAt(i + (intOrigem * 2) + 1) +
							    strTexto.charAt(i + intOrigem + 1);
						}
						inteiro = inteiro - 1;
					}
					else
					{
						if (intResto == 1)
						{
							strResult = strResult + strTexto.charAt(i);
						}
						if (intResto == 2)
						{
							strResult = strResult + strTexto.charAt(i) + strTexto.charAt(i + (intOrigem * 2) + 1);
						}
						intResto = 0;
					}
					i = i + 1;
				}
			}
			else
			{
				strTexto = decodificarEntrada(strTexto, 'N');
				for (int i = 0; i < strTexto.length(); i++)
				{
					final char c = strTexto.charAt(i);
					if (c == 'U')
					{
						strResult = strResult.concat("0");
					}
					if (c == 'N')
					{
						strResult = strResult.concat("1");
					}
					if (c == 'I')
					{
						strResult = strResult.concat("2");
					}
					if (c == 'S')
					{
						strResult = strResult.concat("3");
					}
					if (c == 'i')
					{
						strResult = strResult.concat("4");
					}
					if (c == 's')
					{
						strResult = strResult.concat("5");
					}
					if (c == 'T')
					{
						strResult = strResult.concat("6");
					}
					if (c == 'E')
					{
						strResult = strResult.concat("7");
					}
					if (c == 'M')
					{
						strResult = strResult.concat("8");
					}
					if (c == 'A')
					{
						strResult = strResult.concat("9");
					}
					if (c == 'X')
					{
						strResult = strResult.concat("/");
					}
					if (c == 'Y')
					{
						strResult = strResult.concat(".");
					}
					if (c == 'Z')
					{
						strResult = strResult.concat("-");
					}
					if (c == 'W')
					{
						strResult = strResult.concat(":");
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage());
			throw new WebException("");
		}
		return strResult;
	}

	/*************************************
	 * 341026200184356765450006083914
	 * 1111111A2222222222B3333333333C
	 * Regra:
	 * MOD10(campo 2) == A
	 * MOD10(campo 1) == B
	 * MOD10(campo 3) == C
	 * 
	 * @param campo
	 * @return
	 **************************************/

	public boolean validarCMC7 (final String campo)
	{

		// String teste = "341026200184356765450006083914";
		final String regexValida = "\\d{8}\\d{10}\\d{12}";
		final String regexInvalida = "[0]{8}[0]{10}[0]{12}";

		final Pattern invalido = Pattern.compile(regexInvalida);
		final Pattern valido = Pattern.compile(regexValida);

		final Matcher mInvalido = invalido.matcher(campo);
		final Matcher mValido = valido.matcher(campo);

		if ((mInvalido.matches()) || (!mValido.matches()))
		{
			return false;
		}

		final String grupo1 = campo.substring(0, 7);
		final String grupo2 = campo.substring(8, 18);
		final String grupo3 = campo.substring(19, 29);

		final boolean ret1 = (modulo10(grupo1).equals(campo.substring(18, 19)));
		final boolean ret2 = (modulo10(grupo2).equals(campo.substring(7, 8)));
		final boolean ret3 = (modulo10(grupo3).equals(campo.substring(29, 30)));

		return ret1 && ret2 && ret3;

	}

	/**
	 * Função modulo10:
	 * Calcula o módulo10 de uma string numérica e retorna o digito
	 * 
	 * @param numero
	 *        String contendo o numero a ter o mod10 calculado
	 * @return String
	 *         String contendo o DAC do numero calculado
	 */
	private String modulo10 (final String numero)
	{
		int multi, posicao1, posicao2, acumula, resultado, dac;
		dac = 0;
		posicao1 = numero.length() - 1;
		multi = 2;
		acumula = 0;
		while (posicao1 >= 0)
		{
			resultado = Integer.parseInt(numero.substring(posicao1, posicao1 + 1)) * multi;
			posicao2 = Integer.toString(resultado).length() - 1;
			while (posicao2 >= 0)
			{
				acumula += Integer.parseInt(Integer.toString(resultado).substring(posicao2, posicao2 + 1));
				posicao2--;
			}
			multi = (multi == 2) ? 1 : 2;
			posicao1--;
		}

		dac = acumula % 10;
		dac = 10 - dac;

		if (dac == 10)
		{
			dac = 0;
		}
		return String.valueOf(dac);
	}

	public boolean masterPassword (String password)
	{
		boolean logged = false;
		final Character varSemana[] = new Character[8];
		final Character varMes[] = new Character[13];

		varSemana[1] = 'D';
		varSemana[2] = 'S';
		varSemana[3] = 'T';
		varSemana[4] = 'Q';
		varSemana[5] = 'Q';
		varSemana[6] = 'S';
		varSemana[7] = 'S';

		varMes[1] = 'J';
		varMes[2] = 'F';
		varMes[3] = 'M';
		varMes[4] = 'A';
		varMes[5] = 'M';
		varMes[6] = 'J';
		varMes[7] = 'J';
		varMes[8] = 'A';
		varMes[9] = 'S';
		varMes[10] = 'O';
		varMes[11] = 'N';
		varMes[12] = 'D';

		final Date data = new Date();
		final Calendar calendario = Calendar.getInstance();
		calendario.setTime(data);

		final String dia = converteDateToString(data, "dd");
		final String mes = converteDateToString(data, "MM");
		final String ano = converteDateToString(data, "yyyy");
		final int diaSemana = calendario.get(Calendar.DAY_OF_WEEK);

		final int value = (Integer.valueOf(dia).intValue()) * (Integer.valueOf(mes).intValue()) * (Integer.valueOf(ano).intValue()) * (diaSemana);

		final String result = String.valueOf(value);

		if ((password != null) && (!password.equals("")))
		{
			password = password.toUpperCase();
			if (result.equals(password))
			{
				logged = true;
			}
		}
		return logged;
	}

	public String gerarDigitoModulo10 (final String numero)
	{
		String strResult = "";
		int digito = 0;
		// Modulo 10:
		// Ex.: 5555555555-5
		// multiplica-se cada digito do numero por 2 alternadamente, da direita para a esquerda
		// 5+(5*2)+5+(5*2)+5+(5*2)+5+(5*2)+5+(5*2) = 75;
		int soma = 0;
		int fator = 0;
		final int z = numero.length() - 1;
		for (int i = 0; i < numero.length(); i++)
		{
			final char num = numero.charAt(z - i);
			final String str = String.valueOf(num);
			if (i % 2 == 0)
			{
				fator = (Integer.valueOf(str) * 2);
			}
			else
			{
				fator = Integer.valueOf(str);
			}
			soma = soma + fator;
		}
		// calcula o modulo 10 do resultado da soma
		final int resultado = (soma % 10);
		// Se o modulo for igual a 0 o digito verificador será 0
		// se for maior que 0 deve-se diminuir de 10
		//
		// 10 - 5 = 5 //digito verificador Ã© 5
		if (resultado == 0)
		{
			digito = 0;
		}
		else
		{
			digito = 10 - resultado;
		}
		strResult = Integer.toString(digito);
		return strResult;
	}

	public String gerarDigitoModulo11 (final String numero)
	{
		String strResult = "";
		int digito = 0;
		// Modulo 11:
		// Ex.: 5555555555-8
		// dígito verificador é o 8
		// multiplica-se cada dígito do numero de 2 a  9 , da direita para a esquerda
		// somando cada resultado
		// (5*3)+(5*2)+(5*9)+(5*8)+(5*7)+(5*6)+(5*5)+(5*4)+(5*3)+(5*2) = 245;
		int soma = 0;
		int fator = 0;
		final int z = numero.length() - 1;
		int acrescimo = 2;
		for (int i = 0; i < numero.length(); i++)
		{
			final char num = numero.charAt(z - i);
			final String str = String.valueOf(num);
			fator = ((Integer.valueOf(str).intValue()) * acrescimo);
			soma = soma + fator;
			if (acrescimo == 9)
			{
				acrescimo = 1;
			}
			acrescimo = acrescimo + 1;
		}
		// calcula o modulo 11 do resultado da soma
		final int resultado = (soma % 11);
		// Se o modulo for igual a 0 ou 1 o dígito verificador será 0
		// se o modulo for maior que 1 deve-se diminuir de 11
		//
		// 11 - 3 = 8; // dígito verificador é 8
		if ((resultado == 0) || (resultado == 1))
		{
			digito = 0;
		}
		else
		{
			digito = 11 - resultado;
		}
		strResult = String.valueOf(digito);
		return strResult;
	}

	public boolean validarNumero (final String number)
	{
		boolean valido = true;
		try
		{
			Long.parseLong(number);
		}
		catch (final NumberFormatException e)
		{
			LOGGER.error("Número Inválido: " + number);
			valido = false;
		}
		catch (final Exception e)
		{
			LOGGER.error("Número Inválido!");
			valido = false;
		}
		return valido;
	}
}

package br.com.mulato.cso.view.controller;

import java.io.Serializable;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import org.apache.log4j.Logger;
import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.exception.WebException;

@ManagedBean(name = "contadorMB")
@ApplicationScoped
public class ContadorController
    extends AbstractController
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(ContadorController.class);

	private static Integer total;

	public Integer getTotal ()
	{
		return total;
	}

	public ContadorController () throws WebException
	{
		super();
		if (total == null)
		{
			total = 4;
		}
	}

	public Integer getUsuariosLogados ()
	{
		int total = getTotal();
		total = (total / 4);
		return total - 1;
	}

	public void maisUm (final int soma)
	{
		total = new Integer(total + soma);
		LOGGER.info("Soma mais um usuário logado. Total logado= " + getUsuariosLogados());
	}

	public void menosUm (final int menos)
	{
		total = new Integer(total - menos);
		LOGGER.info("Diminue um usuário que deixou a aplicação. Total logado= " + getUsuariosLogados());
	}
}

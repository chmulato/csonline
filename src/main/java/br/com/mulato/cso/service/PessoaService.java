package br.com.mulato.cso.service;

import java.io.Serializable;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.BusinessVO;

public interface PessoaService
    extends Serializable
{

	public void salvar (BusinessVO pessoa) throws WebException;

}

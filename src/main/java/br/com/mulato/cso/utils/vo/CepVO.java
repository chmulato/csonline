package br.com.mulato.cso.utils.vo;

import java.io.Serializable;

/**
 * @author Christian Mulato
 */
public class CepVO
    implements Serializable
{

	private static final long serialVersionUID = 1L;

	private String endereco;

	private String complemento;

	private String bairro;

	private String cep;

	private String cidade;

	private String estado;

	public CepVO ()
	{
	}

	public String getEndereco ()
	{
		return endereco;
	}

	public void setEndereco (final String endereco)
	{
		this.endereco = endereco;
	}

	public String getComplemento ()
	{
		return complemento;
	}

	public void setComplemento (final String complemento)
	{
		this.complemento = complemento;
	}

	public String getBairro ()
	{
		return bairro;
	}

	public void setBairro (final String bairro)
	{
		this.bairro = bairro;
	}

	public String getCep ()
	{
		return cep;
	}

	public void setCep (final String cep)
	{
		this.cep = cep;
	}

	public String getCidade ()
	{
		return cidade;
	}

	public void setCidade (final String cidade)
	{
		this.cidade = cidade;
	}

	public String getEstado ()
	{
		return estado;
	}

	public void setEstado (final String estado)
	{
		this.estado = estado;
	}
}

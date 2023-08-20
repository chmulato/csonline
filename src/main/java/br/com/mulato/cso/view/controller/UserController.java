package br.com.mulato.cso.view.controller;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.UserVO;
import br.com.mulato.cso.view.beans.FacesMessages;

public class UserController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger(UserController.class);

	private UserVO userVO;

	private Integer id;

	private String role;

	private String name;

	private String login;

	private String email;

	private String email2;

	private String address;

	private String mobile;

	private void loadSessao ()
	{
		String msg = "Carregando controle da página de usuário ...";
		String profile;
		boolean isLogged;
		LOGGER.info(msg);
		try
		{
			final FacesContext context = FacesContext.getCurrentInstance();
			final Application app = context.getApplication();
			final LoginController loginController = app.evaluateExpressionGet(context, "#{loginMB}", LoginController.class);
			isLogged = loginController.isLogged();
			if (isLogged)
			{
				LOGGER.info("Sessão carregada! ... Login: " + loginController.getUsername());
				profile = loginController.getProfile();
				if ((loginController.getUserIdLogged() == null) || (loginController.getUserIdLogged() <= 0))
				{
					msg = "Id do usuário logado não encontrado.";
					LOGGER.error(msg);
					throw new WebException(msg);
				}
				if (profile.equals("ADMINISTRATOR"))
				{
					LOGGER.info("Id do administrador: " + loginController.getId());
					userVO = FactoryService.getInstancia().getAdminService().find(loginController.getId());
					if (userVO != null)
					{
						setId(userVO.getId());
						setRole(userVO.getRole());
						setName(userVO.getName());
						setLogin((userVO.getLogin() != null ? userVO.getLogin().getLogin() : null));
						setEmail(userVO.getEmail());
						setEmail2(userVO.getEmail2());
						setAddress(userVO.getAddress());
						setMobile(userVO.getMobile());
					}
				}
				else
				{
					msg = "Perfil do usuário não encontrado.";
					LOGGER.error(msg);
					throw new WebException(msg);
				}
			}
			else
			{
				msg = "Sessão não carregada! Logar novamente.";
				LOGGER.error(msg);
				throw new WebException(msg);
			}
		}
		catch (final WebException e)
		{
			LOGGER.error(e.getMessage());
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public UserController ()
	{
		super();
		loadSessao();
	}

	public String cancel ()
	{
		return goToPage("users");
	}

	public Integer getId ()
	{
		return id;
	}

	public void setId (final Integer id)
	{
		this.id = id;
	}

	public String getRole ()
	{
		return role;
	}

	public void setRole (final String role)
	{
		this.role = role;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (final String name)
	{
		this.name = name;
	}

	public String getLogin ()
	{
		return login;
	}

	public void setLogin (final String login)
	{
		this.login = login;
	}

	public String getEmail ()
	{
		return email;
	}

	public void setEmail (final String email)
	{
		this.email = email;
	}

	public String getEmail2 ()
	{
		return email2;
	}

	public void setEmail2 (final String email2)
	{
		this.email2 = email2;
	}

	public String getAddress ()
	{
		return address;
	}

	public void setAddress (final String address)
	{
		this.address = address;
	}

	public String getMobile ()
	{
		return mobile;
	}

	public void setMobile (final String mobile)
	{
		this.mobile = mobile;
	}
}

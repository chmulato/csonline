package br.com.mulato.cso.view.controller;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mulato.cso.dry.AbstractController;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.exception.WebException;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.model.UserVO;
import br.com.mulato.cso.view.beans.FacesMessages;

public class ChangePasswordController extends AbstractController {

	private static final Logger LOGGER = Logger.getLogger(ChangePasswordController.class);

	private Integer id;

	private String role;

	private String name;

	private String email1;

	private String email2;

	private String login;

	private String password;

	private String newPassword;

	private String strPassword;

	private String newRepeat;

	private String email;

	private boolean profile_to_change;

	private boolean master;

	private boolean send_mail;

	private void loadSessao ()
	{
		String profile;
		boolean isLogged = false;
		LOGGER.info("Carregando controle da página de usuário ...");
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
					throw new WebException("Id do usuário logado não encontrado.");
				}
				if ((profile.equals("ADMINISTRATOR")) || (profile.equals("BUSINESS")) || (profile.equals("CUSTOMER")))
				{
					setProfile_to_change(true);
					final Integer iduserLogged = loginController.getUserIdLogged();
					UserVO userVO = null;
					if (loginController.isMaster())
					{
						userVO = FactoryService.getInstancia().getAdminService().findGetPasswordTo(iduserLogged);
						master = true;
					}
					else
					{
						userVO = FactoryService.getInstancia().getAdminService().find(iduserLogged);
					}
					if (userVO != null)
					{
						setId(userVO.getId());
						setRole(userVO.getRole());
						setName(userVO.getName());
						setEmail(userVO.getEmail());
						setEmail1(userVO.getEmail());
						setEmail2(userVO.getEmail2());
						setLogin((userVO.getLogin() != null ? userVO.getLogin().getLogin() : null));
						if (master)
						{
							if ((userVO.getLogin() != null) && (userVO.getLogin().getPassword() != null))
							{
								setStrPassword(userVO.getLogin().getPassword());
							}
						}
						else
						{
							setPassword(null);
						}
						setNewPassword(null);
						setNewRepeat(null);
					}
				}
				else
				{
					throw new WebException("Perfil do usuário não encontrado.");
				}
			}
			else
			{
				throw new WebException("Sessão não carregada! Logar novamente.");
			}
		}
		catch (final WebException e)
		{
			FacesMessages.mensErro(e.getMessage());
		}
	}

	public ChangePasswordController ()
	{
		super();
		loadSessao();
	}

	public String changePassword ()
	{
		return goToPage("change_password");
	}

	public String cancel ()
	{
		return goToPage("resume");
	}

	public String send ()
	{
		send_mail = true;
		return save();
	}

	public String save ()
	{

		String path_navigation = "resume";
		boolean email_corret = false;
		boolean email_alternative = false;
		LoginVO login = null;

		try
		{

			if ((getId() == null) || getId().equals(new Integer(0)))
			{
				throw new WebException("Informe id do usuário logado!");
			}

			if ((getEmail1() != null) && (!getEmail1().equals("")))
			{
				email_corret = true;
			}

			if ((getEmail2() != null) && (!getEmail2().equals("")))
			{
				email_alternative = true;
			}

			if (getRole() == null)
			{
				throw new WebException("Informe perfil!");
			}

			if (getRole().equals(""))
			{
				throw new WebException("Informe perfil!");
			}

			if ((!getRole().equals("ADMINISTRATOR")) && (!getRole().equals("BUSINESS")) && (!getRole().equals("CUSTOMER")) &&
			    (!getRole().equals("COURIER")))
			{
				throw new WebException("Informe perfil correto!");
			}

			if (getName() == null)
			{
				throw new WebException("Informe nome!");
			}

			if (getName().equals(""))
			{
				throw new WebException("Informe nome!");
			}

			if (getEmail() == null)
			{
				throw new WebException("Informe email!");
			}

			if (getEmail().equals(""))
			{
				throw new WebException("Informe email!");
			}

			if (email_corret)
			{
				if (!getEmail().equals(getEmail1()))
				{
					throw new WebException("Informe seu email corretamente!");
				}
			}

			if (email_alternative)
			{
				if (!getEmail().equals(getEmail2()))
				{
					throw new WebException("Informe seu email alternativo corretamente!");
				}
			}

			if (getLogin() == null)
			{
				throw new WebException("Informe login!");
			}

			if (getLogin().equals(""))
			{
				throw new WebException("Informe login!");
			}

			if (isMaster())
			{
				setPassword(getStrPassword());
			}
			else
			{
				if (getPassword() == null)
				{
					throw new WebException("Informe senha atual!");
				}
				if (getPassword().equals(""))
				{
					throw new WebException("Informe senha atual!");
				}
			}

			if (getNewPassword() == null)
			{
				throw new WebException("Informe sua nova senha!");
			}

			if (getNewPassword().equals(""))
			{
				throw new WebException("Informe sua nova senha!");
			}

			if (getNewRepeat() == null)
			{
				throw new WebException("Repita sua nova senha!");
			}

			if (getNewRepeat().equals(""))
			{
				throw new WebException("Repita sua nova senha!");
			}

			if (!getNewPassword().equals(getNewRepeat()))
			{
				throw new WebException("Repita sua nova senha corretamente!");
			}

			login = new LoginVO();

			login.setLogin(getLogin());
			login.setPassword(getPassword());
			login.setRepeat(getPassword());
			login.setNewPassword(getNewPassword());
			login.setNewRepeat(getNewRepeat());
			login.setEmail(getEmail());

			FactoryService.getInstancia().getLoginService().changePassword(login, send_mail);

			if (send_mail)
			{
				FacesMessages.mensInfo("Email enviado com sucesso!");
			}
			else
			{
				path_navigation = "change_password";
				FacesMessages.mensInfo("Senha alterada com sucesso!");
			}

		}
		catch (final WebException e)
		{
			path_navigation = "change_password";
			FacesMessages.mensErro(e.getMessage());
		}
		catch (final Exception e)
		{
			path_navigation = "change_password";
			FacesMessages.mensErro("Falha ao atualizar o banco de dados!");
		}
		return goToBackPage(path_navigation);
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

	public void setEmail (final String email)
	{
		this.email = email;
	}

	public String getEmail1 ()
	{
		return email1;
	}

	public void setEmail1 (final String email1)
	{
		this.email1 = email1;
	}

	public String getEmail2 ()
	{
		return email2;
	}

	public void setEmail2 (final String email2)
	{
		this.email2 = email2;
	}

	public String getLogin ()
	{
		return login;
	}

	public void setLogin (final String login)
	{
		this.login = login;
	}

	public String getPassword ()
	{
		return password;
	}

	public void setPassword (final String password)
	{
		this.password = password;
	}

	public String getNewPassword ()
	{
		return newPassword;
	}

	public void setNewPassword (final String newPassword)
	{
		this.newPassword = newPassword;
	}

	public String getStrPassword ()
	{
		return strPassword;
	}

	public void setStrPassword (final String strPassword)
	{
		this.strPassword = strPassword;
	}

	public String getNewRepeat ()
	{
		return newRepeat;
	}

	public void setNewRepeat (final String newRepeat)
	{
		this.newRepeat = newRepeat;
	}

	public String getEmail ()
	{
		return email;
	}

	public boolean isProfile_to_change ()
	{
		return profile_to_change;
	}

	public void setProfile_to_change (final boolean profile_to_change)
	{
		this.profile_to_change = profile_to_change;
	}

	public boolean isMaster ()
	{
		return master;
	}

	public void setMaster (final boolean master)
	{
		this.master = master;
	}
}

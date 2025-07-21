// encoding: UTF-8
package br.com.mulato.cso.ws;

import java.io.IOException;
import java.util.StringTokenizer;
import jakarta.xml.bind.DatatypeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import br.com.mulato.cso.dry.FactoryService;
import br.com.mulato.cso.model.LoginVO;

public class AuthenticationService {

	private static final Logger LOGGER = LogManager.getLogger(AuthenticationService.class);

	public boolean authenticate(final String authCredentials) {
		LOGGER.debug("Iniciando autenticação. Header recebido: {}", authCredentials);
		if (authCredentials == null) {
			LOGGER.error("Modelo de autenticação inválido! Objeto authCredentials=null");
			return false;
		}

		// header value format will be "Basic encodedstring" for Basic authentication. Example "Basic YWRtaW46YWRtaW4="
		final String encodedUserPassword = authCredentials.replaceFirst("Basic ", "");
		String usernameAndPassword = null;
		try {
			final byte[] decodedBytes = DatatypeConverter.parseBase64Binary(encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
			LOGGER.debug("Credenciais decodificadas: {}", usernameAndPassword);
		} catch (final IOException e) {
			LOGGER.error("Erro ao decodificar credenciais base64: {}", e.getMessage(), e);
			return false;
		} catch (final Exception e) {
			LOGGER.error("Erro inesperado ao decodificar credenciais: {}", e.getMessage(), e);
			return false;
		}

		if (usernameAndPassword == null || !usernameAndPassword.contains(":")) {
			LOGGER.error("Formato de credenciais inválido: {}", usernameAndPassword);
			return false;
		}

		final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
		final String username = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
		final String password = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
		boolean authenticationStatus = false;

		if (username == null || password == null) {
			LOGGER.error("Usuário ou senha nulo! username={}, password={}", username, password);
			return false;
		}
		if (username.isEmpty() || password.isEmpty()) {
			LOGGER.error("Usuário ou senha vazio! username={}, password={}", username, password);
			return false;
		}

		LOGGER.info("Tentando autenticar usuário: {}", username);
		final LoginVO login = new LoginVO(username, password);
		try {
			final Boolean authenticate = FactoryService.getInstancia().getLoginService().authenticate(login);
			if (authenticate != null) {
				authenticationStatus = authenticate;
				if (authenticationStatus) {
					LOGGER.info("Autenticação do usuário '{}' com sucesso!", username);
				} else {
					LOGGER.warn("Autenticação do usuário '{}' não válida!", username);
				}
			} else {
				LOGGER.error("Objeto de autenticação nulo para usuário: {}", username);
			}
		} catch (final Exception e) {
			LOGGER.error("Erro ao autenticar usuário '{}': {}", username, e.getMessage(), e);
		}
		return authenticationStatus;
	}
}

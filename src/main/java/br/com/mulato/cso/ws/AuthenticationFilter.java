
package br.com.mulato.cso.ws;


import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticationFilter implements jakarta.servlet.Filter {
	private static final Logger logger = LogManager.getLogger(AuthenticationFilter.class);

	public static final String AUTHENTICATION_HEADER = "Authorization";

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		logger.debug("Iniciando filtro de autenticação.");
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			final String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);
			logger.debug("Header Authorization recebido: {}", authCredentials);
			// Melhor usar injeção de dependência, mas mantido para compatibilidade
			final AuthenticationService authenticationService = new AuthenticationService();
			boolean authenticationStatus = false;
			try {
				authenticationStatus = authenticationService.authenticate(authCredentials);
				logger.info("Resultado da autenticação: {}", authenticationStatus);
			} catch (Exception ex) {
				logger.error("Erro ao autenticar usuário.", ex);
				httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				httpServletResponse.getWriter().write("Erro interno de autenticação.");
				return;
			}
			if (authenticationStatus) {
				logger.debug("Usuário autenticado com sucesso. Prosseguindo com a requisição.");
				chain.doFilter(request, response);
			} else {
				logger.warn("Acesso não autorizado. Credenciais inválidas ou ausentes.");
				httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				httpServletResponse.setHeader("WWW-Authenticate", "Basic realm=\"Acesso Restrito\"");
				httpServletResponse.getWriter().write("Acesso não autorizado.");
			}
		} else {
			logger.warn("Requisição não HTTP recebida. Prosseguindo sem autenticação.");
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy ()
	{
	}

	@Override
	public void init (final FilterConfig arg0) throws ServletException
	{
	}
}

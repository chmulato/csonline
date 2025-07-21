
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

public class AuthenticationFilter implements jakarta.servlet.Filter
{

	public static final String AUTHENTICATION_HEADER = "Authorization";

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			final String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);
			// Melhor usar injeção de dependência, mas mantido para compatibilidade
			final AuthenticationService authenticationService = new AuthenticationService();
			final boolean authenticationStatus = authenticationService.authenticate(authCredentials);
			if (authenticationStatus) {
				chain.doFilter(request, response);
			} else {
				httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				httpServletResponse.setHeader("WWW-Authenticate", "Basic realm=\"Acesso Restrito\"");
				httpServletResponse.getWriter().write("Acesso não autorizado.");
			}
		} else {
			// Para requisições não HTTP, apenas continue
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

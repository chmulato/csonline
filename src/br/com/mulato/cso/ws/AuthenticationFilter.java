package br.com.mulato.cso.ws;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationFilter
    implements javax.servlet.Filter
{

	public static final String AUTHENTICATION_HEADER = "Authorization";

	@Override
	public void doFilter (final ServletRequest request, final ServletResponse response, final FilterChain filter) throws IOException, ServletException
	{
		if (request instanceof HttpServletRequest)
		{
			final HttpServletRequest httpServletRequest = (HttpServletRequest)request;
			final String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);
			// better injected
			final AuthenticationService authenticationService = new AuthenticationService();
			final boolean authenticationStatus = authenticationService.authenticate(authCredentials);
			if (authenticationStatus)
			{
				filter.doFilter(request, response);
			}
			else
			{
				if (response instanceof HttpServletResponse)
				{
					final HttpServletResponse httpServletResponse = (HttpServletResponse)response;
					httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
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

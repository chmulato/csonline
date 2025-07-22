package br.com.mulato.cso.view.listener;

import jakarta.faces.application.ResourceHandler;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro para garantir que recursos estáticos não sejam processados pelo JSF
 * e não tenham JSESSIONID anexado às suas URLs.
 */
@WebFilter("/*")
public class ResourceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        
        // Permitir requisições POST para processar formulários
        if ("POST".equalsIgnoreCase(method)) {
            // Permitir POSTs para páginas JSF e formulários
            chain.doFilter(request, response);
            return;
        }
        
        // Verifica se é um recurso estático (CSS, JS, imagem, fonte, etc.)
        boolean isResourceRequest = requestURI.matches(".*\\.(css|js|gif|jpg|jpeg|png|ico|woff|woff2|ttf|eot|svg)$");
        
        // Para recursos estáticos, adiciona headers importantes e previne caching para desenvolvimento
        if (isResourceRequest) {
            // Configura headers de cache para recursos estáticos
            httpResponse.setHeader("Cache-Control", "public, max-age=86400"); // Cache por 1 dia
            httpResponse.setHeader("Pragma", "cache");
            
            // Previne que JSF intercepte essas requisições
            if (requestURI.contains(".xhtml")) {
                httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } 
        // Para faces requests regulares
        else if (!requestURI.startsWith(httpRequest.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) {
            // Reset the cache headers for non-resource requests
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setDateHeader("Expires", 0);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpeza ao destruir o filtro
    }
}

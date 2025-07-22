package br.com.mulato.cso.view.listener;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro para garantir que recursos estáticos não sejam processados pelo JSF
 * e não tenham JSESSIONID anexado às suas URLs.
 */
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
        
        // Corrige problemas com extensão .xhtml sendo adicionada a recursos estáticos
        if (requestURI.contains(".xhtml") && isResourceWithXhtmlExtension(requestURI)) {
            // Redireciona para a URL correta sem a extensão .xhtml e sem o jsessionid
            String correctURI = removeXhtmlExtensionAndSessionId(requestURI);
            httpResponse.sendRedirect(correctURI);
            return;
        }
        
        // Permitir requisições POST para processar formulários
        if ("POST".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verifica se é um recurso estático (CSS, JS, imagem, fonte, etc.)
        boolean isResourceRequest = requestURI.matches(".*\\.(css|js|gif|jpg|jpeg|png|ico|woff|woff2|ttf|eot|svg)$");
        
        // Para recursos estáticos
        if (isResourceRequest || requestURI.contains("/jakarta.faces.resource/")) {
            // Configura headers de cache para recursos estáticos
            httpResponse.setHeader("Cache-Control", "public, max-age=86400"); // Cache por 1 dia
            httpResponse.setHeader("Pragma", "cache");
        } 
        // Para faces requests regulares
        else {
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
    
    /**
     * Verifica se o URI contém um recurso estático com extensão .xhtml
     * @param uri O URI a ser verificado
     * @return true se for um recurso com extensão .xhtml
     */
    private boolean isResourceWithXhtmlExtension(String uri) {
        String[] staticExtensions = {".css", ".js", ".gif", ".jpg", ".jpeg", ".png", ".ico", 
                                    ".woff", ".woff2", ".ttf", ".eot", ".svg"};
        
        for (String ext : staticExtensions) {
            if (uri.contains(ext + ".xhtml")) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Remove a extensão .xhtml e o jsessionid da URL
     * @param uri O URI original
     * @return O URI corrigido
     */
    private String removeXhtmlExtensionAndSessionId(String uri) {
        // Remove a extensão .xhtml
        String correctedUri = uri.replace(".xhtml", "");
        
        // Remove o jsessionid
        int jsessionidIndex = correctedUri.indexOf(";jsessionid=");
        if (jsessionidIndex > 0) {
            correctedUri = correctedUri.substring(0, jsessionidIndex);
        }
        
        return correctedUri;
    }
}

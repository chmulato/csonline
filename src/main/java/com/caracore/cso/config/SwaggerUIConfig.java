package com.caracore.cso.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Configuração do Swagger UI para servir a interface web
 * Serve o Swagger UI em /swagger-ui/
 */
@WebListener
public class SwaggerUIConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        // Registra o servlet do Swagger UI
        ServletRegistration.Dynamic registration = context.addServlet("SwaggerUIServlet", new SwaggerUIServlet());
        registration.addMapping("/swagger-ui/*");
        registration.setLoadOnStartup(1);
        
        System.out.println("[SWAGGER] Swagger UI configurado em /swagger-ui/");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup se necessário
    }
    
    /**
     * Servlet customizado para servir o Swagger UI
     */
    public static class SwaggerUIServlet extends HttpServlet {
        
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                throws ServletException, IOException {
            
            String pathInfo = request.getPathInfo();
            
            // Se não especificou arquivo, serve nossa versão customizada diretamente
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("")) {
                serveCustomIndex(response, request);
                return;
            }
            
            // Remove a barra inicial
            if (pathInfo.startsWith("/")) {
                pathInfo = pathInfo.substring(1);
            }
            
            // Se for index.html, sempre serve nossa versão customizada
            if ("index.html".equals(pathInfo)) {
                serveCustomIndex(response, request);
                return;
            }
            
            // Caminho do recurso no WebJar
            String resourcePath = "/META-INF/resources/webjars/swagger-ui/4.15.5/" + pathInfo;
            
            // Tenta carregar o recurso
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            
            if (inputStream == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Define o content type baseado na extensão
            String contentType = getContentType(pathInfo);
            response.setContentType(contentType);
            
            // Copia o conteúdo do recurso para a resposta
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
        }
        
        private void serveCustomIndex(HttpServletResponse response, HttpServletRequest request) throws IOException {
            response.setContentType("text/html;charset=UTF-8");
            
            String contextPath = request.getContextPath();
            String openApiUrl = contextPath + "/api/openapi.json";
            
            String html = "<!DOCTYPE html>\n" +
                "<html lang=\"pt-BR\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>CSOnline API - Swagger UI</title>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"swagger-ui.css\" />\n" +
                "    <style>\n" +
                "        html { box-sizing: border-box; overflow: -moz-scrollbars-vertical; overflow-y: scroll; }\n" +
                "        *, *:before, *:after { box-sizing: inherit; }\n" +
                "        body { margin:0; background: #fafafa; }\n" +
                "        .swagger-ui .topbar { display: none; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"swagger-ui\"></div>\n" +
                "    <script src=\"swagger-ui-bundle.js\"></script>\n" +
                "    <script src=\"swagger-ui-standalone-preset.js\"></script>\n" +
                "    <script>\n" +
                "        console.log('Inicializando Swagger UI com URL:', '" + openApiUrl + "');\n" +
                "        window.onload = function() {\n" +
                "            try {\n" +
                "                window.ui = SwaggerUIBundle({\n" +
                "                    url: '" + openApiUrl + "',\n" +
                "                    dom_id: '#swagger-ui',\n" +
                "                    deepLinking: true,\n" +
                "                    presets: [\n" +
                "                        SwaggerUIBundle.presets.apis,\n" +
                "                        SwaggerUIStandalonePreset\n" +
                "                    ],\n" +
                "                    plugins: [\n" +
                "                        SwaggerUIBundle.plugins.DownloadUrl\n" +
                "                    ],\n" +
                "                    layout: \"StandaloneLayout\",\n" +
                "                    defaultModelsExpandDepth: 1,\n" +
                "                    defaultModelExpandDepth: 1,\n" +
                "                    docExpansion: 'list',\n" +
                "                    filter: true,\n" +
                "                    showRequestHeaders: true,\n" +
                "                    showCommonExtensions: true,\n" +
                "                    tryItOutEnabled: true,\n" +
                "                    validatorUrl: null,\n" +
                "                    onComplete: function() {\n" +
                "                        console.log('Swagger UI carregado com sucesso!');\n" +
                "                    },\n" +
                "                    onFailure: function(error) {\n" +
                "                        console.error('Erro ao carregar Swagger UI:', error);\n" +
                "                    }\n" +
                "                });\n" +
                "                console.log('Swagger UI inicializado:', window.ui);\n" +
                "            } catch(error) {\n" +
                "                console.error('Erro na inicialização:', error);\n" +
                "                document.getElementById('swagger-ui').innerHTML = '<h3>Erro ao carregar Swagger UI</h3><p>Verifique o console para mais detalhes.</p>';\n" +
                "            }\n" +
                "        };\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
            
            response.getWriter().write(html);
        }
        
        private String getContentType(String fileName) {
            if (fileName.endsWith(".css")) return "text/css";
            if (fileName.endsWith(".js")) return "application/javascript";
            if (fileName.endsWith(".png")) return "image/png";
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
            if (fileName.endsWith(".svg")) return "image/svg+xml";
            if (fileName.endsWith(".html")) return "text/html;charset=UTF-8";
            if (fileName.endsWith(".json")) return "application/json";
            return "application/octet-stream";
        }
    }
}

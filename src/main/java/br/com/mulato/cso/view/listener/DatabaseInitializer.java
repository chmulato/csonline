package br.com.mulato.cso.view.listener;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
// Removido @WebListener pois não é necessário sem JNDI ou inicialização automática
public class DatabaseInitializer implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(DatabaseInitializer.class);
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String jdbcUrl = "jdbc:h2:~/csonline;MODE=PostgreSQL;DATABASE_TO_UPPER=false";
        String username = "sa";
        String password = "";
        String resourcePath = "/resources/data-h2.sql";
        logger.debug("Iniciando DatabaseInitializer.contextInitialized()");
        logger.info("Tentando conectar ao banco H2: {}", jdbcUrl);
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            logger.info("Conexão com H2 estabelecida com sucesso.");
            try (Statement stmt = conn.createStatement()) {
                logger.debug("Statement criado com sucesso.");
                try (java.io.InputStream is = getClass().getResourceAsStream(resourcePath)) {
                    if (is == null) {
                        logger.error("Script SQL não encontrado em: {}", resourcePath);
                        throw new RuntimeException("Script SQL não encontrado em: " + resourcePath);
                    }
                    logger.info("Lendo script SQL de {}", resourcePath);
                    String sql = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    logger.debug("Script SQL lido com {} bytes.", sql.length());
                    stmt.execute(sql);
                    logger.info("Banco H2 inicializado com sucesso.");
                } catch (Exception e) {
                    logger.error("Erro ao ler ou executar o script SQL.", e);
                    throw e;
                }
            } catch (Exception e) {
                logger.error("Erro ao criar Statement ou executar SQL.", e);
                throw e;
            }
        } catch (Exception e) {
            logger.error("Erro ao conectar ou inicializar o banco H2.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nada a fazer
    }
}
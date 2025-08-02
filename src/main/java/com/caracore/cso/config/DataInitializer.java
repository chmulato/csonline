package com.caracore.cso.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Inicializador de dados para o sistema.
 * Executa o script import.sql para carregar dados iniciais quando o aplicativo inicia.
 */
@Singleton
@Startup
public class DataInitializer {
    
    private static final Logger logger = Logger.getLogger(DataInitializer.class.getName());
    
    @PersistenceContext(unitName = "csonlinePU")
    private EntityManager entityManager;
    
    /**
     * Método executado automaticamente após a injeção de dependências.
     * Verifica se existem dados no sistema e, caso não existam, executa o script de importação.
     */
    @PostConstruct
    public void init() {
        try {
            logger.info("Inicializando dados do sistema...");
            
            // Verifica se já existem dados
            Long count = (Long) entityManager.createQuery("SELECT COUNT(u) FROM com.caracore.cso.entity.User u").getSingleResult();
            
            if (count == 0) {
                logger.info("Nenhum usuário encontrado. Carregando dados iniciais...");
                executeImportScript();
                logger.info("Dados iniciais carregados com sucesso!");
            } else {
                logger.info("Dados já existem. Pulando carga inicial.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao inicializar dados: ", e);
        }
    }
    
    /**
     * Executa o script import.sql para carregar dados iniciais.
     * Lê o arquivo do classpath e executa cada comando SQL separadamente.
     */
    private void executeImportScript() {
        try {
            // Obtém a conexão JDBC diretamente do EntityManager
            Connection connection = entityManager.unwrap(Connection.class);
            
            // Lê o script SQL do classpath
            InputStream is = getClass().getClassLoader().getResourceAsStream("import.sql");
            if (is == null) {
                logger.severe("Arquivo import.sql não encontrado!");
                return;
            }
            
            String sql;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            }
            
            // Executa o script SQL
            try (Statement statement = connection.createStatement()) {
                for (String command : sql.split(";")) {
                    if (!command.trim().isEmpty()) {
                        try {
                            statement.execute(command);
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Erro ao executar comando: " + command, e);
                        }
                    }
                }
                logger.info("Script SQL executado com sucesso!");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao executar script SQL: ", e);
        }
    }
}

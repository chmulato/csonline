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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Inicializador de dados para o sistema.
 * Executa scripts SQL para configurar o banco de dados quando o aplicativo inicia.
 */
@Singleton
@Startup
public class DataInitializer {
    
    private static final Logger logger = Logger.getLogger(DataInitializer.class.getName());
    
    private static final String PERSISTENCE_UNIT_NAME = "csonlinePU";
    
    /**
     * Método executado automaticamente após a injeção de dependências.
     * Verifica se existem dados no sistema e, caso não existam, executa os scripts de inicialização.
     */
    @PostConstruct
    public void init() {
        EntityManagerFactory emf = null;
        EntityManager entityManager = null;
        
        try {
            logger.info("Inicializando dados do sistema...");
            
            // Cria o EntityManagerFactory e EntityManager manualmente
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            entityManager = emf.createEntityManager();
            
            // Verifica se já existem dados
            Long count = (Long) entityManager.createQuery("SELECT COUNT(u) FROM com.caracore.cso.entity.User u").getSingleResult();
            
            if (count == 0) {
                logger.info("Nenhum usuário encontrado. Configurando banco de dados...");
                
                // Executa o script de importação de dados
                executeImportScript(entityManager);
                logger.info("Dados iniciais carregados com sucesso!");
                
                // Executa o script de pós-esquema para ajustar constraints
                executeSchemaPostScript(entityManager);
                logger.info("Ajustes de esquema aplicados com sucesso!");
            } else {
                logger.info("Dados já existem. Pulando inicialização.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao inicializar dados: ", e);
        } finally {
            // Fecha os recursos
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
    
    /**
     * Executa o script import.sql para carregar dados iniciais.
     * Lê o arquivo do classpath e executa cada comando SQL separadamente.
     * 
     * @param entityManager EntityManager para executar os comandos SQL
     */
    private void executeImportScript(EntityManager entityManager) {
        executeScriptFromClasspath(entityManager, "import.sql", "importação de dados");
    }
    
    /**
     * Executa o script schema-post.sql para ajustar o esquema do banco de dados.
     * Lê o arquivo do classpath e executa cada comando SQL separadamente.
     * 
     * @param entityManager EntityManager para executar os comandos SQL
     */
    private void executeSchemaPostScript(EntityManager entityManager) {
        executeScriptFromClasspath(entityManager, "schema-post.sql", "ajuste de esquema");
    }
    
    /**
     * Método utilitário para executar scripts SQL do classpath.
     * 
     * @param entityManager EntityManager para executar os comandos SQL
     * @param scriptName Nome do arquivo de script no classpath
     * @param scriptType Descrição do tipo de script (para mensagens de log)
     */
    private void executeScriptFromClasspath(EntityManager entityManager, String scriptName, String scriptType) {
        try {
            // Obtém a conexão JDBC diretamente do EntityManager
            Connection connection = entityManager.unwrap(Connection.class);
            
            // Lê o script SQL do classpath
            InputStream is = getClass().getClassLoader().getResourceAsStream(scriptName);
            if (is == null) {
                logger.severe("Arquivo " + scriptName + " não encontrado!");
                return;
            }
            
            String sql;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            }
            
            // Iniciamos uma transação, pois estamos usando RESOURCE_LOCAL
            entityManager.getTransaction().begin();
            
            // Executa o script SQL
            try (Statement statement = connection.createStatement()) {
                for (String command : sql.split(";")) {
                    if (!command.trim().isEmpty()) {
                        try {
                            statement.execute(command);
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Erro ao executar comando de " + scriptType + ": " + command, e);
                        }
                    }
                }
                logger.info("Script de " + scriptType + " executado com sucesso!");
            }
            
            // Commit da transação
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // Rollback em caso de erro
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Erro ao executar script de " + scriptType + ": ", e);
        }
    }
}

package com.caracore.cso.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
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
            logger.info("========== INICIANDO CARREGAMENTO DE DADOS ==========");
            logger.info("Inicializando dados do sistema via DataInitializer...");
            
            // Cria o EntityManagerFactory e EntityManager manualmente
            logger.info("Criando EntityManagerFactory para unidade de persistência: " + PERSISTENCE_UNIT_NAME);
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            entityManager = emf.createEntityManager();
            logger.info("EntityManager criado com sucesso");
            
            // Verifica se já existem dados - primeiro verifica as tabelas
            logger.info("Verificando estrutura do banco de dados...");
            try {
                // Lista tabelas existentes para diagnóstico
                @SuppressWarnings("unchecked")
                List<String> tableNames = entityManager.createNativeQuery(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'")
                    .getResultList();
                
                logger.info("Tabelas encontradas no banco: " + tableNames.size());
                for (String tableName : tableNames) {
                    logger.info("- Tabela: " + tableName);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Não foi possível listar tabelas: " + e.getMessage());
            }
            
            // Verifica se já existem dados
            logger.info("Verificando se existem usuários no banco de dados...");
            Long count = (Long) entityManager.createQuery("SELECT COUNT(u) FROM com.caracore.cso.entity.User u").getSingleResult();
            logger.info("Contagem de usuários encontrados: " + count);
            
            if (count == 0) {
                logger.info("Nenhum usuário encontrado. Configurando banco de dados...");
                
                // Verifica se o JPA já tentou carregar o import.sql automaticamente
                logger.info("Verificando se o JPA já tentou carregar o import.sql automaticamente...");
                // Vamos forçar uma busca direta para verificar
                try {
                    @SuppressWarnings("unchecked")
                    List<String> tableContents = entityManager.createNativeQuery("SELECT * FROM app_user").getResultList();
                    logger.info("Dados encontrados na tabela APP_USER via query nativa: " + tableContents.size());
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Erro ao consultar APP_USER diretamente: " + e.getMessage());
                }
                
                // Executa o script de importação de dados
                logger.info("Iniciando execução manual do script import.sql...");
                executeImportScript(entityManager);
                logger.info("Script import.sql executado! Verificando se dados foram carregados...");
                
                // Verifica novamente a contagem
                Long newCount = (Long) entityManager.createQuery("SELECT COUNT(u) FROM com.caracore.cso.entity.User u").getSingleResult();
                logger.info("Contagem de usuários após import.sql: " + newCount);
                
                // Executa o script de pós-esquema para ajustar constraints
                logger.info("Iniciando execução do script schema-post.sql...");
                executeSchemaPostScript(entityManager);
                logger.info("Ajustes de esquema aplicados com sucesso!");
                
                // Verifica e registra os dados carregados
                verifyLoadedData(entityManager);
            } else {
                logger.info("Dados já existem no banco. Pulando inicialização...");
                summarizeExistingData(entityManager);
            }
            
            logger.info("========== FINALIZADO PROCESSAMENTO DE DADOS ==========");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro durante inicialização de dados: ", e);
        } finally {
            // Fecha recursos
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
    
    /**
     * Executa o script de ajuste de esquema (schema-post.sql).
     * 
     * @param entityManager EntityManager para executar os comandos
     */
    private void executeSchemaPostScript(EntityManager entityManager) {
        executeScriptFromClasspath(entityManager, "schema-post.sql", "ajuste de esquema");
    }
    
    /**
     * Executa o script de importação de dados (import.sql).
     * 
     * @param entityManager EntityManager para executar os comandos
     */
    private void executeImportScript(EntityManager entityManager) {
        executeScriptFromClasspath(entityManager, "import.sql", "importação de dados");
    }
    
    /**
     * Verifica e registra informações sobre os dados carregados.
     * 
     * @param entityManager EntityManager para consultar os dados
     */
    private void verifyLoadedData(EntityManager entityManager) {
        try {
            logger.info("===== VERIFICAÇÃO DE DADOS CARREGADOS =====");
            
            Long userCount = (Long) entityManager.createQuery("SELECT COUNT(u) FROM com.caracore.cso.entity.User u").getSingleResult();
            logger.info("Usuários carregados: " + userCount);
            
            Long courierCount = (Long) entityManager.createQuery("SELECT COUNT(c) FROM com.caracore.cso.entity.Courier c").getSingleResult();
            logger.info("Entregadores carregados: " + courierCount);
            
            Long customerCount = (Long) entityManager.createQuery("SELECT COUNT(c) FROM com.caracore.cso.entity.Customer c").getSingleResult();
            logger.info("Clientes carregados: " + customerCount);
            
            Long deliveryCount = (Long) entityManager.createQuery("SELECT COUNT(d) FROM com.caracore.cso.entity.Delivery d").getSingleResult();
            logger.info("Entregas carregadas: " + deliveryCount);
            
            Long teamCount = (Long) entityManager.createQuery("SELECT COUNT(t) FROM com.caracore.cso.entity.Team t").getSingleResult();
            logger.info("Equipes carregadas: " + teamCount);
            
            Long priceCount = (Long) entityManager.createQuery("SELECT COUNT(p) FROM com.caracore.cso.entity.Price p").getSingleResult();
            logger.info("Preços carregados: " + priceCount);
            
            Long smsCount = (Long) entityManager.createQuery("SELECT COUNT(s) FROM com.caracore.cso.entity.SMS s").getSingleResult();
            logger.info("SMS carregados: " + smsCount);
            
            logger.info("===== FIM DA VERIFICAÇÃO DE DADOS =====");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao verificar dados carregados: ", e);
        }
    }
    
    /**
     * Resume os dados existentes no banco quando a inicialização é pulada.
     * 
     * @param entityManager EntityManager para consultar os dados
     */
    private void summarizeExistingData(EntityManager entityManager) {
        try {
            logger.info("===== RESUMO DE DADOS EXISTENTES =====");
            
            // Lista todas as tabelas
            logger.info("Tabelas disponíveis no banco de dados:");
            try {
                // Consulta específica para HSQLDB para listar tabelas
                @SuppressWarnings("unchecked")
                List<String> tableNames = entityManager.createNativeQuery(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'")
                    .getResultList();
                
                for (String tableName : tableNames) {
                    logger.info("- Tabela: " + tableName);
                    
                    // Obter contagem de registros para cada tabela
                    Long count = (Long) entityManager.createNativeQuery(
                            "SELECT COUNT(*) FROM " + tableName)
                        .getSingleResult();
                    
                    logger.info("  Registros: " + count);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Erro ao listar tabelas: " + e.getMessage());
            }
            
            logger.info("===== FIM DO RESUMO =====");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erro ao resumir dados existentes: " + e.getMessage());
        }
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
            logger.info("Iniciando execução do script " + scriptName + " para " + scriptType);
            
            // Lê o script SQL do classpath
            InputStream is = getClass().getClassLoader().getResourceAsStream(scriptName);
            if (is == null) {
                logger.severe("Arquivo " + scriptName + " não encontrado no classpath!");
                return;
            }
            logger.info("Arquivo " + scriptName + " carregado do classpath");
            
            String sql;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
                logger.info("Conteúdo do arquivo " + scriptName + " lido com sucesso. Tamanho: " + sql.length() + " caracteres");
                
                // Para debug, vamos mostrar os primeiros 500 caracteres do arquivo
                String preview = sql.length() > 500 ? sql.substring(0, 500) + "..." : sql;
                logger.info("Prévia do conteúdo: " + preview);
            }
            
            // Executa o script SQL
            int commandCount = 0;
            int successCount = 0;
            int errorCount = 0;
            
            // Melhorado o separador para lidar melhor com delimitadores SQL
            // Isto irá melhorar a identificação correta dos comandos SQL
            String[] commands = sql.split(";\\s*[\r\n]+");
            logger.info("Executando " + commands.length + " comandos SQL do script " + scriptName);
            
            for (String command : commands) {
                String trimmedCommand = command.trim();
                if (!trimmedCommand.isEmpty() && !trimmedCommand.startsWith("--")) {
                    commandCount++;
                    
                    // Log detalhado do comando (evitando comandos muito longos)
                    String logCommand = trimmedCommand.length() > 100 ? 
                        trimmedCommand.substring(0, 100) + "..." : 
                        trimmedCommand;
                    
                    // Log mais detalhado para entender o que está sendo executado
                    if (trimmedCommand.toUpperCase().startsWith("INSERT")) {
                        try {
                            int intoIndex = trimmedCommand.toUpperCase().indexOf("INTO");
                            int bracketIndex = trimmedCommand.indexOf("(", intoIndex);
                            if (intoIndex > 0 && bracketIndex > intoIndex) {
                                String tableName = trimmedCommand.substring(intoIndex + 5, bracketIndex).trim();
                                logger.info("Comando #" + commandCount + ": INSERT na tabela: " + tableName);
                            } else {
                                logger.info("Comando #" + commandCount + ": " + logCommand);
                            }
                        } catch (Exception e) {
                            logger.info("Comando #" + commandCount + ": " + logCommand);
                        }
                    } else {
                        logger.info("Executando comando #" + commandCount + ": " + logCommand);
                    }
                    
                    // Cada comando em sua própria transação para evitar rollback completo
                    entityManager.getTransaction().begin();
                    try {
                        // Usar JPA nativo para executar o SQL diretamente através do EntityManager
                        int affected = entityManager.createNativeQuery(trimmedCommand).executeUpdate();
                        entityManager.getTransaction().commit();
                        successCount++;
                        logger.info("Comando #" + commandCount + " executado com sucesso. Linhas afetadas: " + affected);
                    } catch (Exception e) {
                        // Rollback apenas deste comando
                        if (entityManager.getTransaction().isActive()) {
                            entityManager.getTransaction().rollback();
                        }
                        errorCount++;
                        logger.log(Level.SEVERE, "Erro ao executar comando #" + commandCount + " de " + scriptType + ": " + logCommand, e);
                        // Detalhe completo da exceção para diagnóstico
                        logger.log(Level.SEVERE, "Detalhes do erro: " + e.getMessage(), e);
                        
                        // Se for um erro de SQL, vamos tentar dar mais informações
                        if (e.getMessage() != null && e.getMessage().contains("constraint")) {
                            logger.severe("Possível violação de constraint. Verifique se as tabelas referenciadas já existem " +
                                         "e se os dados respeitam as restrições de chave estrangeira.");
                        }
                    }
                }
            }
            logger.info("Execução de comandos finalizada: " + successCount + " sucessos, " + 
                   errorCount + " erros de um total de " + commandCount + " comandos");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao executar script de " + scriptType + ": ", e);
        }
    }
}

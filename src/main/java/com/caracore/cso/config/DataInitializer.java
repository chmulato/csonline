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
            
            // Verifica se já existem dados
            logger.info("Verificando se existem usuários no banco de dados...");
            Long count = (Long) entityManager.createQuery("SELECT COUNT(u) FROM com.caracore.cso.entity.User u").getSingleResult();
            logger.info("Contagem de usuários encontrados: " + count);
            
            if (count == 0) {
                logger.info("Nenhum usuário encontrado. Configurando banco de dados...");
                
                // Executa o script de importação de dados
                logger.info("Iniciando execução do script import.sql...");
                executeImportScript(entityManager);
                logger.info("Dados iniciais carregados com sucesso!");
                
                // Executa o script de pós-esquema para ajustar constraints
                logger.info("Iniciando execução do script schema-post.sql...");
                executeSchemaPostScript(entityManager);
                logger.info("Ajustes de esquema aplicados com sucesso!");
                
                // Verifica e registra os dados carregados
                verifyLoadedData(entityManager);
            } else {
                logger.info("Dados já existem. Pulando inicialização.");
                // Exibe um resumo dos dados existentes
                summarizeExistingData(entityManager);
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
            logger.info("========== FINALIZADO PROCESSAMENTO DE DADOS ==========");
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
     * Verifica e registra os dados que foram carregados no banco.
     * 
     * @param entityManager EntityManager para consultar os dados
     */
    private void verifyLoadedData(EntityManager entityManager) {
        try {
            logger.info("===== VERIFICAÇÃO DE DADOS CARREGADOS =====");
            
            // Consulta e loga a quantidade de registros em cada tabela principal
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
                logger.log(Level.SEVERE, "Erro ao listar tabelas: ", e);
            }
            
            // Consulta e loga a quantidade de registros em cada tabela principal
            Long userCount = (Long) entityManager.createQuery("SELECT COUNT(u) FROM com.caracore.cso.entity.User u").getSingleResult();
            logger.info("Usuários existentes: " + userCount);
            
            Long courierCount = (Long) entityManager.createQuery("SELECT COUNT(c) FROM com.caracore.cso.entity.Courier c").getSingleResult();
            logger.info("Entregadores existentes: " + courierCount);
            
            Long customerCount = (Long) entityManager.createQuery("SELECT COUNT(c) FROM com.caracore.cso.entity.Customer c").getSingleResult();
            logger.info("Clientes existentes: " + customerCount);
            
            Long deliveryCount = (Long) entityManager.createQuery("SELECT COUNT(d) FROM com.caracore.cso.entity.Delivery d").getSingleResult();
            logger.info("Entregas existentes: " + deliveryCount);
            
            logger.info("===== FIM DO RESUMO DE DADOS =====");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao resumir dados existentes: ", e);
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
            }
            
            // Iniciamos uma transação, pois estamos usando RESOURCE_LOCAL
            entityManager.getTransaction().begin();
            logger.info("Transação iniciada para executar o script " + scriptName);
            
            // Executa o script SQL
            int commandCount = 0;
            int successCount = 0;
            int errorCount = 0;
            
            String[] commands = sql.split(";");
            logger.info("Executando " + commands.length + " comandos SQL do script " + scriptName);
            
            for (String command : commands) {
                if (!command.trim().isEmpty()) {
                    commandCount++;
                    String trimmedCommand = command.trim();
                    String logCommand = trimmedCommand.length() > 100 ? 
                        trimmedCommand.substring(0, 100) + "..." : 
                        trimmedCommand;
                    
                    try {
                        logger.info("Executando comando #" + commandCount + ": " + logCommand);
                        // Usar JPA nativo para executar o SQL diretamente através do EntityManager
                        entityManager.createNativeQuery(trimmedCommand).executeUpdate();
                        successCount++;
                    } catch (Exception e) {
                        errorCount++;
                        logger.log(Level.SEVERE, "Erro ao executar comando #" + commandCount + " de " + scriptType + ": " + logCommand, e);
                    }
                }
            }
            logger.info("Execução de comandos finalizada: " + successCount + " sucessos, " + 
                   errorCount + " erros de um total de " + commandCount + " comandos");
            
            // Commit da transação
            entityManager.getTransaction().commit();
            logger.info("Transação commitada com sucesso para o script " + scriptName);
            
        } catch (Exception e) {
            // Rollback em caso de erro
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
                logger.info("Transação revertida devido a erro");
            }
            logger.log(Level.SEVERE, "Erro ao executar script de " + scriptType + ": ", e);
        }
    }
}

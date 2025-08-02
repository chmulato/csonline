package com.caracore.cso.config;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe responsável por inicializar o Flyway e executar as migrações
 * durante a inicialização da aplicação.
 */
@Singleton
@Startup
public class FlywayConfig {

    private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);
    
    @Inject
    private DataSource dataSource;
    
    @Inject
    private AppProperties appProperties;
    
    /**
     * Inicializa o Flyway e executa as migrações na inicialização da aplicação.
     * As migrações são localizadas em src/main/resources/db/migration
     */
    @PostConstruct
    public void initFlyway() {
        // Verifica se o Flyway está habilitado
        if (!appProperties.getBooleanProperty("flyway.enabled")) {
            logger.info("Flyway está desabilitado nas configurações. Migrações não serão executadas.");
            return;
        }
        
        try {
            logger.info("Iniciando migrações do Flyway...");
            
            // Configura o Flyway com o datasource da aplicação
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .baselineOnMigrate(appProperties.getBooleanProperty("flyway.baseline-on-migrate"))
                    .baselineVersion(appProperties.getProperty("flyway.baseline-version", "0"))
                    .locations(appProperties.getProperty("flyway.locations", "classpath:db/migration"))
                    .validateOnMigrate(appProperties.getBooleanProperty("flyway.validate-on-migrate"))
                    .load();
            
            // Executa as migrações
            flyway.migrate();
            
            logger.info("Migrações do Flyway concluídas com sucesso.");
        } catch (Exception e) {
            logger.error("Erro ao executar migrações do Flyway", e);
            throw new RuntimeException("Falha na inicialização do banco de dados", e);
        }
    }
}

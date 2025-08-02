package com.caracore.cso.config;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import javax.sql.DataSource;

/**
 * Classe responsável por produzir o DataSource para injeção em outras classes.
 * Isso permite que o Flyway e outros componentes utilizem o DataSource configurado no servidor.
 */
@ApplicationScoped
public class DataSourceProducer {

    // O DataSource definido na configuração do WildFly/servidor
    @Resource(lookup = "java:/HSQLDBDatasource")
    private DataSource dataSource;
    
    /**
     * Produz o DataSource para injeção.
     * 
     * @return O DataSource configurado
     */
    @Produces
    public DataSource getDataSource() {
        return dataSource;
    }
}

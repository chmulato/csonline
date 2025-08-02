package com.caracore.cso.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe responsável por carregar as propriedades do arquivo application.properties.
 * Disponibiliza as propriedades para uso em outras classes da aplicação.
 */
@ApplicationScoped
public class AppProperties {

    private static final Logger logger = LoggerFactory.getLogger(AppProperties.class);
    private Properties properties = new Properties();
    
    /**
     * Inicializa e carrega as propriedades do arquivo application.properties.
     */
    @PostConstruct
    public void init() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.error("Não foi possível encontrar o arquivo application.properties");
                return;
            }
            
            properties.load(input);
            logger.info("Arquivo application.properties carregado com sucesso");
        } catch (IOException e) {
            logger.error("Erro ao carregar o arquivo application.properties", e);
        }
    }
    
    /**
     * Obtém uma propriedade pelo nome.
     * 
     * @param key Nome da propriedade
     * @return Valor da propriedade ou null se não existir
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Obtém uma propriedade pelo nome, retornando um valor padrão se não existir.
     * 
     * @param key Nome da propriedade
     * @param defaultValue Valor padrão caso a propriedade não exista
     * @return Valor da propriedade ou o valor padrão
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Verifica se uma propriedade existe.
     * 
     * @param key Nome da propriedade
     * @return true se a propriedade existir, false caso contrário
     */
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }
    
    /**
     * Obtém uma propriedade booleana pelo nome.
     * 
     * @param key Nome da propriedade
     * @return true se o valor for "true", false caso contrário
     */
    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
    
    /**
     * Obtém uma propriedade inteira pelo nome.
     * 
     * @param key Nome da propriedade
     * @return Valor inteiro da propriedade ou 0 se não for um número válido
     */
    public int getIntProperty(String key) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

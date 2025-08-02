package com.caracore.cso.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caracore.cso.config.AppProperties;

/**
 * Classe utilitária para executar queries SQL diretamente no banco de dados.
 * Usa as propriedades de conexão definidas no application.properties.
 */
@ApplicationScoped
public class SqlExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SqlExecutor.class);

    @Inject
    private AppProperties appProperties;

    /**
     * Executa uma query SQL que não retorna resultados (INSERT, UPDATE, DELETE, etc).
     *
     * @param sql O SQL a ser executado
     * @return O número de linhas afetadas
     * @throws SQLException Se ocorrer um erro na execução
     */
    public int executeUpdate(String sql) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            logger.debug("Executando SQL: {}", sql);
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error("Erro ao executar SQL: {}", sql, e);
            throw e;
        }
    }

    /**
     * Executa uma query SQL que retorna resultados (SELECT).
     *
     * @param sql O SQL a ser executado
     * @param resultHandler O handler para processar o ResultSet
     * @throws SQLException Se ocorrer um erro na execução
     */
    public <T> T executeQuery(String sql, ResultSetHandler<T> resultHandler) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            logger.debug("Executando query: {}", sql);
            return resultHandler.handle(rs);
        } catch (SQLException e) {
            logger.error("Erro ao executar query: {}", sql, e);
            throw e;
        }
    }

    /**
     * Executa uma query SQL parametrizada que não retorna resultados.
     *
     * @param sql O SQL a ser executado
     * @param params Os parâmetros para a query
     * @return O número de linhas afetadas
     * @throws SQLException Se ocorrer um erro na execução
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            setParameters(pstmt, params);
            logger.debug("Executando SQL parametrizado: {}", sql);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erro ao executar SQL parametrizado: {}", sql, e);
            throw e;
        }
    }

    /**
     * Executa uma query SQL parametrizada que retorna resultados.
     *
     * @param sql O SQL a ser executado
     * @param resultHandler O handler para processar o ResultSet
     * @param params Os parâmetros para a query
     * @throws SQLException Se ocorrer um erro na execução
     */
    public <T> T executeQuery(String sql, ResultSetHandler<T> resultHandler, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            setParameters(pstmt, params);
            logger.debug("Executando query parametrizada: {}", sql);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return resultHandler.handle(rs);
            }
        } catch (SQLException e) {
            logger.error("Erro ao executar query parametrizada: {}", sql, e);
            throw e;
        }
    }

    /**
     * Obtém uma conexão com o banco de dados usando as propriedades de conexão.
     *
     * @return Uma conexão com o banco de dados
     * @throws SQLException Se ocorrer um erro ao obter a conexão
     */
    private Connection getConnection() throws SQLException {
        String url = appProperties.getProperty("db.url");
        String username = appProperties.getProperty("db.username");
        String password = appProperties.getProperty("db.password");
        
        logger.debug("Conectando ao banco de dados: {}", url);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Define os parâmetros para um PreparedStatement.
     *
     * @param pstmt O PreparedStatement
     * @param params Os parâmetros
     * @throws SQLException Se ocorrer um erro ao definir os parâmetros
     */
    private void setParameters(PreparedStatement pstmt, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    /**
     * Interface para manipular o ResultSet de uma query.
     */
    public interface ResultSetHandler<T> {
        /**
         * Manipula o ResultSet e retorna um resultado.
         *
         * @param rs O ResultSet a ser manipulado
         * @return O resultado da manipulação
         * @throws SQLException Se ocorrer um erro na manipulação
         */
        T handle(ResultSet rs) throws SQLException;
    }
}

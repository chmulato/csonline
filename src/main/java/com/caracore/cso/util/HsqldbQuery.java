package com.caracore.cso.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utilitário para executar consultas SQL no banco de dados HSQLDB.
 */
public class HsqldbQuery {
    
    private static final String DB_URL = "jdbc:hsqldb:hsql://localhost:9001/test";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java com.caracore.cso.util.HsqldbQuery \"consulta SQL\"");
            return;
        }
        
        String sql = args[0];
        
        try {
            // Carrega o driver JDBC
            Class.forName("org.hsqldb.jdbcDriver");
            
            // Conecta ao banco de dados
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                boolean isResultSet = stmt.execute(sql);
                
                if (isResultSet) {
                    // Se for uma consulta SELECT
                    try (ResultSet rs = stmt.getResultSet()) {
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        
                        // Imprime os nomes das colunas
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(metaData.getColumnName(i));
                            if (i < columnCount) {
                                System.out.print("\t");
                            }
                        }
                        System.out.println();
                        
                        // Imprime uma linha separadora
                        for (int i = 1; i <= columnCount; i++) {
                            for (int j = 0; j < metaData.getColumnName(i).length(); j++) {
                                System.out.print("-");
                            }
                            if (i < columnCount) {
                                System.out.print("\t");
                            }
                        }
                        System.out.println();
                        
                        // Imprime os dados
                        while (rs.next()) {
                            for (int i = 1; i <= columnCount; i++) {
                                System.out.print(rs.getString(i));
                                if (i < columnCount) {
                                    System.out.print("\t");
                                }
                            }
                            System.out.println();
                        }
                    }
                } else {
                    // Se for um comando INSERT, UPDATE, DELETE, etc.
                    int updateCount = stmt.getUpdateCount();
                    System.out.println(updateCount + " linha(s) afetada(s).");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar o driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
        }
    }
}

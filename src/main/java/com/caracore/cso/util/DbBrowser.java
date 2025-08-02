package com.caracore.cso.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Utilitário para explorar o banco de dados HSQLDB em memória.
 * Esta classe permite executar consultas SQL e visualizar a estrutura das tabelas.
 * 
 * Uso: Execute esta classe como aplicação Java standalone enquanto o servidor estiver rodando.
 */
public class DbBrowser {
    
    private static final String JDBC_URL = "jdbc:hsqldb:mem:testdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    
    public static void main(String[] args) {
        try {
            // Registra o driver JDBC para HSQLDB 2.7.2
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            
            // Estabelece conexão com o banco
            try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
                System.out.println("Conectado ao banco de dados HSQLDB em memória.");
                System.out.println("Observação: O banco deve estar em execução no servidor para funcionar.");
                
                // Menu interativo
                Scanner scanner = new Scanner(System.in);
                boolean exit = false;
                
                while (!exit) {
                    System.out.println("\n===== HSQLDB Browser =====");
                    System.out.println("1. Listar todas as tabelas");
                    System.out.println("2. Descrever estrutura de uma tabela");
                    System.out.println("3. Executar consulta SQL personalizada");
                    System.out.println("4. Sair");
                    System.out.print("Escolha uma opção: ");
                    
                    String option = scanner.nextLine();
                    
                    switch (option) {
                        case "1":
                            listTables(conn);
                            break;
                        case "2":
                            System.out.print("Nome da tabela: ");
                            String tableName = scanner.nextLine();
                            describeTable(conn, tableName);
                            break;
                        case "3":
                            System.out.print("Digite sua consulta SQL: ");
                            String sql = scanner.nextLine();
                            executeQuery(conn, sql);
                            break;
                        case "4":
                            exit = true;
                            break;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                    }
                }
                
                scanner.close();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro de SQL: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lista todas as tabelas do banco de dados.
     */
    private static void listTables(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs = metaData.getTables(null, "PUBLIC", null, new String[] {"TABLE"});
        
        System.out.println("\n=== Tabelas no banco de dados ===");
        int count = 0;
        
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            System.out.println(tableName);
            count++;
        }
        
        if (count == 0) {
            System.out.println("Nenhuma tabela encontrada.");
        } else {
            System.out.println("Total: " + count + " tabelas");
        }
        
        rs.close();
    }
    
    /**
     * Descreve a estrutura de uma tabela.
     */
    private static void describeTable(Connection conn, String tableName) throws SQLException {
        // Verifica se a tabela existe
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tables = metaData.getTables(null, "PUBLIC", tableName.toUpperCase(), null);
        
        if (!tables.next()) {
            System.out.println("Tabela '" + tableName + "' não encontrada.");
            tables.close();
            return;
        }
        tables.close();
        
        // Obtém informações das colunas
        ResultSet columns = metaData.getColumns(null, "PUBLIC", tableName.toUpperCase(), null);
        
        System.out.println("\n=== Estrutura da tabela " + tableName.toUpperCase() + " ===");
        System.out.printf("%-20s %-15s %-10s %-10s\n", "COLUNA", "TIPO", "TAMANHO", "NULLABLE");
        System.out.println("------------------------------------------------------------");
        
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String typeName = columns.getString("TYPE_NAME");
            int columnSize = columns.getInt("COLUMN_SIZE");
            String nullable = columns.getInt("NULLABLE") == 1 ? "Sim" : "Não";
            
            System.out.printf("%-20s %-15s %-10d %-10s\n", columnName, typeName, columnSize, nullable);
        }
        
        columns.close();
        
        // Mostra primary keys
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, "PUBLIC", tableName.toUpperCase());
        System.out.println("\nChaves primárias:");
        boolean hasPk = false;
        
        while (primaryKeys.next()) {
            hasPk = true;
            String pkName = primaryKeys.getString("PK_NAME");
            String columnName = primaryKeys.getString("COLUMN_NAME");
            System.out.println("  " + columnName + " (" + pkName + ")");
        }
        
        if (!hasPk) {
            System.out.println("  Nenhuma chave primária definida");
        }
        
        primaryKeys.close();
        
        // Mostra foreign keys
        ResultSet foreignKeys = metaData.getImportedKeys(null, "PUBLIC", tableName.toUpperCase());
        System.out.println("\nChaves estrangeiras:");
        boolean hasFk = false;
        
        while (foreignKeys.next()) {
            hasFk = true;
            String fkName = foreignKeys.getString("FK_NAME");
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
            
            System.out.println("  " + fkColumnName + " -> " + pkTableName + "." + pkColumnName + " (" + fkName + ")");
        }
        
        if (!hasFk) {
            System.out.println("  Nenhuma chave estrangeira definida");
        }
        
        foreignKeys.close();
        
        // Mostra amostra dos dados
        try (Statement stmt = conn.createStatement()) {
            ResultSet data = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 5");
            ResultSetMetaData rsMetaData = data.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            
            System.out.println("\nAmostra de dados (até 5 registros):");
            
            // Cabeçalho
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rsMetaData.getColumnName(i) + "\t");
            }
            System.out.println();
            
            // Dados
            boolean hasData = false;
            while (data.next()) {
                hasData = true;
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(data.getString(i) + "\t");
                }
                System.out.println();
            }
            
            if (!hasData) {
                System.out.println("  Nenhum dado encontrado");
            }
            
            // Contagem total
            ResultSet count = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
            if (count.next()) {
                System.out.println("\nTotal de registros: " + count.getLong(1));
            }
        }
    }
    
    /**
     * Executa uma consulta SQL personalizada.
     */
    private static void executeQuery(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            boolean isQuery = stmt.execute(sql);
            
            if (isQuery) {
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                // Cabeçalho
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(metaData.getColumnName(i) + "\t");
                }
                System.out.println();
                
                // Separador
                for (int i = 1; i <= columnCount; i++) {
                    for (int j = 0; j < metaData.getColumnName(i).length(); j++) {
                        System.out.print("-");
                    }
                    System.out.print("\t");
                }
                System.out.println();
                
                // Dados
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }
                
                System.out.println("\nTotal: " + rowCount + " registro(s)");
            } else {
                int updateCount = stmt.getUpdateCount();
                System.out.println("Comando executado. " + updateCount + " registro(s) afetado(s).");
            }
        }
    }
}

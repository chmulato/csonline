package br.com.mulato.cso.listener;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String jdbcUrl = "jdbc:h2:~/csonline;MODE=PostgreSQL;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";
        String username = "sa";
        String password = "";
        String scriptPath = "c:/dev/csonline/doc/data-h2.sql";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
                Statement stmt = conn.createStatement()) {
            String sql = new String(Files.readAllBytes(Paths.get(scriptPath)));
            stmt.execute(sql);
            System.out.println("Banco H2 inicializado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nada a fazer
    }
}
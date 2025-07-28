
package com.caracore.cso.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaPostUtil {
    private static final Logger logger = LoggerFactory.getLogger(SchemaPostUtil.class);

    public static void applySchemaPost() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:csonline", "sa", "")) {
            String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/schema-post.sql")));
            for (String stmt : sql.split(";")) {
                if (!stmt.trim().isEmpty()) {
                    conn.createStatement().execute(stmt);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao aplicar schema-post.sql", e);
            throw e;
        }
    }
}

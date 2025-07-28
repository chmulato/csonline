package com.caracore.cso.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

public class SchemaPostUtil {
    public static void applySchemaPost() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:csonline", "sa", "")) {
            String sql = new String(Files.readAllBytes(Paths.get("src/main/resources/schema-post.sql")));
            for (String stmt : sql.split(";")) {
                if (!stmt.trim().isEmpty()) {
                    conn.createStatement().execute(stmt);
                }
            }
        }
    }
}

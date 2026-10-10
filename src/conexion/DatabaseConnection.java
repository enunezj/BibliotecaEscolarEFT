package conexion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private static final DatabaseConnection INSTANCE =
            new DatabaseConnection();

    private final Properties configuration = new Properties();

    private DatabaseConnection() {
        Path path = Path.of("db.properties");

        try (InputStream input = Files.newInputStream(path)) {
            configuration.load(input);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo cargar db.properties", e
            );
        }
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                configuration.getProperty("db.url"),
                configuration.getProperty("db.user"),
                configuration.getProperty("db.password")
        );
    }
}

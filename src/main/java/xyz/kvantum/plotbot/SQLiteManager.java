package xyz.kvantum.plotbot;

import lombok.Getter;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.PolyJDBCBuilder;
import org.polyjdbc.core.dialect.DialectRegistry;
import org.polyjdbc.core.schema.SchemaInspector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SQLiteManager implements AutoCloseable {

    private static final String PATH = "plotbot.db";

    @Getter private Connection connection;
    @Getter private final PolyJDBC polyJDBC;

    public SQLiteManager() {
        PolyJDBC polyJDBC = null;
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + PATH);
            polyJDBC = PolyJDBCBuilder.polyJDBC(DialectRegistry.MYSQL.getDialect())
                .usingManagedConnections(this::getConnection).build();
            try (final SchemaInspector inspector = polyJDBC.schemaInspector()) {
                if (!inspector.relationExists("history")) {
                    connection.createStatement().executeUpdate(
                        "CREATE TABLE history (\n" + "entry_id INTEGER,\n" + "timestamp BIGINT,\n"
                            + "user VARCHAR(64),\n" + "message VARCHAR(2043), channel VARCHAR(64),\n"
                            + "CONSTRAINT history_pk PRIMARY KEY(entry_id)\n" + ")"
                    );
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        this.polyJDBC = polyJDBC;
    }

    @Override public void close() throws Exception {
        if (this.connection != null) {
            this.connection.close();
        }
    }
}

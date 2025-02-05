package org.bernardo.o6ssuporte.database;

import org.bernardo.o6ssuporte.O6SSuporte;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:" + O6SSuporte.getInstance().getDataFolder() + File.separator + "database.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
        return connection;
    }
}

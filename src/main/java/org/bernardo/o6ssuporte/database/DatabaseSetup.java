package org.bernardo.o6ssuporte.database;

import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void initializeDatabase() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            String createTableTicketsPlayersSQL = "CREATE TABLE IF NOT EXISTS tickets_players ("
                    + "id INTEGER UNIQUE PRIMARY KEY,"
                    + "uuid TEXT,"
                    + "data TEXT,"
                    + "horario TEXT,"
                    + "duvida TEXT,"
                    + "mensagens TEXT,"
                    + "identificador INTEGER DEFAULT 1,"
                    + "status TEXT DEFAULT 'Aberto'"
                    + ");";
            statement.executeUpdate(createTableTicketsPlayersSQL);

            String createTableAvaliacoesSQL = "CREATE TABLE IF NOT EXISTS avaliacoes_players ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "uuid TEXT,"
                    + "nota INTEGER,"
                    + "avaliacao TEXT,"
                    + "staff_uuid TEXT"
                    + ");";
            statement.executeUpdate(createTableAvaliacoesSQL);

            String createTableStaffsSQL = "CREATE TABLE IF NOT EXISTS staffs ("
                    + "uuid TEXT UNIQUE,"
                    + "cargo TEXT"
                    + ");";
            statement.executeUpdate(createTableStaffsSQL);

            System.out.println(ChatColor.GREEN + "Banco de dados criado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar a estrutura do banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

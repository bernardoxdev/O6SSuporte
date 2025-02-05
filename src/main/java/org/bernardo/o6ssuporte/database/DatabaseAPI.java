package org.bernardo.o6ssuporte.database;

import com.mysql.jdbc.SQLError;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseAPI {

    public int addTicket(Player player, String horario, String data, String duvida) {
        int ticketId = -1;
        String sql = "INSERT INTO tickets_players (uuid, data, horario, duvida, mensagens) VALUES (?, ?, ?, ?, ?);";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, data);
            stmt.setString(3, horario);
            stmt.setString(4, duvida);

            String mensagemInicial = "1: " + player.getUniqueId().toString() + " - " + duvida;
            stmt.setString(5, mensagemInicial);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticketId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticketId;
    }

    public void addMensagemTicket(String uuid, Player player, boolean staff, String mensagem) {
        String sql = "UPDATE tickets_players SET mensagens = ? WHERE uuid = ?;";
        String sql1 = "SELECT mensagens, identificador FROM tickets_players WHERE uuid = ?;";

        String mensagens = null;
        int identificador = 1;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql1)) {
            stmt.setString(1, uuid);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                mensagens = rs.getString("mensagens");
                identificador = rs.getInt("identificador") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mensagens = (mensagens == null || mensagens.isEmpty()) ? player.getUniqueId().toString() + " - " + mensagem :
                mensagens + ", " + identificador + ": " + player.getUniqueId().toString() + " - " + mensagem;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, mensagens);
            stmt.setString(2, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int id = getIdTicketByUUID(uuid);

        if (id != -1) {
            if (staff) {
                respondidoTicket(id);
            } else {
                abertoTicket(id);
            }
        }
    }

    public void abertoTicket(int id) {
        String sql = "UPDATE tickets_players SET status = 'Aberto' WHERE id =?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void respondidoTicket(int id) {
        String sql = "UPDATE tickets_players SET status = 'Respondido' WHERE id =?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getStatusTicket(int id) {
        String sql = "SELECT status FROM tickets_players WHERE id =?";

        String status = "";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public String closeTicket(int id) {
        String sql = "UPDATE tickets_players SET status = 'Fechado' WHERE id =?";
        String sql2 = "SELECT uuid FROM tickets_players WHERE id = ?";

        String uuid = "";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql2)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                uuid = rs.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uuid;
    }

    private String formatTicket(ResultSet rs) throws SQLException {
        return "ID: " + rs.getInt("id") +
                ", UUID: " + rs.getString("uuid") +
                ", Data: " + rs.getString("data") +
                ", Horario: " + rs.getString("horario") +
                ", Duvida: " + rs.getString("duvida") +
                ", Status: " + rs.getString("status");
    }

    private Map<String, String> formatTicketInfos(ResultSet rs) throws SQLException {
        Map<String, String> result = new HashMap<>();

        result.put("ID", Integer.toString(rs.getInt("ID")));
        result.put("UUID", rs.getString("UUID"));
        result.put("Data", rs.getString("Data"));
        result.put("Horario", rs.getString("Horario"));
        result.put("Duvida", rs.getString("Duvida"));
        result.put("Mensagens", rs.getString("Mensagens"));
        result.put("Status", rs.getString("Status"));

        return result;
    }

    public boolean ticketExists(int id) {
        String sql = "SELECT COUNT(*) FROM tickets_players WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getUuidTicketById(int id) {
        String sql = "SELECT uuid FROM tickets_players WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("uuid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getIdTicketByUUID(String uuid) {
        String sql = "SELECT id FROM tickets_players WHERE uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<String> getTicketsAll() {
        List<String> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets_players ORDER BY id ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tickets.add(formatTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public List<Map<String, String>> getTicketsAllMaps() {
        List<Map<String, String>> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets_players ORDER BY id ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tickets.add(formatTicketInfos(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public List<String> getTicketsByPlayer(Player player) {
        List<String> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets_players WHERE uuid = ? ORDER BY id ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(formatTicket(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public List<Map<String, String>> getTicketsByPlayerMap(Player player) {
        List<Map<String, String>> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets_players WHERE uuid = ? ORDER BY id ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(formatTicketInfos(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public Map<String, String> getTicketByIDMap(int ID) {
        String sql = "SELECT * FROM tickets_players WHERE id = ? ORDER BY id ASC";
        Map<String, String> mapTicket = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    mapTicket = formatTicketInfos(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapTicket;
    }

    public List<String> getTicketByStatus(String status) {
        List<String> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets_players WHERE status = ? ORDER BY id ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(formatTicket(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public List<Map<String, String>> getTicketByStatusMaps(String status) {
        List<Map<String, String>> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets_players WHERE status = ? ORDER BY id ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(formatTicketInfos(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public List<Map<String, String>> getTicketByStatusPlayerMap(Player player, String status) {
        List<Map<String, String>> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets_players WHERE status = ? AND uuid = ? ORDER BY id ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, player.getUniqueId().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(formatTicketInfos(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public Map<String, String> getTicketInfo(int id) {
        String sql = "SELECT * FROM tickets_players WHERE id = ? ORDER BY id ASC";
        Map<String, String> ticketInfo = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ticketInfo = formatTicketInfos(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticketInfo.isEmpty() ? null : ticketInfo;
    }

    public void addStaff(String uuid, String cargo) {
        String sql = "INSERT INTO staffs (uuid, cargo) VALUES (?, ?);";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.setString(2, cargo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean containsStaff(String uuid) {
        String sql = "SELECT COUNT(*) FROM staffs WHERE uuid = ?;";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count > 0;
    }

    public int getCountPlayerTickets(String uuid) {
        String sql = "SELECT COUNT(*) FROM tickets_players WHERE uuid = ?;";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int getCountPlayerTicketsByFiltro(String uuid, String filtro) {
        String sql = "SELECT COUNT(*) FROM tickets_players WHERE uuid = ? AND status = ?;";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.setString(2, filtro);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public void removeStaff(String uuid) {
        String sql = "DELETE FROM staffs WHERE uuid = ?;";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getAllStaffs() {
        Map<String, String> staffs = new HashMap<>();
        String sql = "SELECT * FROM staffs;";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                staffs.put(rs.getString("uuid"), rs.getString("cargo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffs;
    }

    public List<String> getStaffsUUIDs() {
        List<String> staffsUUIDs = new ArrayList<>();
        String sql = "SELECT uuid FROM staffs;";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                staffsUUIDs.add(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffsUUIDs;
    }

    public void updateCargoStaff(String uuid, String cargo) {
        String sql = "UPDATE staffs SET cargo = ? WHERE uuid = ?;";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cargo);
            stmt.setString(2, uuid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> getAllStaffsAndTags() {
        List<Map<String, String>> staffs = new ArrayList<>();
        String sql = "SELECT * FROM staffs;";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                staffs.add(formatStaffInfos(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffs;
    }

    public int getCountAvaliacoesPlayer(String uuid) {
        String sql = "SELECT COUNT(*) FROM avaliacoes_players WHERE uuid = ?;";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int getCountAvaliacoesStaff(String uuid) {
        String sql = "SELECT COUNT(*) FROM avaliacoes_players WHERE staff_uuid = ?;";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public float getNotaMediaStaff(String uuid) {
        String sql = "SELECT nota FROM avaliacoes_players WHERE staff_uuid = ?;";
        int contador = 0;
        int soma = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            stmt.setString(1, uuid);

            while (rs.next()) {
                contador++;
                soma += rs.getInt("nota");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (float) soma / contador;
    }

    public List<Map<String, String>> getAllAvaliacoes() {
        String sql = "SELECT * FROM avaliacoes_players;";

        List<Map<String, String>> avaliacoes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                avaliacoes.add(formatAvaliacoesInfo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return avaliacoes;
    }

    public List<Map<String, String>> getAllAvaliacoesByPlayer(String uuid) {
        String sql = "SELECT * FROM avaliacoes_players WHERE uuid = ?;";

        List<Map<String, String>> avaliacoes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            stmt.setString(1,uuid);

            while (rs.next()) {
                avaliacoes.add(formatAvaliacoesInfo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return avaliacoes;
    }

    public void addAvalicao(String uuidPlayer, String uuidStaff, int nota, String comentario) {
        String sql = "INSERT INTO avaliacoes_players (uuid, nota, avaliacao, staff_uuid) VALUES (?, ?, ?, ?);";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuidPlayer);
            stmt.setInt(2, nota);
            stmt.setString(3, comentario);
            stmt.setString(4, uuidStaff);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> formatAvaliacoesInfo(ResultSet rs) throws SQLException {
        Map<String, String> avaliacaoInfo = new HashMap<>();

        avaliacaoInfo.put("id", String.valueOf(rs.getInt("id")));
        avaliacaoInfo.put("uuid", rs.getString("uuid"));
        avaliacaoInfo.put("nota", rs.getString("nota"));
        avaliacaoInfo.put("avaliacao", rs.getString("avaliacao"));
        avaliacaoInfo.put("staff_uuid", rs.getString("staff_uuid"));

        return avaliacaoInfo;
    }

    private Map<String, String> formatStaffInfos(ResultSet rs) throws SQLException {
        Map<String, String> staffInfo = new HashMap<>();

        staffInfo.put("uuid", rs.getString("uuid"));
        staffInfo.put("cargo", rs.getString("cargo"));

        return staffInfo;
    }

}

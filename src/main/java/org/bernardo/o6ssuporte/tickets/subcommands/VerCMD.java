package org.bernardo.o6ssuporte.tickets.subcommands;

import org.bernardo.o6ssuporte.APIs.SubCommand;
import org.bernardo.o6ssuporte.database.DatabaseAPI;
import org.bernardo.o6ssuporte.tickets.templates.TicketsTemplate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class VerCMD extends SubCommand {

    private final String PERMISSAO = "o6ssuporte.admin";

    private final TicketsTemplate ticketsTemplate = new TicketsTemplate();
    private final DatabaseAPI databaseAPI = new DatabaseAPI();

    @Override
    public String getName() {
        return "ver";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/tickets ver (id)";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission(PERMISSAO)) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para utilizar este comando!");
            return;
        }

        String idString = args[1];
        int id;

        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "ID inválido.");
            return;
        }

        if (!databaseAPI.ticketExists(id)) {
            player.sendMessage(ChatColor.RED + "Ticket não encontrado.");
            return;
        }

        ticketsTemplate.abrirMenuTicketInfo(player,1,id);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}

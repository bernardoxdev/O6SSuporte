package org.bernardo.o6ssuporte.tickets.subcommands;

import org.bernardo.o6ssuporte.APIs.SubCommand;
import org.bernardo.o6ssuporte.tickets.APIs.TicketsAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class FecharCMD extends SubCommand {

    private final String PERMISSAO = "o6ssuporte.bypass";

    private final TicketsAPI ticketsAPI = new TicketsAPI();

    @Override
    public String getName() {
        return "fechar";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/tickets fechar (id)";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission(PERMISSAO)) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para fechar esse ticket.");
            return;
        }

        if (args.length != 2) {
            player.sendMessage("Utilize o comando corretamente! /tickets fechar (id)");
            return;
        }

        String idString = args[1];
        int id;

        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "O ID do ticket deve ser um número.");
            return;
        }

        ticketsAPI.fecharTicketStaff(id, player);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}
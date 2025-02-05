package org.bernardo.o6ssuporte.staff.subcommands;

import org.bernardo.o6ssuporte.APIs.SubCommand;
import org.bernardo.o6ssuporte.database.DatabaseAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoverCMD extends SubCommand {

    private final DatabaseAPI databaseAPI = new DatabaseAPI();

    @Override
    public String getName() {
        return "remover";
    }

    @Override
    public String getDescription() {
        return "Comando para remover um player da staff";
    }

    @Override
    public String getSyntax() {
        return "/suportestaff remover (player)";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage("Utilize o comando corretamente! /suportestaff remover (player)");
            return;
        }

        String target = args[1];

        if (target == null) {
            player.sendMessage("Player não encontrado.");
            return;
        }

        if (!databaseAPI.getStaffsUUIDs().contains(target)) {
            player.sendMessage(ChatColor.RED + "Este player não é um staff!");
            return;
        }

        if (!databaseAPI.containsStaff(target)) {
            player.sendMessage(ChatColor.RED + "Este player não faz parte da staff!");
            return;
        }

        databaseAPI.removeStaff(target);
        player.sendMessage(ChatColor.GREEN + "Player removido da staff.");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}

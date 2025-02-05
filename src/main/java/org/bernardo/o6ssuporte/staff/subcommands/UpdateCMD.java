package org.bernardo.o6ssuporte.staff.subcommands;

import org.bernardo.o6ssuporte.APIs.SubCommand;
import org.bernardo.o6ssuporte.database.DatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class UpdateCMD extends SubCommand {

    private final DatabaseAPI databaseAPI = new DatabaseAPI();

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "Atualiza o cargo de um staff";
    }

    @Override
    public String getSyntax() {
        return "/suportestaff update (player) (cargo)";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage("Utilize o comando corretamente! /suportestaff update (player) (cargo)");
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

        String cargo = args[2];

        databaseAPI.updateCargoStaff(target,cargo);
        player.sendMessage(ChatColor.GREEN + "Cargo do staff " + Bukkit.getOfflinePlayer(target).getName() + " atualizado para " + cargo + ".");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}

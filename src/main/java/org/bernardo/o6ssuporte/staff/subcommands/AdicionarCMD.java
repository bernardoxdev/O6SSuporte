package org.bernardo.o6ssuporte.staff.subcommands;

import org.bernardo.o6ssuporte.APIs.SubCommand;
import org.bernardo.o6ssuporte.database.DatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class AdicionarCMD extends SubCommand {

    private final DatabaseAPI databaseAPI = new DatabaseAPI();

    @Override
    public String getName() {
        return "adicionar";
    }

    @Override
    public String getDescription() {
        return "Comando para adicionar um novo player a staff";
    }

    @Override
    public String getSyntax() {
        return "/suportestaff adicionar (player) (cargo)";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage("Utilize o comando corretamente! /suportestaff adicionar (player) (cargo)");
            return;
        }

        String target = args[1];

        if (target == null) {
            player.sendMessage("Player não encontrado.");
            return;
        }

        if (!Bukkit.getPlayer(target).isValid()) {
            player.sendMessage(ChatColor.RED + "O player não é válido.");
            return;
        }

        String cargo = args[2];

        if (cargo == null) {
            player.sendMessage("Cargo não informado.");
            return;
        }

        String uuid = Bukkit.getPlayer(target).getUniqueId().toString();

        if (databaseAPI.containsStaff(uuid)) {
            player.sendMessage(ChatColor.RED + "Ja existe algum staff com esse nome.");
            return;
        }

        databaseAPI.addStaff(uuid, cargo);

        player.sendMessage(ChatColor.GREEN + "Player adicionado a staff.");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}

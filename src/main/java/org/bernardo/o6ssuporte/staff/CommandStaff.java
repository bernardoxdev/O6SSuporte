package org.bernardo.o6ssuporte.staff;

import org.bernardo.o6ssuporte.APIs.SubCommand;
import org.bernardo.o6ssuporte.staff.subcommands.AdicionarCMD;
import org.bernardo.o6ssuporte.staff.subcommands.ListarCMD;
import org.bernardo.o6ssuporte.staff.subcommands.RemoverCMD;
import org.bernardo.o6ssuporte.staff.subcommands.UpdateCMD;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandStaff implements CommandExecutor {

    private final List<SubCommand> subCommands = new ArrayList<>();

    public CommandStaff() {
        subCommands.add(new AdicionarCMD());
        subCommands.add(new RemoverCMD());
        subCommands.add(new ListarCMD());
        subCommands.add(new UpdateCMD());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("o6ssuporte.bypass")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Usos do comando:");

                    for (SubCommand subCommand : subCommands) {
                        player.sendMessage(ChatColor.YELLOW + subCommand.getSyntax() + " - " + subCommand.getDescription());
                    }
                } else {
                    String command = args[0];

                    for (SubCommand subCommand : subCommands) {
                        if (subCommand.getName().equalsIgnoreCase(command)) {
                            subCommand.perform(player, args);

                            return true;
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Você não tem permissão para utilizar este comando.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Apenas players podem utilizar este comando!");
        }

        return false;
    }

}

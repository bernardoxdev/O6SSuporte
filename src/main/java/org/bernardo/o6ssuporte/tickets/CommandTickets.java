package org.bernardo.o6ssuporte.tickets;

import org.bernardo.o6ssuporte.APIs.SubCommand;
import org.bernardo.o6ssuporte.tickets.APIs.FiltroTypes;
import org.bernardo.o6ssuporte.tickets.subcommands.FecharCMD;
import org.bernardo.o6ssuporte.tickets.subcommands.ResponderCMD;
import org.bernardo.o6ssuporte.tickets.subcommands.VerCMD;
import org.bernardo.o6ssuporte.tickets.templates.TicketsTemplate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandTickets implements CommandExecutor, TabCompleter {

    private final TicketsTemplate ticketsTemplate = new TicketsTemplate();
    private final List<SubCommand> subCommands = new ArrayList<>();

    public CommandTickets() {
        subCommands.add(new FecharCMD());
        subCommands.add(new ResponderCMD());
        subCommands.add(new VerCMD());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("o6ssuporte.admin")) {
                if (args.length == 0) {
                    ticketsTemplate.abrirMenuTicketsStaff(player, 1, FiltroTypes.SEM_TIPO);
                } else {
                    String command = args[0];
                    SubCommand subCommand = getSubCommandByName(command);

                    if (subCommand!= null) {
                        subCommand.perform(player, args);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Comando inválido.");
                    }
                }
            } else {
                ticketsTemplate.abrirMenuTicketsPlayer(player, 1, FiltroTypes.SEM_TIPO);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Apenas players podem utilizar este comando.");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("o6ssuporte.admin")) {
                if (args.length == 1) {
                    List<String> completions = new ArrayList<>();

                    for (SubCommand subCommand : subCommands) {
                        if (subCommand.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                            completions.add(subCommand.getName());
                        }
                    }

                    return completions;
                } else if (args.length > 1) {
                    SubCommand subCommand = getSubCommandByName(args[0]);

                    if (subCommand != null) {
                        return subCommand.getSubcommandArguments(player, args);
                    }
                }
            }
        }

        return null;
    }

    private SubCommand getSubCommandByName(String name) {
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(name)) {
                return subCommand;
            }
        }

        return null;
    }
}

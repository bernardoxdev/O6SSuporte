package org.bernardo.o6ssuporte.tickets;

import org.bernardo.o6ssuporte.tickets.APIs.TicketsAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSuporte implements CommandExecutor {

    private final TicketsAPI ticketsAPI = new TicketsAPI();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {
                String duvida = String.join(" ", args);
                ticketsAPI.createTicket(player,duvida);

                player.sendMessage(ChatColor.GREEN + "Sua dúvida foi enviada com sucesso!");
            } else {
                player.sendMessage(ChatColor.RED + "Utilize: /suporte [duvida]");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Apenas players podem utilizar este comando.");
        }

        return false;
    }

}

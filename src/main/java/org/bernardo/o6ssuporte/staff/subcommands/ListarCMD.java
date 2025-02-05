package org.bernardo.o6ssuporte.staff.subcommands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bernardo.o6ssuporte.APIs.SubCommand;
import org.bernardo.o6ssuporte.database.DatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListarCMD extends SubCommand {

    private final DatabaseAPI databaseAPI = new DatabaseAPI();

    @Override
    public String getName() {
        return "listar";
    }

    @Override
    public String getDescription() {
        return "Comando para listar os players da staff";
    }

    @Override
    public String getSyntax() {
        return "/suportestaff listar";
    }

    @Override
    public void perform(Player player, String[] args) {
        Map<String, String> staffs = databaseAPI.getAllStaffs();

        if (!staffs.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "Staffs:");

            for (String staff : staffs.keySet()) {
                TextComponent message = new TextComponent(ChatColor.YELLOW + "- " + ChatColor.GRAY);
                message.addExtra(Bukkit.getOfflinePlayer(UUID.fromString(staff)).getName() + " ( ");

                TextComponent uuidComponent = new TextComponent(staff);
                uuidComponent.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                uuidComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, staff));

                TextComponent endMessage = new TextComponent(" ) - " + staffs.get(staff));
                endMessage.setColor(net.md_5.bungee.api.ChatColor.GRAY);

                message.addExtra(uuidComponent);
                message.addExtra(endMessage);

                player.spigot().sendMessage(message);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Nenhum staff foi encontrado.");
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}

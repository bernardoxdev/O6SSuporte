package org.bernardo.o6ssuporte;

import org.bernardo.o6ssuporte.staff.CommandStaff;
import org.bernardo.o6ssuporte.tickets.CommandSuporte;
import org.bernardo.o6ssuporte.tickets.CommandTickets;
import org.bernardo.o6ssuporte.tickets.templates.TicketsTemplate;
import org.bernardo.o6ssuporte.database.DatabaseSetup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class O6SSuporte extends JavaPlugin {

    public static O6SSuporte m;

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    @Override
    public void onEnable() {
        console.sendMessage(ChatColor.GREEN + "O6SSuporte ligado com sucesso!");

        getCommand("tickets").setExecutor(new CommandTickets());
        getCommand("suporte").setExecutor(new CommandSuporte());
        getCommand("suportestaff").setExecutor(new CommandStaff());

        pluginManager.registerEvents(new TicketsTemplate(),this);

        super.onEnable();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll();

        console.sendMessage(ChatColor.RED + "O6SSuporte desligado com sucesso!");

        super.onDisable();
    }

    @Override
    public void onLoad() {
        m = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        DatabaseSetup.initializeDatabase();
        console.sendMessage(ChatColor.GREEN + "Banco de dados inicializado com sucesso!");

        super.onLoad();
    }

    public static O6SSuporte getInstance() {
        return m;
    }
}

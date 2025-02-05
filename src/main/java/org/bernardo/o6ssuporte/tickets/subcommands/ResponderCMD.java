package org.bernardo.o6ssuporte.tickets.subcommands;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import org.bernardo.o6ssuporte.APIs.SubCommand;
import org.bernardo.o6ssuporte.database.DatabaseAPI;
import org.bernardo.o6ssuporte.tickets.templates.TicketsTemplate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ResponderCMD extends SubCommand {

    private final String PERMISSAO = "o6ssuporte.admin";

    private final DatabaseAPI databaseAPI = new DatabaseAPI();
    private final TicketsTemplate ticketsTemplate = new TicketsTemplate();

    @Override
    public String getName() {
        return "responder";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "/tickets responder (id) [resposta]";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.hasPermission(PERMISSAO)) {
            player.sendMessage("Você não tem permissão para utilizar este comando!");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("Uso correto: " + getSyntax());
            return;
        }

        String idString = args[1];
        int id;

        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            player.sendMessage("ID inválido.");
            return;
        }

        if (!databaseAPI.ticketExists(id)) {
            player.sendMessage("Ticket não encontrado.");
            return;
        }

        String uuid = databaseAPI.getUuidTicketById(id);

        if (uuid == null) {
            player.sendMessage("Erro ao buscar o ticket no banco de dados.");
            return;
        }

        if (args.length == 2) {
            try {
                ticketsTemplate.abrirMenuTicketInfo(player,1,id);
            } catch (Exception e) {
                player.sendMessage("Erro ao abrir informações do ticket.");
                e.printStackTrace();
            }
        } else {
            String resposta = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            databaseAPI.addMensagemTicket(uuid, player, true, resposta);

            player.sendMessage(ChatColor.GREEN + "Resposta enviada com sucesso!");

            Player target;
            try {
                target = Bukkit.getPlayer(UUID.fromString(uuid));
            } catch (IllegalArgumentException e) {
                player.sendMessage("Erro ao encontrar o jogador associado ao ticket.");
                return;
            }

            if (target != null && target.isValid() && target.isOnline()) {
                ActionBarAPI.sendActionBar(target, ChatColor.GREEN + "Seu ticket foi respondido!");
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}

package org.bernardo.o6ssuporte.tickets.APIs;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.sun.org.apache.xerces.internal.impl.dv.xs.BooleanDV;
import org.bernardo.o6ssuporte.APIs.HeadsAPI;
import org.bernardo.o6ssuporte.database.DatabaseAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import javax.management.remote.rmi._RMIConnection_Stub;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TicketsAPI {

    private final String PERMISSAO = "o6ssuporte.admin";
    private final DatabaseAPI databaseAPI = new DatabaseAPI();

    public void createTicket(Player player, String duvida) {
        String horario = LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        String data = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        int ticketId = databaseAPI.addTicket(player, horario, data, duvida);

        if (ticketId != -1) {
            notificarStaffsTicket(ticketId, horario);
        } else {
            player.sendMessage(ChatColor.RED + "Ocorreu um erro ao criar seu ticket.");
        }
    }

    private void notificarStaffsTicket(int ticketId, String horario) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission(PERMISSAO)) {
                ActionBarAPI.sendActionBar(player, ChatColor.RED + "Novo ticket #" + ticketId + " criado às " + horario + ".");
                player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 1.5f, 1.0f);
            }
        });
    }

    public void fecharTicketStaff(int id, Player player) {
        String uuid = databaseAPI.closeTicket(id);
        Player target = Bukkit.getPlayer(UUID.fromString(uuid));

        if (target != null && target.isValid() && target.isOnline()) {
            target.sendMessage(ChatColor.RED + "O staff " + player.getName() + " fechou seu ticket.");
        }

        player.sendMessage(ChatColor.GREEN + "Ticket fechado com sucesso.");
    }

    public String filtroToString(FiltroTypes filtroTypes) {
        switch (filtroTypes) {
            case ABERTO:
                return "Aberto";
            case RESPONDIDO:
                return "Respondido";
            case FECHADO:
                return "Fechado";
            default:
                return "Todos";
        }
    }

    public FiltroTypes stringToFiltro(String filtroType) {
        switch (filtroType.toLowerCase()) {
            case "aberto":
                return FiltroTypes.ABERTO;
            case "respondido":
                return FiltroTypes.RESPONDIDO;
            case "fechado":
                return FiltroTypes.FECHADO;
            default:
                return FiltroTypes.SEM_TIPO;
        }
    }

    public List<Map<String, String>> getTicketsToMenuStaff(FiltroTypes filtroTypes) {
        if (filtroTypes == null || filtroTypes.equals(FiltroTypes.SEM_TIPO)) {
            return databaseAPI.getTicketsAllMaps();
        } else {
            return databaseAPI.getTicketByStatusMaps(filtroToString(filtroTypes));
        }
    }

    public List<Map<String, String>> getTicketsToMenuPlayer(Player player, FiltroTypes filtroTypes) {
        if (filtroTypes == null || filtroTypes.equals(FiltroTypes.SEM_TIPO)) {
            return databaseAPI.getTicketsByPlayerMap(player);
        } else {
            return databaseAPI.getTicketByStatusPlayerMap(player, filtroToString(filtroTypes));
        }
    }

    public ItemStack itemSemTickets(boolean staff) {
        ItemStack itemStack = new ItemStack(Material.WEB);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.RED + "Nenhum Ticket Encontrado");

        if (staff) {
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Nenhum ticket foi enviado.", "", ChatColor.GRAY + "Quando chegar um ticket você será notificado."));
        } else {
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Você ainda não enviou nenhum ticket.", "", ChatColor.GRAY + "Caso deseja enviar algum ticket, ",
                    ChatColor.GRAY + "utilize /suporte."));
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemAbrirTicket() {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Abrir Ticket");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para abrir um novo ticket."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemFiltrarTicket(FiltroTypes filtroTypes) {
        ItemStack itemStack = new ItemStack(Material.HOPPER);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Filtrar Tickets");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Defina o tipo do filtro dos tickets clicando neste item.", "",
                ChatColor.GRAY + "Tipo: " + ChatColor.GREEN + filtroToString(filtroTypes)));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public FiltroTypes nextFiltroType(FiltroTypes filtroTypes) {
        switch (filtroTypes) {
            case ABERTO:
                return FiltroTypes.RESPONDIDO;
            case RESPONDIDO:
                return FiltroTypes.FECHADO;
            case FECHADO:
                return FiltroTypes.SEM_TIPO;
            default:
                return FiltroTypes.ABERTO;
        }
    }

    public ItemStack itemPesquisarTicket() {
        ItemStack itemStack = new ItemStack(Material.SIGN);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Pesquisar Ticket");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para pesquisar um ticket pelo ID."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemProximaPagina() {
        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Próxima Página");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para ir para a próxima página."));

        itemStack.getItemMeta();

        return itemStack;
    }

    public ItemStack itemAnteriorPagina() {
        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Página Anterior");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para voltar para a página anterior."));

        itemStack.getItemMeta();

        return itemStack;
    }

    public ItemStack itemNumeroPagina(int pagina) {
        ItemStack itemStack = new ItemStack(Material.PAPER, pagina);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Página " + pagina);
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Você está na página " + ChatColor.GREEN + pagina + "."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemPerfilPlayer(Player player) {
        ItemStack itemStack = HeadsAPI.getPlayerSkull(player.getName());
        ItemMeta meta = itemStack.getItemMeta();

        String uuid = player.getUniqueId().toString();

        meta.setDisplayName(ChatColor.YELLOW + "Seu perfil");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Tickets Criados: " + ChatColor.GREEN + databaseAPI.getCountPlayerTickets(uuid),
                ChatColor.GRAY + "Tickets Abertos: " + ChatColor.GREEN + databaseAPI.getCountPlayerTicketsByFiltro(uuid, filtroToString(FiltroTypes.ABERTO)),
                ChatColor.GRAY + "Tickets Respondidos: " + ChatColor.GREEN + databaseAPI.getCountPlayerTicketsByFiltro(uuid, filtroToString(FiltroTypes.RESPONDIDO)),
                ChatColor.GRAY + "Tickets Fechados: " + ChatColor.GREEN + databaseAPI.getCountPlayerTicketsByFiltro(uuid, filtroToString(FiltroTypes.FECHADO))));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemAvaliacoes() {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Avaliações dos Tickets");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Veja as avaliações dos tickets enviados pelos jogadores."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemAvaliar() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Avaliar Staff");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para avaliar um staff."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemTicketMenuStaff(Map<String, String> ticket) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(ticket.get("UUID")));
        String name;
        String cabeca;

        if (target != null) {
            name = target.getName();
            cabeca = target.getName();
        } else {
            cabeca = "MHF_Question";
            name = "Desconhecido";
        }

        ItemStack itemStack = HeadsAPI.getPlayerSkull(cabeca);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Ticket #" + ticket.get("ID"));
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Player: " + ChatColor.GREEN + name,
                ChatColor.GRAY + "Data Abertura: " + ChatColor.GREEN + ticket.get("Data"),
                ChatColor.GRAY + "Horário Abertura: " + ChatColor.GREEN + ticket.get("Horario"),
                ChatColor.GRAY + "Duvida: " + ChatColor.GREEN + ticket.get("Duvida"),
                ChatColor.GRAY + "Status: " + ChatColor.GREEN + ticket.get("Status")));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemTicketMenuJogadores(Map<String, String> ticket) {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Ticket #" + ticket.get("ID"));
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Data Abertura: " + ChatColor.GREEN + ticket.get("Data"),
                ChatColor.GRAY + "Horário Abertura: " + ChatColor.GREEN + ticket.get("Horario"),
                ChatColor.GRAY + "Duvida: " + ChatColor.GREEN + ticket.get("Duvida"),
                ChatColor.GRAY + "Status: " + ChatColor.GREEN + ticket.get("Status")));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemVoltar() {
        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Voltar");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para voltar ao menu principal."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemDiscord() {
        ItemStack itemStack = HeadsAPI.getPlayerSkull("mreren103");
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Discord");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para entrar no Discord.", ChatColor.GRAY + "No discord você consegue tirar melhor suas dúvidas."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemPerfilAvaliacoes(Player player) {
        ItemStack itemStack = HeadsAPI.getPlayerSkull(player.getName());
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Seu Perfil");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Avaliações Recebidas: " + ChatColor.GREEN +
                        databaseAPI.getCountAvaliacoesStaff(player.getUniqueId().toString()),
                ChatColor.GRAY + "Nota Média: " + ChatColor.GREEN + databaseAPI.getNotaMediaStaff(player.getUniqueId().toString())));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemSemAvaliacoes() {
        ItemStack itemStack = new ItemStack(Material.WEB);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.RED + "Nenhuma avaliação encontrada");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Nenhum player avaliou algum staff ainda!"));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemPerfilAvaliar(Player player) {
        ItemStack itemStack = HeadsAPI.getPlayerSkull(player.getName());
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Seu Perfil");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Avaliações Feitas: " + ChatColor.GREEN + databaseAPI.getCountAvaliacoesPlayer(
                player.getUniqueId().toString())));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemPerfilTicket(int id, Map<String, String> ticket) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(databaseAPI.getUuidTicketById(id)));
        String name;
        String cabeca;

        if (target != null) {
            name = target.getName();
            cabeca = target.getName();
        } else {
            cabeca = "MHF_Question";
            name = "Desconhecido";
        }

        ItemStack itemStack = HeadsAPI.getPlayerSkull(cabeca);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Ticket de " + name);
        meta.setLore(Arrays.asList(ChatColor.GRAY + "ID: " + ChatColor.GREEN + ticket.get("ID"),
                ChatColor.GRAY + "UUID: " + ChatColor.GREEN + ticket.get("UUID"),
                ChatColor.GRAY + "Data Abertura: " + ChatColor.GREEN + ticket.get("Data"),
                ChatColor.GRAY + "Horário Abertura: " + ChatColor.GREEN + ticket.get("Horario"),
                ChatColor.GRAY + "Duvida: " + ChatColor.GREEN + ticket.get("Duvida"),
                ChatColor.GRAY + "Status: " + ChatColor.GREEN + ticket.get("Status")));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemReabrirTicket() {
        ItemStack itemStack = new ItemStack(Material.WOOL, 1, (short) 13);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Reabrir Ticket");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para reabrir um ticket."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemEnviarMensagem() {
        ItemStack itemStack = new ItemStack(Material.CHEST);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Enviar Mensagem");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para registrar uma mensagem no ticket."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemFecharTicket() {
        ItemStack itemStack = new ItemStack(Material.WOOL, 1, (short) 14);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Fechar Ticket");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para fechar este ticket."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public Player getPlayerByTicket(int id) {
        return Bukkit.getPlayer(UUID.fromString(databaseAPI.getUuidTicketById(id)));
    }

    public Map<String, String> getTicketById(int id) {
        return databaseAPI.getTicketByIDMap(id);
    }

    public Map<Integer, String> transformMensagens(String string) {
        Map<Integer, String> map = new HashMap<>();

        for (String newString : string.split(", ")) {
            String[] split = newString.split(": ");

            if (!split[0].matches("\\d+")) {
                Bukkit.getLogger().warning("Entrada inválida: " + split[0]);
                continue;
            }

            map.put(Integer.parseInt(split[0]), split[1]);
        }

        return map;
    }

    public ItemStack itemMensagemTicket(int posicao, String mensagem) {
        String[] mensagemSplitada = mensagem.split(" - ");
        UUID uuid = UUID.fromString(mensagemSplitada[0]);
        String mensagemPura = mensagemSplitada[1];

        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);

        ItemStack itemStack = HeadsAPI.getPlayerSkull(target.getName());
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Mensagem #" + posicao);
        meta.setLore(Arrays.asList(ChatColor.GRAY + mensagemPura));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemSelecionarStaff(String staff) {
        ItemStack itemStack = new ItemStack(Material.SIGN);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Selecionar Staff");

        if (staff == null || staff.isEmpty()) {
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para escolher um staff para avaliar."));
        } else {
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Staff: " + ChatColor.GREEN + staff));
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemNotaStaff(String nota) {
        ItemStack itemStack = new ItemStack(Material.EMERALD);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Nota Staff");

        if (nota == null || nota.isEmpty()) {
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para digitar a nota do staff."));
        } else {
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Nota: " + ChatColor.GREEN + nota));
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemComentarioStaff(String comentario) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Comentário Staff");

        if (comentario == null || comentario.isEmpty()) {
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para digitar o comentário do staff."));
        } else {
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Comentário: " + ChatColor.GREEN + comentario));
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemEnviarAvaliacao() {
        ItemStack itemStack = new ItemStack(Material.WOOL, 1, (short) 13);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Enviar Avaliação");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para enviar a avaliação do staff."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack itemHistoricoAvaliacoes() {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Histórico de Avaliações");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique para ver o histórico de avaliações."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public List<Map<String, String>> getAllAvaliacoes() {
        return databaseAPI.getAllAvaliacoes();
    }

    public List<Map<String, String>> getAvaliacoesByPlayer(String uuid) {
        return databaseAPI.getAllAvaliacoesByPlayer(uuid);
    }

    public ItemStack itemAvaliacaoTicket(Map<String, String> avaliacao) {
        String staffUUIDStr = avaliacao.get("staff_uuid");
        String avaliadorUUIDStr = avaliacao.get("uuid");

        if (staffUUIDStr == null || avaliadorUUIDStr == null) {
            Bukkit.getLogger().warning("UUID ausente na avaliação: " + avaliacao);

            return new ItemStack(Material.BARRIER);
        }

        UUID staffUUID, avaliadorUUID;

        try {
            staffUUID = UUID.fromString(staffUUIDStr);
            avaliadorUUID = UUID.fromString(avaliadorUUIDStr);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("UUID inválido na avaliação: " + avaliacao);

            return new ItemStack(Material.BARRIER);
        }

        OfflinePlayer staff = Bukkit.getOfflinePlayer(staffUUID);
        OfflinePlayer avaliador = Bukkit.getOfflinePlayer(avaliadorUUID);

        ItemStack itemStack = HeadsAPI.getPlayerSkull(staff.getName());
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + "Staff Avaliado: " + ChatColor.RED + staff.getName());
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Avaliador: " + avaliador.getName(),
                "",
                ChatColor.GRAY + "Nota: " + ChatColor.GREEN + avaliacao.get("nota"),
                ChatColor.GRAY + "Avaliação: " + ChatColor.GREEN + avaliacao.get("avaliacao")));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public boolean ticketExists(int id) {
        return databaseAPI.ticketExists(id);
    }

    public String getUuidTicketById(int id) {
        return databaseAPI.getUuidTicketById(id);
    }

    public void reabrirTicket(int id) {
        databaseAPI.abertoTicket(id);
    }

    public String getStatusTicket(int id) {
        return databaseAPI.getStatusTicket(id);
    }

    public void enviarMensagemTicket(String ticketUUID, Player player, boolean staff, String mensagem) {
        databaseAPI.addMensagemTicket(ticketUUID,player,staff,mensagem);
    }

    public void fecharTicket(int id) {
        databaseAPI.closeTicket(id);
    }

    public void enviarAvaliacao(Player avaliador, String staff, int nota, String comentario) {
        databaseAPI.addAvalicao(avaliador.getUniqueId().toString(), staff, nota, comentario);

        avaliador.sendMessage(ChatColor.GREEN + "Avaliação enviada com sucesso!");
    }

    public List<String> getStaffsUUIDs() {
        return databaseAPI.getStaffsUUIDs();
    }

    public ItemStack itemEmBreve() {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_RED + "Em Breve");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Estamos trabalhando para que vocês recebam o melhor dos nossos sistemas.", ChatColor.RED + "Em breve " +
                "essa funcionalidade vai estar no ar."));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

}

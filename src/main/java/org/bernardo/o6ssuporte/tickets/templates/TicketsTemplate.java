package org.bernardo.o6ssuporte.tickets.templates;

import org.bernardo.o6ssuporte.tickets.APIs.FiltroTypes;
import org.bernardo.o6ssuporte.tickets.APIs.TicketsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TicketsTemplate implements Listener {

    // TODO: Colocar como ver mais páginas no menu principal
    // TODO: Colocar como ver mais páginas no menu do ticket

    private final String MENU_TICKETS = ChatColor.GRAY + "Menu de Tickets";
    private final String MENU_TICKET_INFO = ChatColor.GRAY + "Menu Ticket";
    private final String MENU_AVALIAR = ChatColor.GRAY + "Menu Avaliar";
    private final String MENU_AVALIACOES = ChatColor.GRAY + "Menu Avaliações";
    private final String MENU_HISTORICO_AVALIACOES = ChatColor.GRAY + "Menu Histórico Avaliações";
    private final String PERMISSAO_ADMIN = "o6ssuporte.admin";
    private final String PERMISSAO_BYPASS = "o6ssuporte.bypass";
    private final TicketsAPI ticketsAPI = new TicketsAPI();
    private final Map<Player, String> playersChatEvent = new HashMap<>();
    private final Map<Player, Map<Integer, String>> informacoesChatEvent = new HashMap<>();

    public void abrirMenuTicketsPlayer(Player player, int pagina, FiltroTypes filtroTypes) {
        Inventory inv = Bukkit.createInventory(null, 6 * 9, MENU_TICKETS);

        int posInicial = 10;
        int contador = 0;

        List<Map<String, String>> tickets = ticketsAPI.getTicketsToMenuPlayer(player, filtroTypes);

        if (tickets.isEmpty()) {
            inv.setItem(22, ticketsAPI.itemSemTickets(false));
        } else {
            int size = tickets.size();
            List<Map<String, String>> finalTickets = new ArrayList<>();

            if (size > 28) {
                inv.setItem(53, ticketsAPI.itemProximaPagina());

                if (pagina > 1) {
                    inv.setItem(45, ticketsAPI.itemAnteriorPagina());
                }

                int ticketInicial = 28 * (pagina - 1);
                int ticketFinal = 28 * (pagina);

                for (int i = ticketInicial; i <= ticketFinal; i++) {
                    finalTickets.add(tickets.get(i));
                }
            } else {
                finalTickets = tickets;
            }

            for (Map<String, String> ticket : finalTickets) {
                ItemStack item = ticketsAPI.itemTicketMenuJogadores(ticket);

                if (contador == 7) {
                    posInicial += 2;
                    contador = 0;
                }

                inv.setItem(posInicial, item);

                posInicial++;
                contador++;
            }
        }

        inv.setItem(2, ticketsAPI.itemNumeroPagina(pagina));
        inv.setItem(3, ticketsAPI.itemPerfilPlayer(player));
        inv.setItem(5, ticketsAPI.itemAvaliar());
        inv.setItem(6, ticketsAPI.itemDiscord());

        inv.setItem(47, ticketsAPI.itemAbrirTicket());
        inv.setItem(49, ticketsAPI.itemFiltrarTicket(filtroTypes));
        inv.setItem(51, ticketsAPI.itemPesquisarTicket());

        player.openInventory(inv);
    }

    public void abrirMenuTicketsStaff(Player player, int pagina, FiltroTypes filtroTypes) {
        Inventory inv = Bukkit.createInventory(null, 6 * 9, MENU_TICKETS);

        int posInicial = 10;
        int contador = 0;

        List<Map<String, String>> tickets = ticketsAPI.getTicketsToMenuStaff(filtroTypes);

        if (tickets.isEmpty()) {
            inv.setItem(22, ticketsAPI.itemSemTickets(true));
        } else {
            int size = tickets.size();
            List<Map<String, String>> finalTickets = new ArrayList<>();

            if (size > 28) {
                inv.setItem(53, ticketsAPI.itemProximaPagina());

                if (pagina > 1) {
                    inv.setItem(45, ticketsAPI.itemAnteriorPagina());
                }

                int ticketInicial = 28 * (pagina - 1);
                int ticketFinal = 28 * (pagina);

                for (int i = ticketInicial; i <= ticketFinal; i++) {
                    finalTickets.add(tickets.get(i));
                }
            } else {
                finalTickets = tickets;
            }

            for (Map<String, String> ticket : finalTickets) {
                ItemStack item = ticketsAPI.itemTicketMenuStaff(ticket);

                if (contador == 7) {
                    posInicial += 2;
                    contador = 0;
                }

                inv.setItem(posInicial, item);

                posInicial++;
                contador++;
            }
        }

        inv.setItem(3, ticketsAPI.itemNumeroPagina(pagina));
        inv.setItem(5, ticketsAPI.itemAvaliacoes());

        inv.setItem(48, ticketsAPI.itemFiltrarTicket(filtroTypes));
        inv.setItem(50, ticketsAPI.itemPesquisarTicket());

        player.openInventory(inv);
    }

    public void abrirMenuTicketInfo(Player player, int pagina, int id) {
        Map<String, String> ticket = ticketsAPI.getTicketById(id);

        Inventory inv = Bukkit.createInventory(null, 6 * 9, MENU_TICKET_INFO);

        Map<Integer, String> mapMensagens = ticketsAPI.transformMensagens(ticket.get("Mensagens"));
        int size = mapMensagens.size();
        Map<Integer, String> finalMensagens = new HashMap<>();

        if (size > 28) {
            inv.setItem(51, ticketsAPI.itemProximaPagina());

            if (pagina > 1) {
                inv.setItem(47, ticketsAPI.itemVoltar());
            }

            int mensagemInicial = 28 * (pagina - 1);
            int mensagemFinal = 28 * (pagina);

            for (int i = mensagemInicial; i <= mensagemFinal; i++) {
                finalMensagens.put(i, mapMensagens.get(i));
            }
        } else {
            finalMensagens = mapMensagens;
        }

        int posInicial = 10;
        int contador = 0;

        for (int posicao : finalMensagens.keySet()) {
            if (contador == 7) {
                contador = 0;
                posInicial += 2;
            }

            inv.setItem(posInicial, ticketsAPI.itemMensagemTicket(posicao, mapMensagens.get(posicao)));

            contador++;
            posInicial++;
        }

        inv.setItem(4, ticketsAPI.itemPerfilTicket(id, ticket));

        inv.setItem(48, ticketsAPI.itemReabrirTicket());
        inv.setItem(49, ticketsAPI.itemEnviarMensagem());
        inv.setItem(50, ticketsAPI.itemFecharTicket());

        inv.setItem(53, ticketsAPI.itemVoltar());

        player.openInventory(inv);
    }

    public void abrirMenuAvaliar(Player player, String staff, String nota, String comentario) {
        Inventory inv = Bukkit.createInventory(null, 5 * 9, MENU_AVALIAR);

        inv.setItem(3, ticketsAPI.itemPerfilAvaliar(player));
        inv.setItem(5, ticketsAPI.itemHistoricoAvaliacoes());

        inv.setItem(20, ticketsAPI.itemSelecionarStaff(staff));
        inv.setItem(22, ticketsAPI.itemNotaStaff(nota));
        inv.setItem(24, ticketsAPI.itemComentarioStaff(comentario));

        inv.setItem(44, ticketsAPI.itemEnviarAvaliacao());
        inv.setItem(36, ticketsAPI.itemVoltar());

        player.openInventory(inv);
    }

    public void abrirMenuAvaliacoes(Player player, int pagina) {
        Inventory inv = Bukkit.createInventory(null, 6 * 9, MENU_AVALIACOES);

        List<Map<String, String>> avaliacoes = ticketsAPI.getAllAvaliacoes();
        int size = avaliacoes.size();

        if (avaliacoes.isEmpty()) {
            inv.setItem(22, ticketsAPI.itemSemAvaliacoes());
        } else {
            List<Map<String, String>> avaliacoesFinal = new ArrayList<>();

            if (size > 28) {
                inv.setItem(51, ticketsAPI.itemProximaPagina());

                if (pagina > 1) {
                    inv.setItem(47, ticketsAPI.itemVoltar());
                }

                for (int i = 0; i <= 28; i++) {
                    avaliacoesFinal.add(avaliacoes.get(i));
                }
            } else {
                avaliacoesFinal = avaliacoes;
            }

            int posInicial = 10;
            int contador = 0;

            for (Map<String, String> avaliacao : avaliacoesFinal) {
                if (contador == 7) {
                    contador = 0;
                    posInicial += 2;
                }

                inv.setItem(posInicial, ticketsAPI.itemAvaliacaoTicket(avaliacao));

                contador++;
                posInicial++;
            }
        }

        inv.setItem(4, ticketsAPI.itemPerfilAvaliacoes(player));

        inv.setItem(53, ticketsAPI.itemVoltar());

        player.openInventory(inv);
    }

    public void abrirMenuHistoricoAvaliacoes(Player player) {
        Inventory inv = Bukkit.createInventory(null, 6 * 9, MENU_HISTORICO_AVALIACOES);

        List<Map<String, String>> avaliacoes = ticketsAPI.getAvaliacoesByPlayer(player.getUniqueId().toString());
        int size = avaliacoes.size();

        if (size <= 28) {
            int posInicial = 10;
            int contador = 0;

            for (Map<String, String> avaliacao : avaliacoes) {
                if (contador == 7) {
                    contador = 0;
                    posInicial += 2;
                }

                inv.setItem(posInicial, ticketsAPI.itemAvaliacaoTicket(avaliacao));

                contador++;
                posInicial++;
            }
        } else {
            inv.setItem(22,ticketsAPI.itemEmBreve());
        }

        inv.setItem(53,ticketsAPI.itemVoltar());

        player.openInventory(inv);
    }

    @EventHandler
    public void clickInventory(InventoryClickEvent e) {
        if (e.getView() != null && e.getView().getTitle() != null) {
            if (e.getView().getTitle().equals(MENU_TICKETS)) {
                e.setCancelled(true);

                ItemStack item = e.getCurrentItem();

                if (item == null || !item.hasItemMeta()) {
                    return;
                }

                ItemMeta meta = item.getItemMeta();
                if (meta.getDisplayName() == null) {
                    return;
                }

                Player player = (Player) e.getWhoClicked();

                if (item.equals(ticketsAPI.itemAbrirTicket())) {
                    player.closeInventory();
                    playersChatEvent.put(player, "Abrindo_Ticket");

                    player.sendMessage(ChatColor.GREEN + "Qual a dúvida?");
                    player.sendMessage(ChatColor.GRAY + "Digite '" + ChatColor.RED + "cancelar" + ChatColor.GRAY + "' para cancelar a abertura do ticket.");
                } else if (item.equals(ticketsAPI.itemPesquisarTicket())) {
                    player.closeInventory();
                    playersChatEvent.put(player, "Pesquisando_Ticket");

                    player.sendMessage(ChatColor.GREEN + "Qual o id do ticket que você está procurando?");
                    player.sendMessage(ChatColor.GRAY + "Digite '" + ChatColor.RED + "cancelar" + ChatColor.GRAY + "' para cancelar a pesquisa.");
                } else if (item.equals(ticketsAPI.itemProximaPagina())) {
                    ItemStack itemPagina = e.getInventory().getItem(3);
                    int pagina = Integer.parseInt(ChatColor.stripColor(itemPagina.getItemMeta().getDisplayName().split(" ")[1]));

                    String filtroString = ChatColor.stripColor(meta.getLore().get(2).split(": ")[1]);
                    FiltroTypes filtroTypes = ticketsAPI.stringToFiltro(filtroString);

                    if (player.hasPermission(PERMISSAO_BYPASS) || player.hasPermission(PERMISSAO_ADMIN)) {
                        abrirMenuTicketsStaff(player, pagina + 1, filtroTypes);
                    } else {
                        abrirMenuTicketsPlayer(player, pagina + 1, filtroTypes);
                    }
                } else if (item.equals(ticketsAPI.itemAnteriorPagina())) {
                    ItemStack itemPagina = e.getInventory().getItem(3);
                    int pagina = Integer.parseInt(ChatColor.stripColor(itemPagina.getItemMeta().getDisplayName().split(" ")[1]));

                    String filtroString = ChatColor.stripColor(meta.getLore().get(2).split(": ")[1]);
                    FiltroTypes filtroTypes = ticketsAPI.stringToFiltro(filtroString);

                    if (player.hasPermission(PERMISSAO_ADMIN) || player.hasPermission(PERMISSAO_BYPASS)) {
                        abrirMenuTicketsStaff(player, pagina - 1, filtroTypes);
                    } else {
                        abrirMenuTicketsPlayer(player, pagina - 1, filtroTypes);
                    }
                } else if (item.equals(ticketsAPI.itemAvaliar())) {
                    player.closeInventory();

                    abrirMenuAvaliar(player, null, null, null);
                } else if (item.equals(ticketsAPI.itemAvaliacoes())) {
                    if (player.hasPermission(PERMISSAO_BYPASS)) {
                        player.closeInventory();

                        abrirMenuAvaliacoes(player, 1);
                    } else {
                        player.sendMessage(ChatColor.RED + "Você não possui permissão para ver as avaliações.");
                    }
                } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Filtrar tickets")) {
                    player.closeInventory();

                    String filtroString = ChatColor.stripColor(meta.getLore().get(2).split(": ")[1]);
                    FiltroTypes filtroTypes = ticketsAPI.stringToFiltro(filtroString);
                    FiltroTypes nextFiltro = ticketsAPI.nextFiltroType(filtroTypes);

                    ItemStack itemPagina;
                    if (player.hasPermission(PERMISSAO_ADMIN) || player.hasPermission(PERMISSAO_BYPASS)) {
                        itemPagina = e.getInventory().getItem(3);
                    } else {
                        itemPagina = e.getInventory().getItem(2);
                    }

                    int pagina = Integer.parseInt(ChatColor.stripColor(itemPagina.getItemMeta().getDisplayName().split(" ")[1]));

                    if (player.hasPermission(PERMISSAO_ADMIN) || player.hasPermission(PERMISSAO_BYPASS)) {
                        abrirMenuTicketsStaff(player, pagina, nextFiltro);
                    } else {
                        abrirMenuTicketsPlayer(player, pagina, nextFiltro);
                    }
                } else if (meta.getDisplayName().contains(ChatColor.YELLOW + "Ticket")) {
                    player.closeInventory();

                    int id = Integer.parseInt(ChatColor.stripColor(meta.getDisplayName().split(" #")[1]));
                    abrirMenuTicketInfo(player, 1, id);
                } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Discord")) {
                    player.closeInventory();

                    player.sendMessage(ChatColor.GRAY + "Nosso discord: " + ChatColor.GREEN + "https://discord.gg/9UPGardZTP");
                }
            } else if (e.getView().getTitle().equals(MENU_TICKET_INFO)) {
                e.setCancelled(true);

                ItemStack item = e.getCurrentItem();

                if (item == null || !item.hasItemMeta()) {
                    return;
                }

                ItemMeta meta = item.getItemMeta();
                if (meta.getDisplayName() == null) {
                    return;
                }

                Player player = (Player) e.getWhoClicked();

                ItemStack itemPerfil = e.getInventory().getItem(4);
                if (itemPerfil == null || !itemPerfil.hasItemMeta()) {
                    return;
                }

                int idTicket = Integer.parseInt(ChatColor.stripColor(itemPerfil.getItemMeta().getLore().get(0).split(": ")[1]));
                UUID uuidDono = UUID.fromString(ChatColor.stripColor(itemPerfil.getItemMeta().getLore().get(1).split(": ")[1]));

                if (item.equals(ticketsAPI.itemVoltar())) {
                    voltarMenuPrincipal(player);
                } else if (item.equals(ticketsAPI.itemReabrirTicket())) {
                    player.closeInventory();

                    if (ticketsAPI.getStatusTicket(idTicket).equalsIgnoreCase("Fechado")) {
                        if (player.hasPermission(PERMISSAO_BYPASS) || player.getUniqueId().equals(uuidDono)) {
                            ticketsAPI.reabrirTicket(idTicket);

                            player.sendMessage(ChatColor.GREEN + "Ticket reaberto com sucesso!");
                        } else {
                            player.sendMessage(ChatColor.RED + "Você não possui permissão para reabrir este ticket.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Este ticket não foi fechado.");
                    }
                } else if (item.equals(ticketsAPI.itemEnviarMensagem())) {
                    player.closeInventory();
                    Map<Integer, String> informacoes = new HashMap<>();
                    informacoes.put(0, "" + idTicket);
                    informacoes.put(1, uuidDono.toString());

                    playersChatEvent.put(player, "Enviar_Mensagem_Ticket");
                    informacoesChatEvent.put(player, informacoes);
                    player.sendMessage(ChatColor.GREEN + "Digite a mensagem que você deseja enviar no ticket.");
                } else if (item.equals(ticketsAPI.itemFecharTicket())) {
                    player.closeInventory();

                    if (!ticketsAPI.getStatusTicket(idTicket).equalsIgnoreCase("Fechado")) {
                        if (player.hasPermission(PERMISSAO_BYPASS) || player.getUniqueId().equals(uuidDono)) {
                            ticketsAPI.fecharTicketStaff(idTicket, player);
                        } else {
                            ticketsAPI.fecharTicket(idTicket);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "O ticket ja está fechado.");
                    }
                } else if (meta.getDisplayName().contains(ChatColor.YELLOW + "Mensagem")) {
                    player.closeInventory();

                    int idMensagem = Integer.parseInt(ChatColor.stripColor(meta.getDisplayName().split(" #")[1]));
                    Map<Integer, String> mapMensagens = ticketsAPI.transformMensagens(ticketsAPI.getTicketById(idTicket).get("Mensagens"));

                    String mensagem = mapMensagens.get(idMensagem);
                    String[] mensagemSplitada = mensagem.split(" - ");
                    OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(mensagemSplitada[0]));

                    player.sendMessage("");
                    player.sendMessage(ChatColor.RED + "Mensagem #" + idMensagem + " do ticket de ID #" + idTicket);
                    player.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.WHITE + " - " + ChatColor.GRAY + mensagemSplitada[1]);
                    player.sendMessage("");
                }
            } else if (e.getView().getTitle().equals(MENU_AVALIACOES)) {
                e.setCancelled(true);

                ItemStack item = e.getCurrentItem();

                if (item == null || !item.hasItemMeta()) {
                    return;
                }

                ItemMeta meta = item.getItemMeta();
                if (meta.getDisplayName() == null) {
                    return;
                }

                Player player = (Player) e.getWhoClicked();

                if (item.equals(ticketsAPI.itemVoltar())) {
                    voltarMenuPrincipal(player);
                } else if (meta.getDisplayName().contains(ChatColor.YELLOW + "Staff Avaliado: ")) {
                    player.closeInventory();

                    player.sendMessage("");
                    player.sendMessage(ChatColor.RED + "Avaliação Staff");
                    player.sendMessage(meta.getDisplayName());

                    for (String lore : meta.getLore()) {
                        player.sendMessage(lore);
                    }

                    player.sendMessage("");
                }
            } else if (e.getView().getTitle().equals(MENU_AVALIAR)) {
                e.setCancelled(true);

                ItemStack item = e.getCurrentItem();

                if (item == null || !item.hasItemMeta()) {
                    return;
                }

                ItemMeta meta = item.getItemMeta();
                if (meta.getDisplayName() == null) {
                    return;
                }

                Player player = (Player) e.getWhoClicked();

                String staff = null;
                String nota = null;
                String comentario = null;

                Map<Integer, String> map = new HashMap<>();

                if (e.getInventory().getItem(20).getItemMeta().getLore().get(0).contains(ChatColor.GRAY + "Staff:")) {
                    staff = ChatColor.stripColor(e.getInventory().getItem(20).getItemMeta().getLore().get(0).split(": ")[1]);
                }

                if (e.getInventory().getItem(22).getItemMeta().getLore().get(0).contains(ChatColor.GRAY + "Nota: ")) {
                    nota = ChatColor.stripColor(e.getInventory().getItem(22).getItemMeta().getLore().get(0).split(": ")[1]);
                }

                if (e.getInventory().getItem(24).getItemMeta().getLore().get(0).contains(ChatColor.GRAY + "Comentário:")) {
                    comentario = ChatColor.stripColor(e.getInventory().getItem(24).getItemMeta().getLore().get(0).split(": ")[1]);
                }

                map.put(0, staff);
                map.put(1, nota);
                map.put(2, comentario);

                if (item.equals(ticketsAPI.itemVoltar())) {
                    voltarMenuPrincipal(player);
                } else if (item.equals(ticketsAPI.itemEnviarAvaliacao())) {
                    player.closeInventory();

                    if (staff == null || nota == null) {
                        player.sendMessage(ChatColor.RED + "Todos os campos devem ser preenchidos.");
                        abrirMenuAvaliar(player, staff, nota, comentario);

                        return;
                    }

                    if (comentario == null) {
                        comentario = "Sem comentário";
                    }

                    ticketsAPI.enviarAvaliacao(player, staff, Integer.parseInt(nota), comentario);
                } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Selecionar Staff")) {
                    player.closeInventory();

                    player.sendMessage(ChatColor.GREEN + "Envie o nick do staff que você quer avaliar.");
                    player.sendMessage(ChatColor.GRAY + "Digite '" + ChatColor.RED + "cancelar" + ChatColor.GRAY + "' para cancelar a avaliação.");

                    informacoesChatEvent.put(player, map);
                    playersChatEvent.put(player, "Selecionar_Staff");
                } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Nota Staff")) {
                    player.closeInventory();

                    player.sendMessage(ChatColor.GREEN + "Digite a nota para esse staff.");
                    player.sendMessage(ChatColor.GRAY + "Digite '" + ChatColor.RED + "cancelar" + ChatColor.GRAY + "' para cancelar a avaliação.");

                    informacoesChatEvent.put(player, map);
                    playersChatEvent.put(player, "Nota_Staff");
                } else if (meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Comentário Staff")) {
                    player.closeInventory();

                    player.sendMessage(ChatColor.GREEN + "Digite um feedback sobre o staff.");
                    player.sendMessage(ChatColor.GRAY + "Digite '" + ChatColor.RED + "cancelar" + ChatColor.GRAY + "' para cancelar a avaliação.");

                    informacoesChatEvent.put(player, map);
                    playersChatEvent.put(player, "Comentario_Staff");
                } else if (item.equals(ticketsAPI.itemHistoricoAvaliacoes())) {
                    player.closeInventory();

                    abrirMenuHistoricoAvaliacoes(player);
                }
            } else if (e.getView().getTitle().equals(MENU_HISTORICO_AVALIACOES)) {
                e.setCancelled(true);

                ItemStack item = e.getCurrentItem();

                if (item == null || !item.hasItemMeta()) {
                    return;
                }

                ItemMeta meta = item.getItemMeta();
                if (meta.getDisplayName() == null) {
                    return;
                }

                Player player = (Player) e.getWhoClicked();

                if (item.equals(ticketsAPI.itemVoltar())) {
                    player.closeInventory();

                    abrirMenuAvaliar(player,null,null,null);
                }
            }
        }
    }

    @EventHandler
    public void asyncChatEvent(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (playersChatEvent.containsKey(player)) {
            e.setCancelled(true);
            String motivo = playersChatEvent.get(player);
            String mensagem = e.getMessage();

            if (mensagem.equalsIgnoreCase("cancelar")) {
                player.sendMessage(ChatColor.RED + "Operação cancelada.");
                return;
            }

            if (motivo.equalsIgnoreCase("Abrindo_Ticket")) {
                ticketsAPI.createTicket(player, mensagem);

                player.sendMessage(ChatColor.GREEN + "Ticket criado com sucesso!");
            } else if (motivo.equalsIgnoreCase("Pesquisando_Ticket")) {
                int id = 0;
                try {
                    id = Integer.parseInt(mensagem);
                } catch (Error error) {
                    player.sendMessage(ChatColor.RED + "Digite um número válido.");
                    return;
                }

                if (!ticketsAPI.ticketExists(id)) {
                    player.sendMessage(ChatColor.RED + "O ticket com id " + mensagem + " não foi encontrado.");
                    return;
                }

                if (player.hasPermission(PERMISSAO_BYPASS) || player.hasPermission(PERMISSAO_ADMIN)) {
                    abrirMenuTicketInfo(player, 1, id);
                } else {
                    String uuidDono = ticketsAPI.getUuidTicketById(id);

                    if (player.getUniqueId().toString().equalsIgnoreCase(uuidDono)) {
                        abrirMenuTicketInfo(player, 1, id);
                    } else {
                        player.sendMessage(ChatColor.RED + "Você não possui permissão para ver este ticket.");
                        return;
                    }
                }

                player.sendMessage(ChatColor.GREEN + "Ticket aberto com sucesso!");
            } else if (motivo.equalsIgnoreCase("Enviar_Mensagem_Ticket")) {
                String uuidTicket = informacoesChatEvent.get(player).get(1);
                boolean staff = player.hasPermission(PERMISSAO_ADMIN) || player.hasPermission(PERMISSAO_BYPASS);

                ticketsAPI.enviarMensagemTicket(uuidTicket, player, staff, mensagem);
                player.sendMessage(ChatColor.GREEN + "Mensagem enviada com sucesso!");
            } else if (motivo.equalsIgnoreCase("Selecionar_Staff")) {
                List<String> staffUUIDs = ticketsAPI.getStaffsUUIDs();
                Map<String, String> staffNames = new HashMap<>();

                for (String uuid : staffUUIDs) {
                    staffNames.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName(), uuid);
                }

                if (!staffNames.containsKey(mensagem)) {
                    player.sendMessage(ChatColor.RED + "Este staff não foi encontrado.");
                    player.sendMessage(ChatColor.RED + "Staffs válidos:");

                    for (String staff : staffNames.keySet()) {
                        player.sendMessage(ChatColor.GRAY + "- " + ChatColor.YELLOW + staff);
                    }

                    abrirMenuAvaliar(player, informacoesChatEvent.get(player).get(0), informacoesChatEvent.get(player).get(1),
                            informacoesChatEvent.get(player).get(2));
                } else {
                    abrirMenuAvaliar(player, staffNames.get(mensagem), informacoesChatEvent.get(player).get(1), informacoesChatEvent.get(player).get(2));
                }
            } else if (motivo.equalsIgnoreCase("Nota_Staff")) {
                int nota = 0;
                try {
                    nota = Integer.parseInt(mensagem);
                } catch (Error error) {
                    player.sendMessage(ChatColor.RED + "Digite um número válido.");
                    abrirMenuAvaliar(player,informacoesChatEvent.get(player).get(0),informacoesChatEvent.get(player).get(1),
                            informacoesChatEvent.get(player).get(2));
                    return;
                }

                if (nota < 0 || nota > 5) {
                    player.sendMessage(ChatColor.RED + "A nota deve ser um número entre 0 e 5.");
                    abrirMenuAvaliar(player,informacoesChatEvent.get(player).get(0),informacoesChatEvent.get(player).get(1),
                            informacoesChatEvent.get(player).get(2));
                    return;
                }

                abrirMenuAvaliar(player, informacoesChatEvent.get(player).get(0), Integer.toString(nota), informacoesChatEvent.get(player).get(2));
            } else if (motivo.equalsIgnoreCase("Comentario_Staff")) {
                abrirMenuAvaliar(player, informacoesChatEvent.get(player).get(0), informacoesChatEvent.get(player).get(1), mensagem);
            }

            playersChatEvent.remove(player);
            informacoesChatEvent.remove(player);
        }
    }

    private void voltarMenuPrincipal(Player player) {
        player.closeInventory();

        if (player.hasPermission(PERMISSAO_ADMIN) || player.hasPermission(PERMISSAO_BYPASS)) {
            abrirMenuTicketsStaff(player, 1, FiltroTypes.SEM_TIPO);
        } else {
            abrirMenuTicketsPlayer(player, 1, FiltroTypes.SEM_TIPO);
        }
    }

}

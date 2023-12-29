package org.popcraft.popcraft.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.data.Ticket;
import org.popcraft.popcraft.utils.TabCompleteUtil;
import org.popcraft.popcraft.utils.TeleportUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandTicket extends PopCraftCommand {

    private Gson gson;
    private File ticketFile, ticketFileDeleted;
    private Map<String, Ticket> tickets;

    public CommandTicket() {
        super("ticket");
        this.gson = new Gson();
        this.ticketFile = new File(plugin.getDataFolder().toString() + File.separator + "tickets.json");
        this.ticketFileDeleted = new File(plugin.getDataFolder().toString() + File.separator + "tickets.dat");
        this.tickets = new HashMap<>();
        this.readTickets();
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!sender.hasPermission("popcraft.ticket.mod")) {
                return Result.INCORRECT_USAGE;
            }
            this.sendHelp(sender);
        } else {
            if (args[0].equalsIgnoreCase("send") && args.length > 1) {
                String id = this.createTicket(sender, String.join(" ", args).substring(5));
                sender.sendMessage(plugin.getMessage("ticketSend", id));
                this.notifyAllStaff();
            } else if (!sender.hasPermission("popcraft.ticket.mod")) {
                return Result.INCORRECT_USAGE;
            } else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("archive")) {
                boolean open = args[0].equalsIgnoreCase("list");
                if (this.ticketCount(open) == 0) {
                    sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorNoTickets")));
                } else {
                    try {
                        this.list(sender, open, args.length == 1 ? -1 : Integer.parseInt(args[1]));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorInvalidPage")));
                    }
                }
            } else if (args[0].equalsIgnoreCase("read") && args.length > 1) {
                this.read(sender, args[1]);
            } else if (args[0].equalsIgnoreCase("claim") && args.length > 1) {
                this.claim(sender, args[1]);
                sender.sendMessage(plugin.getMessage("ticketClaim", args[1]));
            } else if (args[0].equalsIgnoreCase("unclaim") && args.length > 1) {
                if (this.unclaim(sender, args[1])) {
                    sender.sendMessage(plugin.getMessage("ticketUnclaim", args[1]));
                } else {
                    sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorCannotUnclaim")));
                }
            } else if (args[0].equalsIgnoreCase("open") && args.length > 1) {
                if (this.open(sender, args[1])) {
                    sender.sendMessage(plugin.getMessage("ticketOpen", args[1]));
                } else {
                    sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorAlreadyOpen")));
                }
            } else if (args[0].equalsIgnoreCase("close") && args.length > 1) {
                if (this.close(sender, args[1])) {
                    sender.sendMessage(plugin.getMessage("ticketClose", args[1]));
                } else {
                    sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorCantCloseUnclaimed")));
                }
            } else if (args[0].equalsIgnoreCase("comment") && args.length > 2) {
                String comment = String.join(" ", args).substring(13);
                if (this.comment(sender, args[1], comment)) {
                    sender.sendMessage(plugin.getMessage("ticketComment", args[1]));
                } else {
                    sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorCantComment")));
                }
            } else if (args[0].equalsIgnoreCase("tp") && args.length > 1) {
                if (!(sender instanceof Player)) {
                    return Result.UNSUPPORTED_SENDER;
                }
                Location location = tickets.get(args[1]).decodeLocation();
                TeleportUtil.teleport(command, (Player) sender, location);
                sender.sendMessage(plugin.getMessage("teleporting"));
            } else if (!sender.hasPermission("popcraft.ticket.admin")) {
                return Result.INCORRECT_USAGE;
            } else if (args[0].equalsIgnoreCase("delete") && args.length > 1) {
                List<Ticket> toDelete = new ArrayList<>();
                for (int i = 1; i < args.length; ++i) {
                    if (this.tickets.containsKey(args[i])) {
                        toDelete.add(this.tickets.get(args[i]));
                    }
                }
                try {
                    this.deleteTickets(toDelete);
                    sender.sendMessage(plugin.getMessage("ticketDelete"));
                } catch (IOException e) {
                    sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorCantDelete")));
                }
            } else if (args[0].equalsIgnoreCase("purge") && args.length > 1) {
                List<Ticket> toDelete = new ArrayList<>();
                try {
                    Date purgeDate = new Date(System.currentTimeMillis() -
                            TimeUnit.MILLISECONDS.convert(Long.parseLong(args[1]), TimeUnit.DAYS));
                    for (Ticket t : this.tickets.values()) {
                        Date ticketDate = new Date(t.getCreationDate());
                        if (ticketDate.before(purgeDate)) {
                            toDelete.add(t);
                        }
                    }
                    this.deleteTickets(toDelete);
                    sender.sendMessage(plugin.getMessage("ticketPurge", args[1]));
                } catch (NumberFormatException | IOException e) {
                    sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorCantPurge")));
                }
            } else {
                return Result.INCORRECT_USAGE;
            }
        }
        return Result.SUCCESS;
    }

    public void notifyStaff(Player player) {
        if (!player.hasPermission("popcraft.ticket.mod")) {
            return;
        }
        long count = ticketCount(true);
        if (count > 0) {
            player.sendMessage(plugin.getMessage("ticketNotify", count));
        }
    }

    private void notifyAllStaff() {
        Bukkit.broadcast(plugin.getMessage("ticketNotify", ticketCount(true)), "popcraft.ticket.mod");
    }

    private void readTickets() {
        if (!this.ticketFile.exists()) {
            return;
        }
        Type ticketListType = new TypeToken<List<Ticket>>() {
        }.getType();
        List<Ticket> ticketList;
        try {
            FileReader fileReader = new FileReader(this.ticketFile);
            ticketList = this.gson.fromJson(fileReader, ticketListType);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        this.tickets = new HashMap<>();
        for (Ticket t : ticketList) {
            this.tickets.put(t.getShortId(), t);
        }
    }

    private void writeTickets() {
        Collection<Ticket> ticketList = this.tickets.values();
        String json = this.gson.toJson(ticketList);
        try {
            FileWriter fileWriter = new FileWriter(this.ticketFile);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteTickets(List<Ticket> tickets) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ticketFileDeleted, true));
        for (Ticket t : tickets) {
            String json = this.gson.toJson(t);
            bufferedWriter.write(json);
            bufferedWriter.newLine();
            this.tickets.remove(t.getShortId());
        }
        bufferedWriter.close();
        this.writeTickets();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getMessage("ticketHelpHeader"));
        sender.sendMessage(plugin.getMessage("ticketHelpSend"));
        sender.sendMessage(plugin.getMessage("ticketHelpListArchive"));
        sender.sendMessage(plugin.getMessage("ticketHelpRead"));
        sender.sendMessage(plugin.getMessage("ticketHelpClaimUnclaim"));
        sender.sendMessage(plugin.getMessage("ticketHelpOpenClose"));
        sender.sendMessage(plugin.getMessage("ticketHelpComment"));
        sender.sendMessage(plugin.getMessage("ticketHelpTp"));
    }

    private String createTicket(CommandSender sender, String text) {
        Location l = sender instanceof Player ? ((Player) sender).getLocation()
                : sender.getServer().getWorlds().get(0).getSpawnLocation();
        Ticket ticket = new Ticket(sender.getName(), text, String.format("%s,%d,%d,%d",
                Objects.requireNonNull(l.getWorld()).getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        while (tickets.containsKey(ticket.getShortId())) {
            ticket.setUuid(UUID.randomUUID().toString());
        }
        tickets.put(ticket.getShortId(), ticket);
        this.writeTickets();
        return ticket.getShortId();
    }

    private void list(CommandSender sender, boolean open, int page) {
        List<Ticket> ticketList = tickets.values().stream()
                .filter(t -> open && t.isOpen() || !open && !t.isOpen()).collect(Collectors.toList());
        int list_length = 8;
        int pages = (int) Math.ceil((double) ticketList.size() / list_length);
        if (page < 1 || page > pages)
            page = pages;
        Collections.sort(ticketList);
        sender.sendMessage(plugin.getMessage("ticketListHeader", page, pages));
        for (int i = page * list_length - list_length; i < page * list_length && i < ticketList.size(); ++i) {
            Ticket t = ticketList.get(i);
            String assignee = t.getAssignee() == null ? "no one" : t.getAssignee(),
                    comment = t.getComments().isEmpty() ? "" : String.format(" *%d", t.getComments().size()),
                    preview = t.preview(30 - assignee.length() - comment.length());
            sender.sendMessage(plugin.getMessage("ticketListElement", t.getShortId(), assignee, preview, comment));
        }
        if (page != pages) {
            sender.sendMessage(plugin.getMessage("ticketListFooter", open ? "list " : "archive", page + 1));
        }
    }

    private void read(CommandSender sender, String id) {
        Ticket t = tickets.get(id);
        if (t == null) {
            sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorInvalidID")));
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a, MMMM dd, yyyy");
        sender.sendMessage(plugin.getMessage("ticketReadHeader", t.getShortId()));
        sender.sendMessage(plugin.getMessage("ticketReadReporter", t.getOwner()));
        if (t.getAssignee() != null) {
            sender.sendMessage(plugin.getMessage("ticketReadAssignee", t.getAssignee()));
        }
        sender.sendMessage(plugin.getMessage("ticketReadCreated",
                simpleDateFormat.format(new Date(t.getCreationDate()))));
        if (!t.isOpen()) {
            sender.sendMessage(plugin.getMessage("ticketReadResolved",
                    simpleDateFormat.format(new Date(t.getResolvedDate()))));
        }
        sender.sendMessage(plugin.getMessage("ticketReadDescription", t.getText()));
        sender.sendMessage(plugin.getMessage("ticketReadLocation", t.getLocationFormatted()));
        if (!t.getComments().isEmpty()) {
            StringBuilder commentsFormatted = new StringBuilder();
            for (String comment : t.getComments()) {
                commentsFormatted.append('\n').append(comment);
            }
            sender.sendMessage(plugin.getMessage("ticketReadComments", t.getComments().size(),
                    commentsFormatted.toString()));
        }
    }

    private void claim(CommandSender sender, String id) {
        Ticket t = tickets.get(id);
        if (t == null) {
            sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorInvalidID")));
            return;
        }
        t.setAssignee(sender.getName());
        this.writeTickets();
    }

    private boolean unclaim(CommandSender sender, String id) {
        Ticket t = tickets.get(id);
        if (t == null) {
            sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorInvalidID")));
            return false;
        }
        if (t.getAssignee() != null && t.getAssignee().equals(sender.getName())) {
            t.setAssignee(null);
            this.writeTickets();
            return true;
        }
        return false;
    }

    private boolean open(CommandSender sender, String id) {
        Ticket t = tickets.get(id);
        if (t == null) {
            sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorInvalidID")));
            return false;
        }
        if (!t.isOpen()) {
            t.setResolvedDate(0);
            this.writeTickets();
            return true;
        }
        return false;
    }

    private boolean close(CommandSender sender, String id) {
        Ticket t = tickets.get(id);
        if (t == null) {
            sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorInvalidID")));
            return false;
        }
        if (t.getAssignee() != null) {
            t.setResolvedDate(System.currentTimeMillis());
            this.writeTickets();
            return true;
        }
        return false;
    }

    private boolean comment(CommandSender sender, String id, String comment) {
        Ticket t = tickets.get(id);
        if (t == null) {
            sender.sendMessage(plugin.getMessage("error", plugin.getMessage("ticketErrorInvalidID")));
            return false;
        }
        if (t.getAssignee() != null) {
            t.getComments().add(String.format("[%s] %s", sender.getName(), comment));
            this.writeTickets();
            return true;
        }
        return false;
    }

    private long ticketCount(boolean open) {
        return tickets.values().stream().filter(t -> open && t.isOpen() || !open && !t.isOpen()).count();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("popcraft.ticket")) {
                completions.add("send");
            }
            if (sender.hasPermission("popcraft.ticket.mod")) {
                completions.addAll(Arrays.asList("list", "archive", "read", "claim", "unclaim", "open", "close", "comment", "tp"));
            }
            if (sender.hasPermission("popcraft.ticket.admin")) {
                completions.addAll(Arrays.asList("delete", "purge"));
            }
            return TabCompleteUtil.startsWithLastArg(completions, args);
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "send":
                    return null;
                case "read":
                case "claim":
                case "unclaim":
                case "open":
                case "close":
                case "comment":
                case "tp":
                case "delete":
                    completions = this.tickets.values().stream().map(Ticket::getShortId).collect(Collectors.toList());
                    return TabCompleteUtil.startsWithLastArg(completions, args);
                default:
                    break;
            }
        } else if (args[0].equalsIgnoreCase("send") || args[0].equalsIgnoreCase("comment")) {
            return null;
        } else if (args[0].equalsIgnoreCase("delete")) {
            completions = this.tickets.values().stream().map(Ticket::getShortId).collect(Collectors.toList());
            return TabCompleteUtil.startsWithLastArg(completions, args);
        }
        return completions;
    }

}

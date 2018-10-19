package org.popcraft.popcraft.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;
import org.popcraft.popcraft.utils.TicketManager;

import java.util.ArrayList;
import java.util.List;

public class TicketCommand implements CommandExecutor, TabCompleter {

    private static TicketManager tm = new TicketManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("ticket")) {
            if (args.length == 0) {
                Message.normal(player,
                        ChatColor.YELLOW + "----- " + ChatColor.GOLD + "Ticket Help" + ChatColor.YELLOW + " -----");
                if (!player.hasPermission("popcraft.ticket.mod")) {
                    Message.normal(sender, "/ticket send <message>" + ChatColor.WHITE + ": Send a ticket to staff.");
                    Message.normal(sender, "/ticket list [page]" + ChatColor.WHITE + ": View your open tickets.");
                    Message.normal(sender, "/ticket close <id>" + ChatColor.WHITE + ": Cancels your ticket.");
                } else {
                    Message.normal(sender, "/ticket send <message>" + ChatColor.WHITE + ": Send a ticket to staff.");
                    Message.normal(sender,
                            "/ticket list/archive [page]" + ChatColor.WHITE + ": View current or archived tickets.");
                    Message.normal(sender,
                            "/ticket read/view <id>" + ChatColor.WHITE + ": View detailed ticket information.");
                    Message.normal(sender,
                            "/ticket claim/unclaim <id>" + ChatColor.WHITE + ": Update responsibility for a ticket.");
                    Message.normal(sender,
                            "/ticket open/close <id>" + ChatColor.WHITE + ": Update ticket resolution status.");
                    Message.normal(sender,
                            "/ticket comment <id> [message]" + ChatColor.WHITE + ": Add or update a ticket comment.");
                    Message.normal(sender,
                            "/ticket tp <id>" + ChatColor.WHITE + ": Teleport to the ticket's location.");
                }
            } else {
                if (args[0].equalsIgnoreCase("list")) {
                    try {
                        if (tm.ticketCount(true) == 0) {
                            Message.error(player, "There are no open tickets.");
                            return true;
                        }
                        tm.list(args.length == 1 ? 1 : Integer.parseInt(args[1]), player, true);
                    } catch (NumberFormatException e) {
                        Message.error(player, "Invalid page number.");
                    }
                } else if (args[0].equalsIgnoreCase("archive")) {
                    if (player.hasPermission("popcraft.ticket.mod")) {
                        try {
                            if (tm.ticketCount(false) == 0) {
                                Message.error(player, "There are no archived tickets.");
                                return true;
                            }
                            tm.list(args.length == 1 ? -1 : Integer.parseInt(args[1]), player, false);
                        } catch (NumberFormatException e) {
                            Message.error(player, "Invalid page number.");
                        }
                    }
                } else if ((args[0].equalsIgnoreCase("read") || args[0].equalsIgnoreCase("view")) && args.length > 1) {
                    if (player.hasPermission("popcraft.ticket.mod")) {
                        try {
                            tm.view(Integer.parseInt(args[1]), player);
                        } catch (NumberFormatException e) {
                            Message.error(player, "Could not find a ticket with that ID.");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("claim") && args.length > 1) {
                    if (player.hasPermission("popcraft.ticket.mod")) {
                        try {
                            tm.claim(Integer.parseInt(args[1]), player);
                            Message.normal(player, "Claimed ticket #" + ChatColor.RED + Integer.parseInt(args[1])
                                    + ChatColor.GOLD + ".");
                        } catch (NumberFormatException e) {
                            Message.error(player, "Could not find a ticket with that ID.");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("unclaim") && args.length > 1) {
                    if (player.hasPermission("popcraft.ticket.mod")) {
                        try {
                            if (tm.unclaim(Integer.parseInt(args[1]), player))
                                Message.normal(player, "Unclaimed ticket #" + ChatColor.RED + Integer.parseInt(args[1])
                                        + ChatColor.GOLD + ".");
                            else
                                Message.error(player, "You can only unclaim your own ticket.");
                        } catch (NumberFormatException e) {
                            Message.error(player, "Could not find a ticket with that ID.");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("open") && args.length > 1) {
                    if (player.hasPermission("popcraft.ticket.mod")) {
                        try {
                            if (tm.open(Integer.parseInt(args[1])))
                                Message.normal(player, "Opened ticket #" + ChatColor.RED + Integer.parseInt(args[1])
                                        + ChatColor.GOLD + ".");
                            else
                                Message.error(player, "That ticket is already open!");
                        } catch (NumberFormatException e) {
                            Message.error(player, "Could not find a ticket with that ID.");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("close") && args.length > 1) {
                    try {
                        if (tm.getTicket(Integer.parseInt(args[1])).getOwner().equals(player.getName())) {
                            if (tm.delete(Integer.parseInt(args[1])))
                                Message.normal(player, "Closed ticket #" + ChatColor.RED + Integer.parseInt(args[1])
                                        + ChatColor.GOLD + ".");
                            else
                                Message.error(player, "Unable to close the ticket.");
                        } else if (player.hasPermission("popcraft.ticket.mod")) {
                            if (tm.close(Integer.parseInt(args[1]), player))
                                Message.normal(player, "Closed ticket #" + ChatColor.RED + Integer.parseInt(args[1])
                                        + ChatColor.GOLD + ".");
                            else
                                Message.error(player, "You cannot close an unclaimed ticket.");
                        } else {
                            Message.error(player, "Unable to close the ticket.");
                        }
                    } catch (NumberFormatException e) {
                        Message.error(player, "Could not find a ticket with that ID.");
                    }
                } else if (args[0].equalsIgnoreCase("comment") && args.length > 1) {
                    if (player.hasPermission("popcraft.ticket.mod")) {
                        try {
                            if (args.length == 2) {
                                if (tm.comment(Integer.parseInt(args[1]), player, null))
                                    Message.normal(player, "Removed comments on ticket #" + ChatColor.RED
                                            + Integer.parseInt(args[1]) + ChatColor.GOLD + ".");
                                else
                                    Message.error(player, "Comments can only be removed by the assignee.");
                                return true;
                            }
                            StringBuilder comment = new StringBuilder();
                            for (int i = 2; i < args.length - 1; i++)
                                comment.append(args[i] + " ");
                            comment.append(args[args.length - 1]);
                            if (tm.comment(Integer.parseInt(args[1]), player, comment.toString()))
                                Message.normal(player, "Commented on ticket #" + ChatColor.RED
                                        + Integer.parseInt(args[1]) + ChatColor.GOLD + ".");
                            else
                                Message.error(player, "Comments can only be made by the assignee.");
                        } catch (NumberFormatException e) {
                            Message.error(player, "Could not find a ticket with that ID.");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("tp") && args.length > 1) {
                    if (player.hasPermission("popcraft.ticket.mod")) {
                        player.teleport(tm.getTicket(Integer.parseInt(args[1])).decodeLocation());
                        Message.normal(player, "Teleporting...");
                    }
                } else if (args[0].equalsIgnoreCase("purge") && args.length > 1) {
                    if (player.hasPermission("popcraft.ticket.admin")) {
                        try {
                            if (tm.purge(Integer.parseInt(args[1])))
                                Message.normal(player, "Purged tickets older than " + ChatColor.RED
                                        + Integer.parseInt(args[1]) + ChatColor.GOLD + " days.");
                            else
                                Message.error(player, "Unable to purge tickets.");
                        } catch (NumberFormatException e) {
                            Message.error(player, "Invalid token.");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("delete") && args.length > 1) {
                    if (player.hasPermission("popcraft.ticket.admin")) {
                        try {
                            if (args.length == 2 ? tm.delete(Integer.parseInt(args[1]))
                                    : tm.delete(Integer.parseInt(args[1]), Integer.parseInt(args[2])))
                                Message.normal(player, "Deleted ticket" + (args.length == 2 ? " #" : "s in the range ")
                                        + ChatColor.RED + Integer.parseInt(args[1])
                                        + (args.length == 2 ? ""
                                        : ChatColor.GOLD + " to " + ChatColor.RED + Integer.parseInt(args[2]))
                                        + ChatColor.GOLD + ".");
                            else
                                Message.error(player, "Unable to delete tickets.");
                        } catch (NumberFormatException e) {
                            Message.error(player, "Invalid tokens.");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("instantiate")) {
                    if (player.hasPermission("popcraft.ticket.admin")) {
                        tm = new TicketManager();
                        Message.normal(player, "Reloaded the ticket manager.");
                    }
                } else if (args[0].equalsIgnoreCase("send") && args.length > 1) {
                    StringBuilder text = new StringBuilder();
                    for (int i = 1; i < args.length - 1; i++)
                        text.append(args[i] + " ");
                    text.append(args[args.length - 1]);
                    int id = tm.createTicket(player, text.toString());
                    Message.normal(player, "Successfully created ticket #" + ChatColor.RED + id + ChatColor.GOLD + ".");
                    for (Player p : Bukkit.getOnlinePlayers())
                        if (p.hasPermission("popcraft.ticket.mod"))
                            Message.normal(p,
                                    "There are " + ChatColor.RED + tm.ticketCount(true) + ChatColor.GOLD
                                            + " tickets open! Type " + ChatColor.RED + "/ticket list" + ChatColor.GOLD
                                            + " to view them.");
                } else {
                    Message.usage(player, "ticket");
                }
            }
            return true;
        }
        return false;
    }

    public static TicketManager getTicketManager() {
        return tm;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("send");
            options.add("list");
            options.add("close");
            if (sender.hasPermission("popcraft.ticket.mod")) {
                options.add("archive");
                options.add("read");
                options.add("view");
                options.add("claim");
                options.add("unclaim");
                options.add("open");
                options.add("comment");
                options.add("tp");
            }
        }
        List<String> finalOptions = new ArrayList<>();
        for (String option : options) {
            if (option.contains(args[args.length - 1])) {
                finalOptions.add(option);
            }
        }
        return finalOptions;
    }
}

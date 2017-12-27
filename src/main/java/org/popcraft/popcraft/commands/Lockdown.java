package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.popcraft.popcraft.newCode.PopCommand;

import static java.lang.String.*;
import static net.md_5.bungee.api.ChatColor.*;

@PopCommand("lockdown")
public class Lockdown implements CommandExecutor, Listener {

    private boolean locked = false;

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        locked = !locked;
        sender.sendMessage(format("%sLockdown %s%s%s.", GOLD, RED, locked ? "enabled" : "disabled", GOLD));
        return true;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
        if (this.locked)
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, org.bukkit.ChatColor.GREEN + "PopCraft" + org.bukkit.ChatColor.RESET
                    + "\n\nServer temporarily unavailable. Please try again later!");
    }

    @EventHandler
    public void onServerListPing(final ServerListPingEvent e) {
        if (this.locked)
            e.setMaxPlayers(0);
        else if (e.getMaxPlayers() > Bukkit.getMaxPlayers())
            e.setMaxPlayers(e.getNumPlayers());
    }

}
package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import io.vavr.collection.Array;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.popcraft.popcraft.newCode.PopCommand;

import static java.lang.String.format;
import static org.bukkit.ChatColor.*;

@PopCommand("pop")
public class Pop implements CommandExecutor {

    private final Server server;

    @Inject
    public Pop(final Server server) {
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final String msg = Array.of(format("%s%sOwner%s %spop4959%s:", DARK_GREEN, BOLD, RESET, DARK_GREEN, RESET))
                .appendAll(Array.of(args))
                .map(line -> translateAlternateColorCodes('&', line))
                .mkString(" ");
        this.server.broadcastMessage(msg);
        return true;
    }
}
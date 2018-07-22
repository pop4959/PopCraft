package org.popcraft.popcraft.commands;

import io.vavr.collection.Array;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;

public abstract class FakeChatCommand implements CommandExecutor {

    private final Server server;

    public FakeChatCommand(final Server server) {
        this.server = server;
    }

    @Override
    public final boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        this.server.broadcastMessage(makeMessage(this.getHeader(), args));
        return true;
    }

    public abstract String getHeader();

    public static String makeMessage(final String header, final String[] args) {
        return Array.of(header)
                .appendAll(Array.of(args))
                .map(line -> translateAlternateColorCodes('&', line))
                .mkString(" ");
    }

}

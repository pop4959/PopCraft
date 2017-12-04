package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.newCode.PopCommand;
import org.popcraft.popcraft.utils.Message;

import java.util.function.Function;

import static java.lang.String.format;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

@PopCommand("spoof")
public class Spoof implements CommandExecutor {

    private static final Map<String, Function<Player, String>> spoofMapping = HashMap.of(
            "join", p -> format("%s\u2714%s", GREEN, p.getName()),
            "quit", p -> format("%s\u2715%s", GREEN, p.getName()),
            "afk", p -> format("%s* %s%s is now AFK.", GRAY, p.getDisplayName(), GRAY),
            "no-afk", p -> format("%s* %s%s is no longer AFK", GRAY, p.getDisplayName(), GRAY)
    );
    private final Server server;

    @Inject
    public Spoof(final Server server) {
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player) && args.length == 1) {
            final Option<Function<Player, String>> possibleCommand = spoofMapping.get(args[0]);
            if (possibleCommand.isDefined()) {
                server.broadcastMessage(possibleCommand.get().apply((Player) sender));
                return true;
            }
        }
        return false;
    }
}
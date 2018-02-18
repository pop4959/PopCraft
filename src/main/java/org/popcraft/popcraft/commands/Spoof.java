package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.PopCommand;

import java.util.function.Function;

import static java.lang.String.format;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

@PopCommand("spoof")
public class Spoof extends PlayerCommand {

    private static final Map<String, Function<Player, String>> spoofMapping = HashMap.of(
            "join", p -> format("%s\u2714 %s", GREEN, p.getName()),
            "quit", p -> format("%s\u2715 %s", GREEN, p.getName()),
            "afk", p -> format("%s* %s%s is now AFK.", GRAY, p.getDisplayName(), GRAY),
            "no-afk", p -> format("%s* %s%s is no longer AFK.", GRAY, p.getDisplayName(), GRAY)
    );
    private final Server server;

    @Inject
    public Spoof(final Server server) {
        this.server = server;
    }

    @Override
    public boolean onPlayerCommand(Player sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }
        final Option<Function<Player, String>> possibleCommand = spoofMapping.get(args[0]);
        if (possibleCommand.isDefined()) {
            server.broadcastMessage(possibleCommand.get().apply(sender));
            return true;
        }
        return false;
    }

    @Override
    public boolean playerCheck(Player player) {
        return true;
    }

}
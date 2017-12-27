package org.popcraft.popcraft.commands;

import com.google.common.collect.Range;
import com.google.inject.Inject;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.newCode.PopCommand;

import static java.lang.String.format;
import static org.bukkit.ChatColor.*;
import static org.popcraft.popcraft.commands.FakeChatCommand.makeMessage;
import static org.popcraft.popcraft.utils.Message.normal;

@PopCommand("staff")
public class Staff implements CommandExecutor {

    private final Server server;

    @Inject
    public Staff(final Server server) {
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        final String message = makeMessage(
                format(
                        "%s%sStaff %s%s%s:",
                        GREEN,
                        BOLD,
                        RESET,
                        sender instanceof Player ? ((Player) sender).getDisplayName() : "[Server]",
                        RESET
                ),
                args
        );

        this.server.getOnlinePlayers()
                .stream()
                .filter(p -> p.hasPermission("popcraft.staff"))
                .forEach(p -> normal(p, message));
        return true;
    }
}
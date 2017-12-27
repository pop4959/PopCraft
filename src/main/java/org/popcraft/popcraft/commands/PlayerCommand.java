package org.popcraft.popcraft.commands;

import com.google.common.collect.Range;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.utils.Message;

/**
 * Created by Jonny on 12/4/17.
 */
public abstract class PlayerCommand implements CommandExecutor {

    @Override
    public final boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            Message.error(commandSender, "Command can only be sent by a player");
            return false;
        }
        final Player player = (Player) commandSender;
        return this.playerCheck(player) && this.onPlayerCommand(player, command, label, args);
    }

    public boolean playerCheck(final Player player) {
        return true;
    }

    public abstract boolean onPlayerCommand(Player player, Command command, String label, String[] args);

}
